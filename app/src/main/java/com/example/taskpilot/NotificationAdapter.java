package com.example.taskpilot;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.taskpilot.Notification;
import com.example.taskpilot.R;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class NotificationAdapter extends ArrayAdapter<Notification> {

    public NotificationAdapter(@NonNull Context context, @NonNull ArrayList<Notification> notifications) {
        super(context, 0, notifications);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Notification notification = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.single_notification_item, parent, false);
        }

        TextView notificationTime = convertView.findViewById(R.id.tvNotificationTime);
        TextView notificationMessage = convertView.findViewById(R.id.tvNotificationMessage);
        TextView notificationDate = convertView.findViewById(R.id.tvNotificationDate);

        if (notification != null)
        {
            notificationTime.setText(notification.getTime());
            notificationMessage.setText(notification.getMessage());
            notificationDate.setText(notification.getDate());
        }

        return convertView;
    }
}
