//This code is a modified version of the Lab Tutorial on fragments
package com.example.mooood;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import static android.text.TextUtils.isEmpty;

public class AddMoodFragment extends DialogFragment {
    private EditText emotionalStateEditText;
    private EditText reasonEditText;
    private EditText socialSituationEditText;
    private EditText dateEditText;
    private EditText timeEditText;
    private OnFragmentInteractionListener listener;
    private Mood mood;
    //Sets fragment title to Add Mood by default
    protected String title="Add Mood";
    //Variable required by selectDate and selectTime fragments in order to communicate with this fragment
    //Based on: https://brandonlehr.com/android/learn-to-code/2018/08/19/callling-android-datepicker-fragment-from-a-fragment-and-getting-the-date?fbclid=IwAR0ixIB3nbIx7k2gQpu1Nz3VU48pg5ii3grksnRqgLNr-TcDZgV2QHg0uXA
    public static final int REQUEST_CODE = 11;

    //Added editMood functionality since the fragment wasn't updating the mood correctly without it
    interface OnFragmentInteractionListener{
        public void addMood(Mood mood);
        public void editMood(Mood mood,String date,String time,String emotionalState,String reason,String socialSituation); //TODO: Needs location argument
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener=(OnFragmentInteractionListener) context;
    }

    public static AddMoodFragment newInstance(Mood mood){
        Bundle args = new Bundle();
        args.putSerializable("mood",mood);

        AddMoodFragment fragment=new AddMoodFragment();
        fragment.setArguments(args);
        return fragment;

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_add_mood,null);
        AlertDialog.Builder builder= new AlertDialog.Builder(getActivity());

        dateEditText= view.findViewById(R.id.fragment_add_mood_et_date);
        timeEditText= view.findViewById(R.id.fragment_add_mood_et_time);
        emotionalStateEditText =view.findViewById(R.id.fragment_add_mood_et_emotional_state);
        reasonEditText = view.findViewById(R.id.fragment_add_mood_et_reason);
        socialSituationEditText = view.findViewById(R.id.fragment_add_mood_et_social_situation);
        //TODO:Missing a field for location


        /*This implementation of datepicker is based on:
        https://brandonlehr.com/android/learn-to-code/2018/08/19/callling-android-datepicker-fragment-from-a-fragment-and-getting-the-date?fbclid=IwAR0ixIB3nbIx7k2gQpu1Nz3VU48pg5ii3grksnRqgLNr-TcDZgV2QHg0uXA
         */
        final FragmentManager dm = (getActivity()).getSupportFragmentManager();
        dateEditText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // create the datePickerFragment
                AppCompatDialogFragment newFragment = new SelectDateFragment();
                // set the targetFragment to receive the results, specifying the request code
                newFragment.setTargetFragment(AddMoodFragment.this, REQUEST_CODE);
                // show the datePicker
                newFragment.show(dm, "datePicker");
            }
        });
        //Modified datepicker to provide timepicker functionality as well
        //Also based on:  https://brandonlehr.com/android/learn-to-code/2018/08/19/callling-android-datepicker-fragment-from-a-fragment-and-getting-the-date?fbclid=IwAR0ixIB3nbIx7k2gQpu1Nz3VU48pg5ii3grksnRqgLNr-TcDZgV2QHg0uXA

        final FragmentManager tm = (getActivity()).getSupportFragmentManager();
        timeEditText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // create the timePickerFragment
                AppCompatDialogFragment newFragment = new SelectTimeFragment();
                // set the targetFragment to receive the results, specifying the request code
                newFragment.setTargetFragment(AddMoodFragment.this, REQUEST_CODE);
                // show the timePicker
                newFragment.show(tm, "timePicker");
            }
        });

        if(getArguments()!=null){
            //Gets mood object from bundle
            mood=(Mood) getArguments().getSerializable("mood");

            dateEditText.setText(mood.getDate());
            timeEditText.setText(mood.getTime());
            emotionalStateEditText.setText(mood.getEmotionalState());
            reasonEditText.setText(mood.getReason());
            socialSituationEditText.setText(mood.getSocialSituation());
            //TODO:Need a field for location


            //Just a small detail, but changes fragment to title to Edit Mood if the mood is not null
            if(mood!=null){
                title="Edit Mood";
            }
        }

        return builder.setView(view)
                .setTitle(title)
                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        String date;
                        String time;
                        String emotionalState;
                        String reason;
                        String socialSituation;
                        //TODO:Need a field for location


                        //Prevent empty values from being entered by defaulting them
                        if(isEmpty(dateEditText.getText())){
                            dateEditText.setText("2000-01-01");
                        }
                        if(isEmpty(timeEditText.getText())){
                            timeEditText.setText("00:00");
                        }
                        if(isEmpty(emotionalStateEditText.getText())){
                            emotionalStateEditText.setText("Happy");
                        }
                        if(isEmpty(reasonEditText.getText())){
                            reasonEditText.setText("Did good on midterm");
                        }
                        if(isEmpty(socialSituationEditText.getText())){
                            socialSituationEditText.setText("Alone");
                        }
                        //TODO:Need a field for location




                        date= dateEditText.getText().toString();
                        time= timeEditText.getText().toString();
                        emotionalState=emotionalStateEditText.getText().toString();
                        reason=reasonEditText.getText().toString();
                        socialSituation=socialSituationEditText.getText().toString();
                        //TODO:Need a field for location


                        //TODO: Fix bug where empty inputs in add mood crash the app
                        //EDIT: Fixed above by defaulting values where input is empty
                        if(mood==null){

                            Mood mood= new Mood(date,time,emotionalState,reason,socialSituation);
                            listener.addMood(mood);
                        }
                        else {
                            listener.editMood(mood,date,time,emotionalState,reason,socialSituation);
                        }

                    }
                })
                .setNegativeButton("Cancel",null)
                .create();
    }

    //Slightly modified version of datepicker, also obtained from:
    //https://brandonlehr.com/android/learn-to-code/2018/08/19/callling-android-datepicker-fragment-from-a-fragment-and-getting-the-date?fbclid=IwAR0ixIB3nbIx7k2gQpu1Nz3VU48pg5ii3grksnRqgLNr-TcDZgV2QHg0uXA
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // check for the results
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // get date from string
            String selectedDate = data.getStringExtra("selectedDate");
            String selectedTime = data.getStringExtra("selectedTime");
            // set the value of the editText if it exists
            if(selectedTime!=null){
                timeEditText.setText(selectedTime);
            }
            if(selectedDate!=null){
                dateEditText.setText(selectedDate);
            }
        }
    }

}

