package com.example.taskpilot;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class TaskAdapter extends ArrayAdapter<Task> {
    public TaskAdapter(@NonNull Context context, @NonNull ArrayList<Task> tasks) {
        super(context, 0, tasks);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Task task = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.single_task_item, parent, false);
        }

        TextView taskTime = convertView.findViewById(R.id.tvTaskTime);
        TextView taskTitle = convertView.findViewById(R.id.tvTaskTitle);
        TextView taskDescription = convertView.findViewById(R.id.tvTaskDescription);

        if (task != null)
        {
            taskTime.setText(task.time);
            taskTitle.setText(task.title);
            taskDescription.setText(task.description);
        }

        return convertView;
    }
}
