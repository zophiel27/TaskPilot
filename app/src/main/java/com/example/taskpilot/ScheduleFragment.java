package com.example.taskpilot;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ScheduleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScheduleFragment extends Fragment {

    private ListView tasksListView;
    private ArrayList<Task> taskList = new ArrayList<>();
    FloatingActionButton fabAddTask;
    private TaskAdapter adapter;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ScheduleFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ScheduleFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ScheduleFragment newInstance(String param1, String param2) {
        ScheduleFragment fragment = new ScheduleFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        tasksListView = view.findViewById(R.id.lvTasks);
        fabAddTask = view.findViewById(R.id.fabAddTask);

//        taskList.add(new Task("Design new UX flow for Michael", "Start from screen 16", "04/01/2025", "14:00 - 15:00"));
//        taskList.add(new Task("Brainstorm with the team", "Define the problem or question that...", "04/01/2025", "14:00 - 15:00"));
//        taskList.add(new Task("Workout with Ella", "We will do the legs and back workout", "05/04/2025", "19:00 - 20:00"));

        fabAddTask.setOnClickListener(v -> showAddTaskDialog());

        TaskDb db = new TaskDb(getActivity());
        db.open();

        // populate the list view
        taskList.addAll(db.getAllTasks());
        adapter = new TaskAdapter(getContext(), taskList);
        tasksListView.setAdapter(adapter);
//        adapter.notifyDataSetChanged();

        db.close();

        return view;
    }

    private void showAddTaskDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCustomTitle(LayoutInflater.from(getContext()).inflate(R.layout.dialog_title, null));

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_task, null);
        builder.setView(dialogView);

        TextInputEditText etTaskName = dialogView.findViewById(R.id.etTaskName);
        TextInputEditText etDescription = dialogView.findViewById(R.id.etDescription);
        TextInputEditText etDate = dialogView.findViewById(R.id.etDate);
        TextInputEditText etStartTime = dialogView.findViewById(R.id.etStartTime);
        TextInputEditText etEndTime = dialogView.findViewById(R.id.etEndTime);
        TextInputEditText etReminder = dialogView.findViewById(R.id.etReminder);

        etDate.setOnClickListener(v -> showDatePicker(etDate));

        final int[] startTimeHour = new int[1];
        final int[] startTimeMinute = new int[1];
        final int[] endTimeHour = new int[1];
        final int[] endTimeMinute = new int[1];

        etStartTime.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            TimePickerDialog timePicker = new TimePickerDialog(
                    getContext(),
                    (view, hourOfDay, minute) -> {
                        startTimeHour[0] = hourOfDay;
                        startTimeMinute[0] = minute;
                        String selectedTime = String.format(Locale.getDefault(),
                                "%02d:%02d", hourOfDay, minute);
                        etStartTime.setText(selectedTime);

                        if (!etEndTime.getText().toString().isEmpty()) {
                            validateTimeRange(etStartTime, etEndTime);
                        }
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true);
            timePicker.show();
        });

        etEndTime.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            TimePickerDialog timePicker = new TimePickerDialog(
                    getContext(),
                    (view, hourOfDay, minute) -> {
                        endTimeHour[0] = hourOfDay;
                        endTimeMinute[0] = minute;
                        String selectedTime = String.format(Locale.getDefault(),
                                "%02d:%02d", hourOfDay, minute);
                        etEndTime.setText(selectedTime);

                        // validate against start time if it's set
                        if (!etStartTime.getText().toString().isEmpty()) {
                            validateTimeRange(etStartTime, etEndTime);
                        }
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true);
            timePicker.show();
        });

        etReminder.setOnClickListener(v -> showReminderPicker(etReminder));

        builder.setPositiveButton("Create Task", (dialog, which) -> {
            String taskName = etTaskName.getText().toString().trim();
            String description = etDescription.getText().toString().trim();
            String date = etDate.getText().toString().trim();
            String startTime = etStartTime.getText().toString().trim();
            String endTime = etEndTime.getText().toString().trim();
            String reminder = etReminder.getText().toString().trim();

            // validating fields (name, start time and end time are compulsory)
            if (taskName.isEmpty()) {
                Toast.makeText(getContext(), "Task name is required", Toast.LENGTH_SHORT).show();
                return;
            }
            if (startTime.isEmpty() || endTime.isEmpty()) {
                Toast.makeText(getContext(), "Time is required", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!isEndTimeAfterStartTime(startTime, endTime)) {
                Toast.makeText(getContext(), "Time is not set correctly", Toast.LENGTH_SHORT).show();
                return;
            }

            // all validation checks have passed and so task can be stored in db
            TaskDb db = new TaskDb(getActivity());
            db.open();
            // insert in database
            db.insert(taskName, description, date, startTime, endTime, reminder);
            // update the list view as well
            taskList.clear();
            taskList.addAll(db.getAllTasks());
            adapter.notifyDataSetChanged();
            db.close();
        });

        builder.setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showDatePicker(TextInputEditText editText) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePicker = new DatePickerDialog(
                getContext(),
                (view, year, month, dayOfMonth) -> {
                    String selectedDate = String.format(Locale.getDefault(),
                            "%02d/%02d/%04d", month + 1, dayOfMonth, year);
                    editText.setText(selectedDate);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePicker.show();
    }

    private void showReminderPicker(TextInputEditText editText) {
        String[] reminderOptions = new String[]{"None", "10 minutes before", "30 minutes before", "1 hour before"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Set Reminder")
                .setItems(reminderOptions, (dialog, which) -> {
                    editText.setText(reminderOptions[which]);
                });
        builder.create().show();
    }

    private boolean isEndTimeAfterStartTime(String startTime, String endTime) {
        try {
            String[] startParts = startTime.split(":");
            String[] endParts = endTime.split(":");

            int startHour = Integer.parseInt(startParts[0]);
            int startMinute = Integer.parseInt(startParts[1]);
            int endHour = Integer.parseInt(endParts[0]);
            int endMinute = Integer.parseInt(endParts[1]);

            if (endHour > startHour) {
                return true;
            } else if (endHour == startHour) {
                return endMinute > startMinute;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private void validateTimeRange(TextInputEditText startTimeEdit, TextInputEditText endTimeEdit) {
        String startTime = startTimeEdit.getText().toString();
        String endTime = endTimeEdit.getText().toString();

        if (!startTime.isEmpty() && !endTime.isEmpty()) {
            if (!isEndTimeAfterStartTime(startTime, endTime)) {
                endTimeEdit.setError("End time must be after start time");
            } else {
                endTimeEdit.setError(null);
            }
        }
    }
}