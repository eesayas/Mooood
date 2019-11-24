package com.example.mooood;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * This is an adapter for displaying MoodEvents in UserFeedActivity
 * The code is implemented to accommodate RecyclerView
 **/
public class MoodEventsAdapter extends RecyclerView.Adapter<MoodEventsAdapter.MoodEventViewHolder> {

    private List<MoodEvent> moodEventList;
    private OnMoodEventListener onMoodEventListener;

    public class MoodEventViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ImageView emoticon;
        private TextView author, relativeTime;
        OnMoodEventListener onMoodEventListener;

        public MoodEventViewHolder(View view, OnMoodEventListener onMoodEventListener){
            super(view);

            author = view.findViewById(R.id.author);
            emoticon = view.findViewById(R.id.emoticon) ;
            relativeTime = view.findViewById(R.id.date_and_time);

            this.onMoodEventListener = onMoodEventListener;

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view){
            onMoodEventListener.onMoodEventClick(getAdapterPosition());
        }
    }

    public interface OnMoodEventListener{
        void onMoodEventClick(int position);
    }

    public MoodEventsAdapter(List<MoodEvent> moodEventList, OnMoodEventListener onMoodEventListener){
        this.moodEventList = moodEventList;
        this.onMoodEventListener = onMoodEventListener;
    }

    @Override
    public MoodEventViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_user_feed, parent, false);
        return new MoodEventViewHolder(itemView, onMoodEventListener);
    }

    @Override
    public void onBindViewHolder(MoodEventViewHolder holder, int position){
        MoodEvent moodEvent = moodEventList.get(position);

        holder.emoticon.setImageResource(new Emoticon(moodEvent.getEmotionalState(), 1).getImageLink());
        holder.author.setText(moodEvent.getAuthor());
        holder.relativeTime.setText(moodEvent.getDate());

    }

    @Override
    public int getItemCount(){
        return moodEventList.size();
    }

}