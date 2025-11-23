package com.example.test;

import com.example.test.sharedfiles.model.Slot;

import org.junit.Test;

import static org.junit.Assert.*;

public class SlotTest {

    @Test
    public void isPastAndOverlaps() {

        Slot pastSlot = new Slot("tutor1", "2024-01-01", "09:00", "10:00", false, true);
        Slot futureSlot = new Slot("tutor1", "2026-01-01", "09:00", "10:00", false, true);

        //checks if slot is past
        assertTrue("It is a past slot", pastSlot.isPast());
        assertFalse("Not a past slot", futureSlot.isPast());


        Slot slot = new Slot("tutor1", "2025-06-01", "10:00", "11:00", false, true);

        // Overlapping slot
        Slot overlapping = new Slot("tutor1", "2025-06-01", "10:30", "11:30", false, true);

        // not overlapping
        Slot notOverlapping = new Slot("tutor1", "2025-06-01", "11:00", "12:00", false, true);

        // Overlapping tests
        assertTrue("slots should overlap", slot.overlaps(overlapping));
        assertFalse("slots should not overlap", slot.overlaps(notOverlapping));
    }
}
