package it.polimi.polishare.peer.network.download;

import it.polimi.polishare.common.download.Downloader;
import it.polimi.polishare.peer.model.Note;
import it.polimi.polishare.peer.model.NoteDAO;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;

public class DownloaderImpl extends UnicastRemoteObject implements Downloader {
    private NoteDAO noteDAO;
    private String username;

    public DownloaderImpl(String username) throws RemoteException {
        this.noteDAO = new NoteDAO();
        this.username = username;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getMD5(String title) {
        try {
            byte[] fileBytes = readFile(title);
            return DigestUtils.md5Hex(fileBytes);
        } catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public int getSize(String title) {
        Note toDownload = noteDAO.read(title);
        File file = new File(toDownload.getPath());
        return (int)file.length();
    }

    @Override
    public byte[] getChunk(String title, int from, int to) {
        try {
            Thread.sleep(1000);
            byte[] fileBytes = readFile(title);
            return Arrays.copyOfRange(fileBytes, from, to + 1);
        } catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    private byte[] readFile(String title) throws IOException {
        Note toDownload = noteDAO.read(title);
        File file = new File(toDownload.getPath());

        byte[] buffer = new byte[(int)file.length()];
        BufferedInputStream input = new BufferedInputStream(new FileInputStream(toDownload.getPath()));
        input.read(buffer,0,buffer.length);
        input.close();

        return buffer;
    }

    @Override
    public void ping() {}
}
