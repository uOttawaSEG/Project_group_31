package com.example.test;

import com.example.test.tutor.Tutor;

import com.example.test.sharedfiles.model.Rating;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

public class TutorTest {

    @Test
    public void calculateAverageRating_usesAllRatingsCorrectly() {
        Tutor tutor = new Tutor();

        tutor.setRatings(new ArrayList<Rating>());


        tutor.addRating(new Rating("rating1", "tutor1", "student1", "session1", 5, "great", System.currentTimeMillis()));

        tutor.addRating(new Rating("rating2", "tutor1", "student2", "session2", 3, "ok", System.currentTimeMillis()));

        tutor.addRating(new Rating("rating3", "tutor1", "student3", "session3", 4, "good", System.currentTimeMillis()));

        double avg = tutor.getAverageRating();

        // Assert the average is correct
        assertEquals("Average rating should be 4.0", 4.0, avg, 0.0);
    }
}
