//Reference: Lab 3 Instructions - Fragments
package com.example.mooood;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;

public class SocialSituationFragment extends DialogFragment {

    TextView socialSituationText;

    ListView socialSituationList;
    ArrayAdapter<String> socialSituationAdapter;
    ArrayList<String> socialSituationDataList;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState){
        //Inflate layout of this fragment
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_social_situation, null);

        //add data for Adapter
        socialSituationDataList = new ArrayList<>();

        socialSituationDataList.add("Alone");
        socialSituationDataList.add("With Group");
        socialSituationDataList.add("With Crowd");

        //basic ArrayAdapter setup for social situations
        socialSituationList = view.findViewById(R.id.social_situation_list);
        socialSituationAdapter = new ArrayAdapter<>(view.getContext(), R.layout.content_social_situation, socialSituationDataList);
        socialSituationList.setAdapter(socialSituationAdapter);

        //for changing TextView in CreatePostActivity
        socialSituationText = getActivity().findViewById(R.id.social_situation);

        //on click listener for each social situation
        socialSituationList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){

                //change social_situation TextView in CreatePostActivity
                socialSituationText.setText(socialSituationDataList.get(position));

                //exit SocialSituationFragment after choice
                dismiss();
            }

        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView(view)
                .setTitle("Choose Social Situation")
                .create();
    }
}
