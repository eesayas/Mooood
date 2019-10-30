package com.example.mooood;

import java.io.Serializable;

public class Mood implements Serializable {
    //TODO:Add a color field, or maybe that can be part of whatever view we choose to display our moods in -Max
    private String date;
    private String time;
    private String emotionalState;
    private String reason;//TODO: This needs to be a photograph
    private String socialSituation;  /*TODO:This needs to be one of:  alone, with one other person,
    with two to several people, or with a crowd
     */
    //TODO: We need a location field for a mood as well
    //Something like "private Location location" perhaps? -Max

    public Mood(String date, String time, String emotionalState, String reason, String socialSituation) {
        this.date = date;
        this.time = time;
        this.emotionalState = emotionalState;
        this.reason = reason;
        this.socialSituation = socialSituation;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getEmotionalState() {
        return emotionalState;
    }

    public void setEmotionalState(String emotionalState) {
        this.emotionalState = emotionalState;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getSocialSituation() {
        return socialSituation;
    }

    public void setSocialSituation(String socialSituation) {
        this.socialSituation = socialSituation;
    }
}
