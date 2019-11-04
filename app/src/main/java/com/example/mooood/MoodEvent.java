package com.example.mooood;
import com.google.firebase.firestore.Exclude;

public class MoodEvent{

    private String documentId;

    private String date;
    private String time;
    private String emotionalState;

    private String imageUrl;
    private String reason;
    private String socialSituation;

    public MoodEvent(){
        //public no-arg constructor needed
    }

    @Exclude
    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public MoodEvent(String date, String time, String emotionalState, String imageReason, String reason, String socialSituation){
        this.date = date;
        this.time = time;
        this.emotionalState = emotionalState;
        this.imageUrl = imageReason;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageReason) {
        this.imageUrl = imageReason;
    }
}
