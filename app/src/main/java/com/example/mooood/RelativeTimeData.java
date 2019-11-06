package com.example.mooood;

public class RelativeTimeData {
    private String timeDenomination;
    private Integer timeData;

    public RelativeTimeData(String timeDenomination, int timeData) {
        this.timeDenomination = timeDenomination;
        this.timeData = timeData;
    }

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
