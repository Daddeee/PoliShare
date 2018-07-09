package it.polimi.polishare.peer.network.download;

import it.polimi.polishare.common.DHT.model.NoteMetaData;
import it.polimi.polishare.peer.CurrentSession;
import it.polimi.polishare.peer.model.Note;
import it.polimi.polishare.peer.utils.DB;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.RemoteException;

import static org.junit.jupiter.api.Assertions.*;

class DownloadManagerTest {
    private static MockDownloader downloader1;
    private static MockDownloader downloader2;
    private static MockDownloader downloader3;

    @BeforeAll
    public static void setUp() {
        DownloadManagerTest.downloader1 = new MockDownloader("d1");
        DownloadManagerTest.downloader2 = new MockDownloader("d2");
        DownloadManagerTest.downloader3 = new MockDownloader("d3");
    }


    @Test
    void register() throws RemoteException {
        NoteMetaData noteMetaData = new NoteMetaData("Titolo", "Autore", "Materia", "Professore", 2018);
        noteMetaData.addOwner(downloader1);
        Note note = new Note(noteMetaData.getTitle(), "path");
        note.setNoteMetaData(noteMetaData);

        DownloadManager.register(note);

        assertEquals(DownloadManager.getActiveDownloads().size(), 1);

        Download d = DownloadManager.getActiveDownloads().get("Titolo");
        assertEquals(d.getNote(), note);
        assertEquals(d.getReceivedPercentage(), 0);
        assertFalse(d.isStarted());
        assertEquals(d.getSize(), downloader1.getSize("Titolo"));
        assertEquals(d.getMd5(), downloader1.getMD5("Titolo"));

        DownloadManager.removeActiveDownload("Titolo");
    }

    @Test
    void registerFailsIfNoDownloaderIsFound() throws RemoteException {
        NoteMetaData noteMetaData = new NoteMetaData("Titolo", "Autore", "Materia", "Professore", 2018);
        Note note = new Note(noteMetaData.getTitle(), "path");
        note.setNoteMetaData(noteMetaData);

        try {
            DownloadManager.register(note);
            fail("Exception not thrown");
        } catch (Exception e) {}
    }

    @Test
    void downloadFile() throws IOException, NoSuchFieldException, IllegalAccessException {
        NoteMetaData noteMetaData = new NoteMetaData("Titolo", "Autore", "Materia", "Professore", 2018);
        noteMetaData.addOwner(downloader1);
        noteMetaData.addOwner(downloader2);
        noteMetaData.addOwner(downloader3);

        Note note = new Note(noteMetaData.getTitle(), "titolo.pdf");
        note.setNoteMetaData(noteMetaData);

        DownloadManager.register(note);

        //This will fail the creation of the file at data level
        DB.setDbName("test.db");
        CurrentSession.setDHT(null);

        int size = DownloadManager.getActiveDownloads().get("Titolo").getChunkSize();

        DownloadManager.start(DownloadManager.getActiveDownloads().get("Titolo"));

        File file = new File("titolo.pdf");
        byte[] buffer = new byte[(int)file.length()];
        BufferedInputStream input = new BufferedInputStream(new FileInputStream("titolo.pdf"));
        input.read(buffer,0,buffer.length);
        input.close();

        assertEquals(downloader1.getSize("Titolo"), buffer.length);
        assertEquals(downloader1.getMD5("Titolo"), DigestUtils.md5Hex(buffer));

        Field f = DownloadManager.class.getDeclaredField("DEFAULT_CHUNKS_NUMBER");
        f.setAccessible(true);

        assertTrue(downloader1.getSentChunks() > 0);
        assertTrue(downloader2.getSentChunks() > 0);
        assertTrue(downloader3.getSentChunks() > 0);
        assertEquals(downloader1.getSentChunks() + downloader2.getSentChunks() + downloader3.getSentChunks(), (int)f.get(null));
    }

    @AfterAll
    public static void tearDown() throws IOException {
        Files.deleteIfExists(Paths.get("titolo.pdf"));
    }
}