package it.polimi.polishare.peer.utils;

import it.polimi.polishare.common.*;
import it.polimi.polishare.common.DHT.DHTException;
import it.polimi.polishare.peer.App;
import it.polimi.polishare.peer.model.Note;
import it.polimi.polishare.peer.model.NoteDAO;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class DownloadManager {
    private static final int DEFAULT_CHUNKS_NUMBER = 50;
    private static HashMap<String, DownloadData> activeDownloads = new HashMap<>();

    public static void download(Note note) {
        Downloader master = getMaster(note.getNoteMetaData());
        if (master == null) throw new RuntimeException("Non è stata trovata nessuna sorgente disponibile al download.");

        int size;
        String md5;
        try {
            size = master.getSize(note.getTitle());
            md5 = master.getMD5(note.getTitle());
        } catch (RemoteException e) {
            throw new RuntimeException("Errore generico.");
        }

        int chunkSize = (size < DEFAULT_CHUNKS_NUMBER) ? 1 : size / DEFAULT_CHUNKS_NUMBER;
        int chunksNumber;
        if (chunkSize == 1) {
            chunksNumber = size;
        } else {
            chunksNumber = (size % chunkSize > 0) ? DEFAULT_CHUNKS_NUMBER + 1 : DEFAULT_CHUNKS_NUMBER;
        }

        DownloadData downloadData = new DownloadData(size, chunksNumber, md5);
        activeDownloads.put(note.getTitle(), downloadData);

        for (int i = 0; i < chunksNumber; i++) {
            int from = i * chunkSize;
            int to = (from + chunkSize - 1 >= size) ? size - 1 : from + chunkSize - 1;

            Thread downloader = new Thread(() -> {
                try {
                    byte[] chunk = master.getChunk(note.getTitle(), from, to);
                    downloadData.addChunk(chunk, from, to);
                } catch (RemoteException e) {
                }

            });
            downloader.start();
            try {
                downloader.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        try {
            File file = new File(note.getPath());
            BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(file.getName()));
            output.write(downloadData.getFileBytes(), 0, size);
            output.flush();
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            CurrentSession.getDHT().exec(note.getTitle(), new AddOwnerOperation(App.dw));
            new NoteDAO().create(note);
        } catch (DHTException | AddFailedException e) {
            e.printStackTrace();
        }
    }



    private static Downloader getMaster(NoteMetaData noteMetaData) {
        for(Downloader d : noteMetaData.getOwners()){
            try{
                d.ping();
                return d;
            } catch (RemoteException e) {}
        }

        return null;
    }

    private static class DownloadData {
        private byte[] fileBytes;
        private AtomicInteger receivedChunks;
        private final int chunksNumber;
        private String md5;

        public DownloadData(int size, int chunksNumber, String md5) {
            this.fileBytes = new byte[size];
            this.receivedChunks = new AtomicInteger(0);
            this.chunksNumber = chunksNumber;
            this.md5 = md5;
        }

        public void addChunk(byte[] chunk, int from, int to) {
            int length = to - from + 1;
            for(int i = 0; i < length; i++) {
                fileBytes[from + i] = chunk[i];
            }
            receivedChunks.addAndGet(1);
        }

        public byte[] getFileBytes() {
            return fileBytes;
        }
    }
}
