package it.polimi.polishare.common.DHT.chord;

import java.io.Serializable;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Key implements Comparable<Key>, Serializable {
    public static final int m = 160;
    public static final BigInteger two = new BigInteger("2");
    public static final BigInteger greaterValue = two.pow(m);

    private BigInteger key;

    public Key(){}

    public Key(String value){
        try {
            this.key = sha1(value);
        } catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        }
    }

    public void setKey(BigInteger key) {
        this.key = key;
    }

    public boolean isRingBetween(Key from, Key to) {
        if(from.compareTo(to) < 0)
            return from.compareTo(this) < 0 && this.compareTo(to) < 0;
        else
            return from.compareTo(this) < 0 || this.compareTo(to) < 0;
    }

    @Override
    public int compareTo(Key key) {
        return this.key.compareTo(key.key);
    }

    public Key sumPowerOf2(int exponent) {
        Key summed = new Key();
        BigInteger summedKey = this.key.add(two.pow(exponent)).mod(greaterValue);
        summed.setKey(summedKey);

        return summed;
    }

    @Override
    public boolean equals(Object o) {
        if(o == this) return true;
        if(!(o instanceof Key)) return false;
        return ((Key) o).key.equals(this.key);
    }

    public static BigInteger sha1(String value) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA1");
        digest.reset();
        byte[] input = digest.digest(value.getBytes());

        return new BigInteger(1,input);
    }
}
