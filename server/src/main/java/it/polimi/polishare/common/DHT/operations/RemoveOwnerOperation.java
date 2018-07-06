package it.polimi.polishare.common.DHT.operations;

import it.polimi.polishare.common.DHT.chord.Operation;
import it.polimi.polishare.common.DHT.model.NoteMetaData;
import it.polimi.polishare.common.download.Downloader;

import java.io.Serializable;

public class RemoveOwnerOperation implements Operation, Serializable {
    private Downloader owner;

    public RemoveOwnerOperation(Downloader owner) {
        this.owner = owner;
    }

    @Override
    public void execute(Object o) {
        ((NoteMetaData) o).removeOwner(owner);
    }
}