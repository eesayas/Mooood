package com.example.mooood;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ShowNotificationFragment extends DialogFragment {
    private static final String TAG = "Notification_Fragment";
    private TextView notificationText;
    private Button confirm;
    private Button reject;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference userReference;
    private CollectionReference requestReference;
    private String requestName, userName;


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState){
        //Inflate layout of this fragment
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_notification, null);

        notificationText = view.findViewById(R.id.notification_text);
        confirm = view.findViewById(R.id.confirm_button);
        reject = view.findViewById(R.id.reject_Button);


        NotificationActivity activity = (NotificationActivity) getActivity();
        requestName = activity.getRequestName();
        userName = activity.getUserName();
        notificationText.setText(requestName +" wants to follow you");

        userReference = db.collection("MoodEvents").document(userName).collection("Request");
        requestReference = db.collection("MoodEvents").document(requestName).collection("Following");

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
                // add that users name in followers collection under the request name
                // show a toast that request accepted
                // close fragment
                userReference
                        .document(requestName).delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "request was successfully deleted");
                                Toast.makeText(getActivity(), "confirmed request!",
                                        Toast.LENGTH_SHORT).show();
                                NotificationActivity activity = (NotificationActivity) getActivity();
                                activity.notifydata();
                                dismiss();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "request was not deleted", e);
                            }
                        });

                final Map<String, Object> request = new HashMap<>();
                request.put("Username",userName);
                requestReference.document(userName).set(request);

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
                userReference
                        .document(requestName).delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "request was successfully deleted");
                                Toast.makeText(getActivity(), "rejected request!",
                                        Toast.LENGTH_SHORT).show();
                                NotificationActivity activity = (NotificationActivity) getActivity();
                                activity.notifydata();
                                dismiss();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "request was not deleted", e);
                            }
                        });


            }
        });
    }



}
