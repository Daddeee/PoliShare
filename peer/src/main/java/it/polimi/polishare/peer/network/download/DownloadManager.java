package it.polimi.polishare.peer.network.download;

import it.polimi.polishare.common.*;
import it.polimi.polishare.common.DHT.DHTException;
import it.polimi.polishare.common.DHT.model.NoteMetaData;
import it.polimi.polishare.common.DHT.operations.AddOwnerOperation;
import it.polimi.polishare.common.download.Downloader;
import it.polimi.polishare.peer.App;
import it.polimi.polishare.peer.CurrentSession;
import it.polimi.polishare.peer.controller.DownloadController;
import it.polimi.polishare.peer.model.Note;
import it.polimi.polishare.peer.model.NoteDAO;
import javafx.application.Platform;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DownloadManager {
    private static DownloadController downloadController;
    private static final int DEFAULT_CHUNKS_NUMBER = 50;
    private static HashMap<String, Download> activeDownloads = new HashMap<>();

    public static HashMap<String, Download> getActiveDownloads() {
        return activeDownloads;
    }

    public static void removeActiveDownload(String title) {
        activeDownloads.remove(title);
    }

    public static void setDownloadController(DownloadController downloadController) {
        DownloadManager.downloadController = downloadController;
    }

    public static void register(Note note) {
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

        int chunkSize = calculateChunkSize(size);
        int chunksNumber = calculateChunksNumber(size, chunkSize);

        Download download = new Download(note, size, chunksNumber, chunkSize, md5);
        activeDownloads.put(note.getTitle(), download);
    }

    public static void start(Download download) {
        List<Downloader> activeDownloaders = getActiveDownloaders(download.getNote().getNoteMetaData());

        for (int i = 0; i < download.getChunksNumber(); i++) {
            int from = i * download.getChunkSize();
            int to = (from + download.getChunkSize() - 1 >= download.getSize()) ? download.getSize() - 1 : from + download.getChunkSize() - 1;

            Downloader d = activeDownloaders.get(i%activeDownloaders.size());
            Thread downloader = new Thread(() -> {
                Downloader current = d;
                int k = 0;
                while (true) {
                    try {
                        byte[] chunk = current.getChunk(download.getNote().getTitle(), from, to);
                        download.addChunk(chunk, from, to);

                        if(downloadController != null)
                            Platform.runLater(() -> downloadController.updateDownload(download.getNote().getTitle(), download));

                        return;
                    } catch (RemoteException e) {
                        if(k >= activeDownloaders.size()) return;
                        current = activeDownloaders.get(k);
                        k++;
                    }
                }

            });
            downloader.start();
            try {
                downloader.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        String downloadedMD5 = DigestUtils.md5Hex(download.getFileBytes());
        if(!downloadedMD5.equals(download.getMd5())) throw new RuntimeException("Il file ricevuto non corrisponde al file remoto");

        try {
            File file = new File(download.getNote().getPath());
            BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(file.getName()));
            output.write(download.getFileBytes(), 0, download.getSize());
            output.flush();
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            CurrentSession.getDHT().exec(download.getNote().getTitle(), new AddOwnerOperation(App.dw));
            new NoteDAO().create(download.getNote());
        } catch (Exception e) {
            e.printStackTrace();
        }

        activeDownloads.remove(download.getNote().getTitle());
    }

    private static int calculateChunksNumber(int size, int chunkSize) {
        int chunksNumber;
        if (chunkSize == 1) {
            chunksNumber = size;
        } else {
            chunksNumber = (size % chunkSize > 0) ? DEFAULT_CHUNKS_NUMBER + 1 : DEFAULT_CHUNKS_NUMBER;
        }
        return chunksNumber;
    }

    private static int calculateChunkSize(int size) {
        return (size < DEFAULT_CHUNKS_NUMBER) ? 1 : size / DEFAULT_CHUNKS_NUMBER;
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

    private static List<Downloader> getActiveDownloaders(NoteMetaData noteMetaData) {
        List<Downloader> activeDownloaders = new ArrayList<>();
        for(Downloader d : noteMetaData.getOwners()){
            try{
                d.ping();
                activeDownloaders.add(d);
            } catch (RemoteException e) {}
        }

        return activeDownloaders;
    }
}
