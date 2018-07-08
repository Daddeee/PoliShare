package it.polimi.polishare.peer.network.download;

import it.polimi.polishare.peer.model.Note;

import java.util.concurrent.atomic.AtomicInteger;

public class Download {
    private Note note;
    private byte[] fileBytes;
    private AtomicInteger receivedChunks;
    private final int chunksNumber;
    private final int chunkSize;
    private final int size;
    private String md5;
    private boolean isStarted;

    private Thread downloadThread;

    public Download(Note note, int size, int chunksNumber, int chunkSize, String md5) {
        this.note = note;
        this.fileBytes = new byte[size];
        this.receivedChunks = new AtomicInteger(0);
        this.chunksNumber = chunksNumber;
        this.chunkSize = chunkSize;
        this.size = size;
        this.md5 = md5;
        this.isStarted = false;

        this.downloadThread = new Thread(() -> DownloadManager.start(this));
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

    public double getReceivedPercentage() {
        return (double)receivedChunks.get()/(double)chunksNumber;
    }

    public boolean isStarted() {
        return isStarted;
    }

    public Note getNote() {
        return note;
    }

    public int getChunkSize() {
        return chunkSize;
    }

    public int getChunksNumber() {
        return chunksNumber;
    }

    public int getSize() {
        return size;
    }

    public String getMd5() {
        return md5;
    }

    public void start() {
        isStarted = true;
        downloadThread.start();
    }

    public void stop() {
        downloadThread.stop();
    }
}
