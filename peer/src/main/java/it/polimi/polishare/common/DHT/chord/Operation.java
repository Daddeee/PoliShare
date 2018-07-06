package it.polimi.polishare.common.DHT.chord;

import java.io.Serializable;

public interface Operation extends Serializable {
    void execute(Object o);
}
