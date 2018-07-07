package it.polimi.polishare.common.DHT.chord;

import java.io.Serializable;

/**
 * Represents an operation that can be performed on any element of the DHT.
 */
public interface Operation extends Serializable {
    /**
     * Performs the operation on the given element.
     *
     * @param o
     */
    void execute(Object o);
}
