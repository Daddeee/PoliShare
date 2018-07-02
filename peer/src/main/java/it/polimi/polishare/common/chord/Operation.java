package it.polimi.polishare.common.chord;

import java.io.Serializable;

public interface Operation extends Serializable {
    void execute(Object o);
}
