package it.polimi.polishare.common;

import it.polimi.polishare.common.Downloader;
import it.polimi.polishare.common.NoteMetaData;
import it.polimi.polishare.common.chord.Operation;

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