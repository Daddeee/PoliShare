package it.polimi.polishare.peer.network;

import it.polimi.polishare.common.Downloader;
import it.polimi.polishare.common.NoteMetaData;
import it.polimi.polishare.common.chord.Operation;

public class RemoveOwnerOperation implements Operation {
    private Downloader owner;

    public RemoveOwnerOperation(Downloader owner) {
        this.owner = owner;
    }

    @Override
    public void execute(Object o) {
        ((NoteMetaData) o).removeOwner(owner);
    }
}