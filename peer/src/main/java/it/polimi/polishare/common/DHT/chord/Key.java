package it.polimi.polishare.common.DHT.chord;

import java.io.Serializable;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * This class represents a key at chord level.
 * All keys at chord's level can be seen as natural numbers belonging to the interval [0, 2^m).
 * It provides also methods for key's comparison.
 */
public class Key implements Comparable<Key>, Serializable {
    /**
     * The parameter m represents the maximum exponent such as (2^m)-1 is a valid key for the ring.
     */
    public static final int m = 160;

    private static final BigInteger two = new BigInteger("2");
    private static final BigInteger greatestValue = two.pow(m);

    private BigInteger key;

    private Key(){}

    /**
     * Creates a new key based on the provided string key.
     * @param stringKey
     */
    public Key(String stringKey){
        try {
            this.key = sha1(stringKey);
        } catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        }
    }

    private void setKey(BigInteger key) {
        this.key = key;
    }

    /**
     * Check if this key belongs to the given interval on the chord ring excluding extremes and taking into account the
     * possible circularity of the interval.
     *
     * @param from start point of the interval
     * @param to end point of the interval
     * @return true if this key belongs to the given interval on the chord ring, false otherwise.
     */
    public boolean isRingBetween(Key from, Key to) {
        if(from.compareTo(to) < 0)
            return from.compareTo(this) < 0 && this.compareTo(to) < 0;
        else
            return from.compareTo(this) < 0 || this.compareTo(to) < 0;
    }

    /**
     * Compares this Key with the specified Key for order.
     * @param key Key to be compared.
     * @return a negative integer, zero, or a positive integer as this Key is less than, equal to, or greater than the
     *         given Object.
     */
    @Override
    public int compareTo(Key key) {
        return this.key.compareTo(key.key);
    }

    /**
     * Returns a new key whose value is equal to that of this key plus 2^exponent.
     *
     * @param exponent the exponent to be summed.
     * @return
     */
    public Key sumPowerOf2(int exponent) {
        Key summed = new Key();
        BigInteger summedKey = this.key.add(two.pow(exponent)).mod(greatestValue);
        summed.setKey(summedKey);

        return summed;
    }

    @Override
    public boolean equals(Object o) {
        if(o == this) return true;
        if(!(o instanceof Key)) return false;
        return ((Key) o).key.equals(this.key);
    }

    private static BigInteger sha1(String value) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA1");
        digest.reset();
        byte[] input = digest.digest(value.getBytes());

        return new BigInteger(1,input);
    }
}
