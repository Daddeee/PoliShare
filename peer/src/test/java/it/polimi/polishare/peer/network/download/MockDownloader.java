package it.polimi.polishare.peer.network.download;

import it.polimi.polishare.common.download.Downloader;
import org.apache.commons.codec.digest.DigestUtils;

import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class MockDownloader implements Downloader {
    private String username;
    private byte[] mockfile;
    private String md5;
    private int size;
    private AtomicInteger sentChunks;

    public MockDownloader(String username) {
        this.username = username;
        this.size = 1000;
        prepareMockFile();
        this.md5 = DigestUtils.md5Hex(this.mockfile);
        this.sentChunks = new AtomicInteger(0);
    }

    private void prepareMockFile() {
        this.mockfile = new byte[this.size];
        for(int i = 0; i < this.size; i++)
            this.mockfile[i] = (byte)(i%255);
    }

    @Override
    public byte[] getChunk(String title, int from, int to) throws RemoteException {
        sentChunks.addAndGet(1);
        return Arrays.copyOfRange(mockfile, from, to + 1);
    }

    public int getSentChunks() {
        return sentChunks.get();
    }

    @Override
    public String getMD5(String title) throws RemoteException {
        return md5;
    }

    @Override
    public int getSize(String title) throws RemoteException {
        return size;
    }

    @Override
    public void ping() throws RemoteException {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MockDownloader that = (MockDownloader) o;
        return Objects.equals(username, that.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }
}
