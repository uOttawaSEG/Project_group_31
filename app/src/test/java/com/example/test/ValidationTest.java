package com.example.test;

import com.example.test.sharedfiles.model.Slot;

import org.junit.Test;

import java.util.ArrayList;

import java.util.List;

import static org.junit.Assert.*;

public class ValidationTest {

    // Checks if the new slot overlaps with any slot in the existing list
    private boolean conflict(Slot newSlot, List<Slot> currentSlots) {
        for (int i = 0; i < currentSlots.size(); i++) {
            Slot current = currentSlots.get(i);
            if (current.overlaps(newSlot)) {
                return true;
            }
        }
        return false;
    }

    @Test
    public void bookingConflict() {
        List<Slot> currentSlots = new ArrayList<>();

        // current slot
        Slot slot = new Slot("tutor1", "2025-06-01", "10:00", "11:00", false, true);
        currentSlots.add(slot);

        //overlapping slot
        Slot overlapping = new Slot("tutor1", "2025-06-01", "10:30", "11:30", false, true);

        //not overlapping slot
        Slot notOverlapping = new Slot("tutor1", "2025-06-01", "11:00", "12:00", false, true);

        // checks conflict
        assertTrue("Conflict", conflict(overlapping, currentSlots));
        assertFalse("No conflicts", conflict(notOverlapping, currentSlots));
    }
}

