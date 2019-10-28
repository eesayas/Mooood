package com.example.mooood;
//Modified datepicker fragment to provide timepicker functionality.
//Again, based on:  https://brandonlehr.com/android/learn-to-code/2018/08/19/callling-android-datepicker-fragment-from-a-fragment-and-getting-the-date?fbclid=IwAR0ixIB3nbIx7k2gQpu1Nz3VU48pg5ii3grksnRqgLNr-TcDZgV2QHg0uXA

import android.app.Activity;
import android.app.TimePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class SelectTimeFragment extends AppCompatDialogFragment implements TimePickerDialog.OnTimeSetListener {
    private static final String TAG = "TimePickerFragment";
    final Calendar calendar = Calendar.getInstance();

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Set the current time as the default time
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);

        // Return a new instance of TimePickerDialog
        return new TimePickerDialog(getActivity(), SelectTimeFragment.this, hour,minute,true);
    }

    // called when a time has been selected
    public void onTimeSet(TimePicker view, int hour,int minute) {
        calendar.set(Calendar.HOUR, hour);
        calendar.set(Calendar.MINUTE, minute);
        //Formats the time correctly
        String newHour;
        String newMinute;
        if(hour<10){
            newHour="0"+Integer.toString(hour);
        }
        else{
            newHour=Integer.toString(hour);
        }
        if(minute<10){
            newMinute="0"+Integer.toString(minute);
        }
        else{
            newMinute=Integer.toString(minute);
        }
        String selectedTime = newHour+":"+newMinute;

        Log.d(TAG, "onTimeSet: " + selectedTime);
        // send time back to the target fragment
        getTargetFragment().onActivityResult(
                getTargetRequestCode(),
                Activity.RESULT_OK,
                new Intent().putExtra("selectedTime", selectedTime)
        );
    }
}