package com.example.mooood;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class RelativeTime {
    private String relativeTime;
    private ArrayList<RelativeTimeData> relativeTimeDataList; //for organizing data later
    private String eventTime;
    private String eventDate;

    public RelativeTime(String date, String time){
        String dateAndTimeInput = date + ' ' + time;
        this.eventDate = date;
        this.eventTime = time;

        //get date of MoodEvent
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd yyyy h:mm:ss a");

        try{
            Date dateMood = simpleDateFormat.parse(dateAndTimeInput);
            this.relativeTime = timeDifference(dateMood);

        } catch (ParseException e){
            e.printStackTrace();
            this.relativeTime = date + " " + time;
        }
    }

    public String timeDifference(Date dateMood) {

        //declare variable for later use
        relativeTimeDataList = new ArrayList<>();
        Date dateNow = new Date();
        long different = dateNow.getTime() - dateMood.getTime();
        SimpleDateFormat oldTimeFormat = new SimpleDateFormat("h:mm:ss a");
        SimpleDateFormat newTimeFormat = new SimpleDateFormat("h:mm a");


        if (different < 0){
            return futureTime(oldTimeFormat, newTimeFormat);
        }

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;
        long weeksInMilli = daysInMilli * 7;

        long elapsedWeeks = different / weeksInMilli;
        different = different % weeksInMilli;
        relativeTimeDataList.add(new RelativeTimeData("WEEKS", (int) elapsedWeeks));

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;
        relativeTimeDataList.add(new RelativeTimeData("DAYS", (int) elapsedDays));

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;
        relativeTimeDataList.add(new RelativeTimeData("HOURS", (int) elapsedHours));

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;
        relativeTimeDataList.add(new RelativeTimeData("MINUTES", (int) elapsedMinutes));

        long elapsedSeconds = different / secondsInMilli;
        relativeTimeDataList.add(new RelativeTimeData("SECONDS", (int) elapsedSeconds));

        return relativeTimeApprox(relativeTimeDataList, oldTimeFormat, newTimeFormat);

    }

    /*========================================================================================
        Code Explanation:
         Let say relativeTimeDatalist = [{'WEEKS', 0}, {'DAYS', 0}, {'HOURS, 2'} ... ].
         We must only return "2 HOURS AGO". The following code does this.
    =========================================================================================*/

    public String relativeTimeApprox(ArrayList<RelativeTimeData> relativeTimeDataList, SimpleDateFormat oldTimeFormat, SimpleDateFormat newTimeFormat){
        String timeDenomination;
        Integer timeData;

        for (int i = 0; i < relativeTimeDataList.size(); i++) {
            if (relativeTimeDataList.get(i).getTimeData() > 0) {

                timeDenomination = relativeTimeDataList.get(i).getTimeDenomination();
                timeData = relativeTimeDataList.get(i).getTimeData();

                if (timeData == 1) {
                    return timeData.toString() + " " + removeLastChar(timeDenomination) + " AGO";

                } else if (timeDenomination == "SECONDS" && timeData < 50) {
                    return "JUST NOW";

                } else if(timeDenomination == "WEEKS" && timeData > 20){

                    try {
                        Date eventTime = oldTimeFormat.parse(this.eventTime);
                        return this.eventDate + " at " + newTimeFormat.format(eventTime);

                    } catch (ParseException e){
                        e.printStackTrace();
                        return timeData.toString() + " " + timeDenomination + " AGO";
                    }

                }

                return timeData.toString() + " " + timeDenomination + " AGO";
            }
        }

        return "0 SECONDS AGO";

    }

    public String futureTime(SimpleDateFormat oldTimeFormat, SimpleDateFormat newTimeFormat){
        try {
            Date eventTime = oldTimeFormat.parse(this.eventTime);
            return this.eventDate + " at " + newTimeFormat.format(eventTime);

        } catch (ParseException e){
            e.printStackTrace();
        }

        return this.eventDate + " at " + this.eventTime;
    }

    //Resource: baeldung.com/java-remove-last-character-of-string
    public static String removeLastChar(String s) {
        return (s == null || s.length() == 0)
                ? null
                : (s.substring(0, s.length() - 1));
    }

    //normal getter and setter for relative time message
    public String getRelativeTime() {
        return this.relativeTime;
    }

    public void setRelativeTime(String relativeTime) {
        this.relativeTime = relativeTime;
    }
}
