package com.example.mooood;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.Exclude;

public class MoodEvent implements Parcelable{

    private String documentId;

    private String author;

    private String date;
    private String time;
    private String emotionalState;

    private String imageUrl;
    private String reason;
    private String socialSituation;

    public MoodEvent(){
        //public no-arg constructor needed
    }

    public MoodEvent(String author, String date, String time, String emotionalState, String imageReason, String reason, String socialSituation){
        this.author = author;
        this.date = date;
        this.time = time;
        this.emotionalState = emotionalState;
        this.imageUrl = imageReason;
        this.reason = reason;
        this.socialSituation = socialSituation;
    }

    protected MoodEvent(Parcel in) {
        documentId = in.readString();
        author = in.readString();
        date = in.readString();
        time = in.readString();
        emotionalState = in.readString();
        imageUrl = in.readString();
        reason = in.readString();
        socialSituation = in.readString();
    }

    public static final Creator<MoodEvent> CREATOR = new Creator<MoodEvent>() {
        @Override
        public MoodEvent createFromParcel(Parcel in) {
            return new MoodEvent(in);
        }

        @Override
        public MoodEvent[] newArray(int size) {
            return new MoodEvent[size];
        }
    };

    @Exclude
    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return this.time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getEmotionalState() {
        return this.emotionalState;
    }

    public void setEmotionalState(String emotionalState) {
        this.emotionalState = emotionalState;
    }

    public String getReason() {
        return this.reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getSocialSituation() {
        return this.socialSituation;
    }

    public void setSocialSituation(String socialSituation) {
        this.socialSituation = socialSituation;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public void setImageUrl(String imageReason) {
        this.imageUrl = imageReason;
    }


    public String getAuthor() {
        return this.author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(documentId);
        parcel.writeString(author);
        parcel.writeString(date);
        parcel.writeString(time);
        parcel.writeString(emotionalState);
        parcel.writeString(imageUrl);
        parcel.writeString(reason);
        parcel.writeString(socialSituation);
    }
}
