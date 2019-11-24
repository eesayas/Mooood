package com.example.mooood;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class Notification {
    String username;
    String requestTime;

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
        return date;
    }

    public String getTime(){
        String[] data = this.requestTime.split(" ");
        String time = data[3]+" "+data[4];
        return time;
    }

}

