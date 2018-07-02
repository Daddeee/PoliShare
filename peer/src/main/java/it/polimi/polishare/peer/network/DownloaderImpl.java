package it.polimi.polishare.peer.network;

import it.polimi.polishare.common.Downloader;
import it.polimi.polishare.peer.App;
import it.polimi.polishare.peer.model.Note;
import it.polimi.polishare.peer.model.NoteDAO;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class DownloaderImpl extends UnicastRemoteObject implements Downloader {
    private static final int RMI_DEFAULT_PORT = 1099;

    private NoteDAO noteDAO;

    public DownloaderImpl() throws RemoteException {
        this.noteDAO = new NoteDAO();
        startRMI();
    }

    @Override
    public byte[] download(String title) {
        try {
            Note toDownload = noteDAO.read(title);
            File file = new File(toDownload.getPath());
            byte buffer[] = new byte[(int)file.length()];
            BufferedInputStream input = new BufferedInputStream(new FileInputStream(toDownload.getPath()));
            input.read(buffer,0,buffer.length);
            input.close();
            return buffer;
        } catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void ping() {
        System.out.println("Received ping.");
    }

    private void startRMI() throws RemoteException {
        Registry registry;
        try {
            registry = LocateRegistry.createRegistry(RMI_DEFAULT_PORT);
        } catch (Exception e) {
            registry = LocateRegistry.getRegistry(RMI_DEFAULT_PORT);
        }
        registry.rebind("downloader_" + App.USERNAME, this);
    }
}
