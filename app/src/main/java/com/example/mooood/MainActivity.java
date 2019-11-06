package com.example.mooood;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Document;

import java.util.HashMap;


/**
 * This is where the program starts (Login/Sign up)
 */
public class MainActivity extends AppCompatActivity {
    private EditText userName;
    private EditText password;
    private TextView noAccount;
    private TextView signUp;
    private Button prompt;
    private TextView errorMsg;
    private ConstraintLayout background;

    // 0 means logging in, 1 means sign up
    private Integer checkBtn = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userName = findViewById(R.id.activity_main_et__username);
        password = findViewById(R.id.activity_main_et__password);
        signUp = findViewById(R.id.activity_main_tv_signUp);
        prompt = findViewById(R.id.activity_main_btn_submit);
        noAccount = findViewById(R.id.activity_main_tv_noAccount);
        background = findViewById(R.id.activity_main_CL_background);
        errorMsg = findViewById(R.id.activity_main_tv_incorrect);
    }

    /**
     * takes the user to the sign up page or the login page depending on where they were
     */
    public void userSignUpORSignIn(View view) {
        Log.d("debugging","mainActivity- |" + signUp.getText().toString());
        // hiding error message textview
        errorMsg.setVisibility(View.INVISIBLE);
        // setting up the screen to look like the sign up screen
        if (signUp.getText().toString().equals(getString(R.string.sign_up))) {
            prompt.setText(R.string.signUpBtn);
            prompt.setBackgroundColor(0xFF00BCD4);
            background.setBackgroundColor(Color.BLACK);
            noAccount.setText(R.string.alreadyExist);
            noAccount.setTextColor(0xFFFFFFFF);
            signUp.setText(R.string.sign_in);
            signUp.setTextColor(0xFF3F51B5);
            // settingg tp sign up
            checkBtn = 1;
        }
        // setting up the screen to look like log in screen
        else {
            prompt.setText(R.string.submit);
            prompt.setBackgroundColor(0xFF000000);
            background.setBackgroundColor(0xFF00BCD4);
            checkBtn = 0;
            userName.setText("");
            password.setText("");
            noAccount.setText(R.string.no_account);
            signUp.setText(R.string.sign_up);
            signUp.setTextColor(0xFF3F51B5);

        }
    }

    /**
     * Checks to make sure the username or password is not empty when an user tries to login/sign up
     */
    public void CheckEmpty(View view) {
        Log.d("debugging","mainActivity- |"+ prompt.getText().toString()+"|");
        String userNameInput = userName.getText().toString().toLowerCase();
        String passwordInput = password.getText().toString();
        // if either username or password is empty it will show a message saying that they can not be empty
        if ((userName.getText().toString().isEmpty()) || password.getText().toString().isEmpty()){
            errorMsg.setText(R.string.empty);
            errorMsg.setVisibility(View.VISIBLE);
        }
        // since checkBtn is 0, authenticating log in by calling CheckLogin
        else if (checkBtn == 0){
            Log.d("debugging", "mainActivity- Logging in with " + userNameInput +" and " + passwordInput);
            CheckLogIn(userNameInput, passwordInput);
        }
        // letting user sign up
        else{
            Log.d("debugging", "mainActivity- Signing up with " + userNameInput + " and " + passwordInput);
            MakeAccount(userNameInput, passwordInput);
        }
    }

    /**
     * Checks if the account exists or not to validate log in
     * @param accountName
     *     This is the account name user enters
     * @param inputtedpassword
     *     This is the password user enters
     */
    public void CheckLogIn(final String accountName, final String inputtedpassword){
        Log.d("debugging", "mainActivity- Check account");
        final FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("participant").document(accountName);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    // if the account account checks for correct password
                    if (document.exists()) {
                        Log.d("debugging", "mainActivity- Password: " + document.getData().get("password"));
                        // if inputted password is correct, let the user log in
                        if (document.getData().get("password").equals(inputtedpassword)){
                            Log.d("debugging", "mainActivity- Logged In");
                            errorMsg.setVisibility(View.INVISIBLE);
                            errorMsg.setText("");

                            Intent intent = new Intent(getApplicationContext(), UserFeedActivity.class);
                            intent.putExtra("accountKey", accountName);
                            startActivity(intent);
                        }
                        // if inputted password is wrong, set the password field empty and
                        // show error message
                        else {
                            errorMsg.setText(R.string.wrongAccount);
                            errorMsg.setVisibility(View.VISIBLE);
                            password.setText("");
                        }

                        // the account does not exists, call ShowToast and set username and password field back to empty
                    } else {
                        Log.d("debugging", "mainActivity- Account does not exists");
                        errorMsg.setText(R.string.dneAccount);
                        errorMsg.setVisibility(View.VISIBLE);
                        userName.setText("");
                        password.setText("");
                    }
                } else {
                    Log.d("debugging", "mainActivity- get failed with ", task.getException());
                }
            }
        });
    }


    // checks if the inputted username already exists or not, if it does, it takes the user back to
    // log in page, if not, lets the user sign up


    /**
     * Checks if the account exists or not to let user make an account
     * If successful, logs in user automatically
     * @param accountName
     *     This is the account name user enters
     * @param inputtedPassword
     *     This is the password user enters
     */

    public void MakeAccount(final String accountName, final String inputtedPassword){
        Log.d("debugging", "mainActivity- Adding account to FB");
        final FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();

        // Checking if the account exists or not
        DocumentReference docRef = db.collection("participant").document(accountName);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    // if the account exists
                    if (document.exists()) {
                        Log.d("debugging", "mainActivity- Account already exists with password as:  " + document.getData());
                        errorMsg.setText(R.string.existAccount);
                        errorMsg.setVisibility(View.VISIBLE);
                        password.setText("");

                        // since the account does not exists, let the user sign up
                    } else {
                        Log.d("debugging", "mainActivity- Account does not exists");
                        HashMap<String, String> account = new HashMap<>();
                        account.put("password", inputtedPassword);
                        final CollectionReference collectionReference = db.collection("participant");
                        collectionReference
                                .document(accountName)
                                .set(account)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("debugging", "mainActivity- User added");
                                        CheckLogIn(accountName, inputtedPassword);

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("debugging", "mainActivity- Data addition failed" + e.toString());
                                    }
                                });
                    }
                } else {
                    Log.d("debugging", "mainActivity- get failed with ", task.getException());
                }
            }
        });

    }

}