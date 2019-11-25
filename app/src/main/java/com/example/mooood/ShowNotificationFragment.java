package com.example.mooood;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;

public class ShowNotificationFragment extends DialogFragment {
    private TextView notificationText;
    private Button confirm;
    private Button reject;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState){
        //Inflate layout of this fragment
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_notification, null);

        notificationText = view.findViewById(R.id.notification_text);
        confirm = view.findViewById(R.id.confirm_button);
        reject = view.findViewById(R.id.reject_Button);


        NotificationActivity activity = (NotificationActivity) getActivity();
        String requestName = activity.getMyData();
        notificationText.setText(requestName +" wants to follow you");

        acceptRequest();
        rejectRequest();

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView(view)
                .create();

    } // end of onCreateDialog

    public void acceptRequest() {
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get the document from request collection according to the requestName
                // delete that document in request
                // add that document in followers collection under the request name
                // show a toast that request accepted
                // close fragment

            }
        });
    }

    public void rejectRequest(){
        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get the document from request collection according to the requestName
                // delete that document in request
                // show a toast that request rejected
                // close fragment


            }
        });
    }



}
