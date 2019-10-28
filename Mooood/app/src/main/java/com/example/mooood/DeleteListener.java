package com.example.mooood;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;


//activity_main_btn_delete listener, as the name implies it basically deletes a mood from the view.
//Based on lab tutorial on listeners
public class DeleteListener implements View.OnClickListener {
    private Mood toDelete;
    private ArrayAdapter<Mood> moodListAdapter;

    public DeleteListener(Mood toDelete, ArrayAdapter<Mood> moodListAdapter) {
        this.toDelete = toDelete;
        this.moodListAdapter=moodListAdapter;
    }

    @Override
    public void onClick(View view) {
        if(getToDelete()!=null){
            moodListAdapter.remove(getToDelete());
            moodListAdapter.notifyDataSetChanged();
            //Show and hide button based on: //Show and hide button based on:https://stackoverflow.com/questions/21899825/show-hide-button-when-focus-the-list-item-in-android-listview
            Button deleteMoodButton = (Button)view.findViewById(R.id.activity_main_btn_delete);
            deleteMoodButton.setVisibility(View.INVISIBLE);
            setToDelete(null);
        }
    }
    //Gets mood to delete
    public Mood getToDelete() {
        return toDelete;
    }
    //Sets mood to delete
    public void setToDelete(Mood toDelete) {
        this.toDelete = toDelete;
    }
}

