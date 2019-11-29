package com.example.mooood;

/**
 * This is a class for RelativeTimeData object that will be used on RelativeTime class.
 * Each RelativeTimeData has a timeDenomination(ie HOURS, MINUTES) and a timeData which is an integer
 */

public class RelativeTimeData {
    private String timeDenomination;
    private Integer timeData;

    /**
     * Simple constructor
     */
    public RelativeTimeData(String timeDenomination, int timeData) {
        this.timeDenomination = timeDenomination;
        this.timeData = timeData;
    }

    /**
     * Simple getters and setters
     */
    public String getTimeDenomination() {
        return this.timeDenomination;
    }

    public void setTimeDenomination(String timeDenomination) {
        this.timeDenomination = timeDenomination;
    }

    public Integer getTimeData() {
        return this.timeData;
    }

    public void setTimeData(int timeData) {
        this.timeData = timeData;
    }
}