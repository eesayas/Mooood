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
import java.util.ArrayList;

/**
 * This is an adapter for displaying notifications in NOtificationActivity
 * Implementation is derived from lab lectures
 **/
public class NotificationAdapter extends ArrayAdapter<Notification> {

    private static final String TAG = "For Testing";
    private ArrayList<Notification> notifications;
    private Context context;

    public NotificationAdapter(ArrayList<Notification> notifications, Context context) {
        super(context, 0, notifications);
        this.notifications = notifications;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.notification_content, parent, false);
        }

        Notification notification = notifications.get(position);
        //select TextViews
        ImageView userImage = view.findViewById(R.id.user_image);
        TextView author = view.findViewById(R.id.req_author);
        TextView relativeTime = view.findViewById(R.id.req_time);

        //setText of TextViews
        //emoticon.setImageResource(new Emoticon(moodEvent.getEmotionalState(), 1).getImageLink());
        author.setText(notification.getUsername());
        relativeTime.setText(new RelativeTime(notification.getDate(), notification.getTime()).getRelativeTime());

        return view;
    }
}
