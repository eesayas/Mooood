package com.example.mooood;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * This is responsible to determine the relative time of MoodEvent (ie "3 MINUTES AGO" or "JUST NOW").
 * This will be used when a MoodEvent must display a time and date info in UserFeedActivity.
 */
public class RelativeTime {
    private String relativeTime;
    private ArrayList<RelativeTimeData> relativeTimeDataList; //for organizing data later
    private String eventTime;
    private String eventDate;

    /**
     * Constructor which calls methods below to calculate relative time
     */
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

    /**
     * This calculates relative time (ie. the time from the MoodEvent to the time now)
     * @param dateMood
     *  This is the date of the MoodEvent
     * @return
     *  This returns a String that displays the relative time in appropriate format
     */
    public String timeDifference(Date dateMood) {

        relativeTimeDataList = new ArrayList<>();
        Date dateNow = new Date();
        long different = dateNow.getTime() - dateMood.getTime();

        //future time; will be refactored later
        if (different < 0){

            SimpleDateFormat oldTimeFormat = new SimpleDateFormat("h:mm:ss a");

            try {
                Date eventTime = oldTimeFormat.parse(this.eventTime);

                SimpleDateFormat newTimeFormat = new SimpleDateFormat("h:mm a");

                return this.eventDate + " at " + newTimeFormat.format(eventTime);

            } catch (ParseException e){
                e.printStackTrace();

            }
        }
        //================================================================

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

        return relativeTimeApprox(relativeTimeDataList);

    }

    /**
     * This returns the relative time of the highest time denomination
     * HOURS > MINUTES > SECONDS
     * @param relativeTimeDataList
     *  This has all the relative time data and their respective denomination
     */
    /*========================================================================================
        Code Explanation:
         Let say relativeTimeDatalist = [{'WEEKS', 0}, {'DAYS', 0}, {'HOURS, 2'} ... ].
         We must only return "2 HOURS AGO". The following code does this.
    =========================================================================================*/

    public String relativeTimeApprox(ArrayList<RelativeTimeData> relativeTimeDataList){
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

                    SimpleDateFormat oldTimeFormat = new SimpleDateFormat("h:mm:ss a");

                    try {
                        Date eventTime = oldTimeFormat.parse(this.eventTime);

                        SimpleDateFormat newTimeFormat = new SimpleDateFormat("h:mm a");

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

    /**
     * This removes the last character in a string
     * Will be needed when "SECONDS" needs to be "SECOND" (applicable to other denominations)
     */
    public static String removeLastChar(String s) {
        return (s == null || s.length() == 0)
                ? null
                : (s.substring(0, s.length() - 1));
    }

    /**
     * Simple getter and setter for relative time
     * **/
    public String getRelativeTime() {
        return this.relativeTime;
    }

    public void setRelativeTime(String relativeTime) {
        this.relativeTime = relativeTime;
    }
}
