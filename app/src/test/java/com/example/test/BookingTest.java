package com.example.test;

import org.junit.Test;

import static org.junit.Assert.*;

public class BookingTest {

    private boolean canCancel(long sessionStart, long now) {
        long sessionDifference = sessionStart - now;
        long hours = sessionDifference / (1000L * 60L * 60L);
        return hours >= 24;
    }

    public void testCancel() {

        long now = 0L;

        // Session starting less than 24 hours
        long start5Hours = 5L * 60L * 60L * 1000L;

        // Session starting more than 24 hours
        long start30Hours = 30L * 60L * 60L * 1000L;

        // Asserts that sessions starting in less than 24 hours can't be cancelled
        assertFalse("returns false", canCancel(start5Hours, now));

        // Asserts that sessions starting in less than or equal to 24 hours can be cancelled
        assertTrue("return true ", canCancel(start30Hours, now));
    }
}
