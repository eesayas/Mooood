package com.example.mooood;

import java.util.ArrayList;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
public class MoodsTest {

    private ArrayList<MoodEvent> mockMoodEvents() {
        ArrayList<MoodEvent> mockMoodEvents = new ArrayList<MoodEvent>();
        mockMoodEvents.add(mockMood());
        return mockMoodEvents;
    }

    private MoodEvent mockMood() {
        return new MoodEvent("me", "Nov. 08 2019", "12:11:23 p.m."
                , "happy", "nothing", "reason", "Alone");
    }

    @Test
    void testAdd() {
        ArrayList<MoodEvent> mockMoodEvents = mockMoodEvents();

        assertEquals(1, mockMoodEvents.size());

        MoodEvent moodEvent = new MoodEvent("me", "Nov. 08 2019", "12:00:23 p.m."
                , "sad", "nothing", "other reason", "With Group");

        mockMoodEvents.add(moodEvent);

        assertEquals(2, mockMoodEvents.size());
    }

    @Test
    void testEdit(){
        ArrayList<MoodEvent> mockMoodEvents = mockMoodEvents();

        assertEquals("Alone",mockMoodEvents.get(0).getSocialSituation());

        mockMoodEvents.get(0).setSocialSituation("With Group");

        assertEquals("With Group",mockMoodEvents.get(0).getSocialSituation());
    }

}
