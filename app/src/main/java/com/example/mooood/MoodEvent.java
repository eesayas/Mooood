package com.example.mooood;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Date;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.Exclude;

/**
 * This is the class for MoodEvent object
 * It has basic getters and setters for each attribute
 * Parcelable is implemented for sending data to ShowEventActivity
 */

public class MoodEvent implements Parcelable{

    private String documentId;
    private Date timeStamp;

    private String author;

    private String date;
    private String time;
    private String emotionalState;

    private String imageUrl;
    private String reason;
    private String socialSituation;

    private String latitude;
    private String longitude;

    public MoodEvent(){
        //public no-arg constructor needed
    }

    public MoodEvent(String author, String date, String time, String emotionalState, String imageReason, String reason, String socialSituation,String latitude,String longitude){
        this.author = author;
        this.date = date;
        this.time = time;
        this.emotionalState = emotionalState;
        this.imageUrl = imageReason;
        this.reason = reason;
        this.socialSituation = socialSituation;
        this.latitude=latitude;
        this.longitude=longitude;
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
        latitude=in.readString();
        longitude=in.readString();

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

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
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

    public Date getTimeStamp() {
        return this.timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
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
        parcel.writeString(latitude);
        parcel.writeString(longitude);
    }
}
