package it.polimi.polishare.server.utils;


import org.junit.jupiter.api.Test;

import static org.junit.Assert.*;

public class RandomStringTest {
    @Test
    public void nextString() {
        final int length = 10;
        final RandomString randomString = new RandomString(length);

        String result = randomString.nextString();

        assert(result != null && result.length() == length);
    }
}