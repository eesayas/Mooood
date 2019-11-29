package com.example.mooood;

import android.util.Log;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class Notification {
    private String username;
    private String requestTime;

    public Notification(String username, String requestTime) {
        this.username = username;
        this.requestTime = requestTime;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(String requestTime) {
        this.requestTime = requestTime;
    }

    public String getDate(){
        String[] data = this.requestTime.split(" ");
        String date = data[0]+" "+data[1]+" "+data[2];
        Log.d("newNotification", "date: " + date);
        return date;
    }

    public String getTime(){
        String[] data2 = this.requestTime.split(" ");
        String time = data2[3]+" "+data2[4];
        Log.d("newNotification", "time: " + time);
        return time;
    }

    @Override
    public boolean equals(Object o){
        if(this == o){
            return true;
        }
        if(o == null || getClass() != o.getClass()){
            return false;
        }
        Notification temp = (Notification) o;
        return username.equals(temp.getUsername()) &&
                requestTime.equals(temp.getRequestTime());

    }

}
