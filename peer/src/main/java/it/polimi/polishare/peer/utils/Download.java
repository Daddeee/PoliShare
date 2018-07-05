package it.polimi.polishare.peer.utils;

import java.util.concurrent.atomic.AtomicInteger;

public class Download {
    private byte[] fileBytes;
    private AtomicInteger receivedChunks;
    private final int chunksNumber;
    private String md5;

    public Download(int size, int chunksNumber, String md5) {
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

    public double getReceivedQuantity() {
        return (double)receivedChunks.get()/(double)chunksNumber;
    }
}
