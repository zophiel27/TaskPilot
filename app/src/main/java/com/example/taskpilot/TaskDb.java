package com.example.taskpilot;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TaskDb {

    Context context;
    SQLiteDatabase database;
    MyOpenHelper helper;

    private final String DATABASE_NAME = "TaskDB";
    private final int DATABASE_VERSION = 4;

    // task table
    private static final String TABLE_NAME = "tasks";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "event_name";
    private static final String COLUMN_DESC= "description";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_START_TIME = "start_time";
    private static final String COLUMN_END_TIME = "end_time";
    private static final String COLUMN_REMINDER = "reminder";

    // notification table
    private static final String NOTIF_TABLE = "notifications";
    private static final String NOTIF_ID = "id";
    private static final String NOTIF_MESSAGE = "message";
    private static final String NOTIF_DATE = "date";
    private static final String NOTIF_TIME = "time";
    private static final String NOTIF_MARKED = "marked_read";

    public TaskDb(Context context)
    {
        this.context = context;
    }

    public void open()
    {
        helper = new MyOpenHelper(context);
        database = helper.getWritableDatabase();

    }

    public long insert(String name, String description, String date, String startTime, String endTime, String reminder)
    {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NAME, name);
        cv.put(COLUMN_DESC, description);
        cv.put(COLUMN_DATE, date);
        cv.put(COLUMN_START_TIME, startTime);
        cv.put(COLUMN_END_TIME, endTime);
        cv.put(COLUMN_REMINDER, reminder);

        return database.insert(TABLE_NAME, null, cv);
    }

    public int update(int id, String name, String description, String date, String startTime, String endTime, String reminder)
    {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NAME, name);
        cv.put(COLUMN_DESC, description);
        cv.put(COLUMN_DATE, date);
        cv.put(COLUMN_START_TIME, startTime);
        cv.put(COLUMN_END_TIME, endTime);
        cv.put(COLUMN_REMINDER, reminder);

        return database.update(TABLE_NAME, cv, COLUMN_ID+"=?", new String[]{String.valueOf(id)});
    }

    public int delete(int id)
    {
        return database.delete(TABLE_NAME, COLUMN_ID+"=?", new String[]{String.valueOf(id)});
    }

    public void close() {
        database.close();
        helper.close();
    }

    public String readData() {
        String data = "";
        String []columns = new String[]{COLUMN_ID, COLUMN_NAME, COLUMN_DESC, COLUMN_DATE, COLUMN_START_TIME, COLUMN_END_TIME, COLUMN_REMINDER};

        Cursor c = database.query(TABLE_NAME, columns, null, null, null, null, null);

        int index_id = c.getColumnIndex(COLUMN_ID);
        int index_name = c.getColumnIndex(COLUMN_NAME);
        int index_desc = c.getColumnIndex(COLUMN_DESC);
        int index_date = c.getColumnIndex(COLUMN_DATE);
        int index_start = c.getColumnIndex(COLUMN_START_TIME);
        int index_end = c.getColumnIndex(COLUMN_END_TIME);
        int index_reminder = c.getColumnIndex(COLUMN_REMINDER);

        for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
        {
            int id = c.getInt(index_id);
            String name = c.getString(index_name);
            String desc = c.getString(index_desc);
            String date = c.getString(index_date);
            String startTime = c.getString(index_start);
            String endTime = c.getString(index_end);
            String reminder = c.getString(index_reminder);

            data += "ID: " + id + "\n" +
                    "Event: " + name + "\n" +
                    "Description: " + desc + "\n" +
                    "Date: " + date + "\n" +
                    "Time: " + startTime + " - " + endTime + "\n" +
                    "Reminder: " + reminder + "\n\n";
        }

        c.close();
        return data;
    }

    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        int nameIndex = cursor.getColumnIndex(COLUMN_NAME);
        int descIndex = cursor.getColumnIndex(COLUMN_DESC);
        int dateIndex = cursor.getColumnIndex(COLUMN_DATE);
        int startIndex = cursor.getColumnIndex(COLUMN_START_TIME);
        int endIndex = cursor.getColumnIndex(COLUMN_END_TIME);
        int reminderIndex = cursor.getColumnIndex(COLUMN_REMINDER);

        if (cursor.moveToFirst()) {
            do {
                Task task = new Task();

                task.setName(cursor.getString(nameIndex));
                task.setDescription(cursor.getString(descIndex));
                task.setDate(cursor.getString(dateIndex));
                task.setTime(cursor.getString(startIndex) + " - " + cursor.getString(endIndex));
                task.setReminder(cursor.getString(reminderIndex));

                tasks.add(task);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return tasks;
    }
    public ArrayList<Task> getFutureTasks(String currentDate) {

        ArrayList<Task> futureTasks = new ArrayList<>();

        // converting currentDate from MM/dd/yyyy to yyyyMMdd for comparison
        String[] parts = currentDate.split("/");
        String reformattedDate = parts[2] + parts[0] + parts[1]; // yyyyMMdd
        // query that reformats MM/dd/yyyy in SQLite
        String query = "SELECT * FROM " + TABLE_NAME +
                " WHERE (SUBSTR(" + COLUMN_DATE + ", 7, 4) || SUBSTR(" + COLUMN_DATE + ", 1, 2) || SUBSTR(" + COLUMN_DATE + ", 4, 2)) >= ?" +
                " ORDER BY " + COLUMN_DATE + " ASC, " + COLUMN_END_TIME + " ASC";

        Cursor cursor = database.rawQuery(query, new String[]{reformattedDate});

        int nameIndex = cursor.getColumnIndex(COLUMN_NAME);
        int descIndex = cursor.getColumnIndex(COLUMN_DESC);
        int dateIndex = cursor.getColumnIndex(COLUMN_DATE);
        int startIndex = cursor.getColumnIndex(COLUMN_START_TIME);
        int endIndex = cursor.getColumnIndex(COLUMN_END_TIME);
        int reminderIndex = cursor.getColumnIndex(COLUMN_REMINDER);

        if (cursor.moveToFirst()) {
            do {
                Task task = new Task();

                task.setName(cursor.getString(nameIndex));
                task.setDescription(cursor.getString(descIndex));
                task.setDate(cursor.getString(dateIndex));
                task.setTime(cursor.getString(startIndex) + " - " + cursor.getString(endIndex));
                task.setReminder(cursor.getString(reminderIndex));

                futureTasks.add(task);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return futureTasks;
    }
    public ArrayList<Task> getPastTasks(String currentDate) {

        ArrayList<Task> futureTasks = new ArrayList<>();

        // converting currentDate from MM/dd/yyyy to yyyyMMdd for comparison
        String[] parts = currentDate.split("/");
        String reformattedDate = parts[2] + parts[0] + parts[1]; // yyyyMMdd
        // query that reformats MM/dd/yyyy in SQLite
        String query = "SELECT * FROM " + TABLE_NAME +
                " WHERE (SUBSTR(" + COLUMN_DATE + ", 7, 4) || SUBSTR(" + COLUMN_DATE + ", 1, 2) || SUBSTR(" + COLUMN_DATE + ", 4, 2)) < ?" +
                " ORDER BY " +
                "(SUBSTR(" + COLUMN_DATE + ", 7, 4) || SUBSTR(" + COLUMN_DATE + ", 1, 2) || SUBSTR(" + COLUMN_DATE + ", 4, 2)) DESC, " +
                COLUMN_START_TIME + " DESC";

        Cursor cursor = database.rawQuery(query, new String[]{reformattedDate});

        int nameIndex = cursor.getColumnIndex(COLUMN_NAME);
        int descIndex = cursor.getColumnIndex(COLUMN_DESC);
        int dateIndex = cursor.getColumnIndex(COLUMN_DATE);
        int startIndex = cursor.getColumnIndex(COLUMN_START_TIME);
        int endIndex = cursor.getColumnIndex(COLUMN_END_TIME);
        int reminderIndex = cursor.getColumnIndex(COLUMN_REMINDER);

        if (cursor.moveToFirst()) {
            do {
                Task task = new Task();

                task.setName(cursor.getString(nameIndex));
                task.setDescription(cursor.getString(descIndex));
                task.setDate(cursor.getString(dateIndex));
                task.setTime(cursor.getString(startIndex) + " - " + cursor.getString(endIndex));
                task.setReminder(cursor.getString(reminderIndex));

                futureTasks.add(task);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return futureTasks;
    }

    public long insertNotification(String message, String date, String time) {

        ContentValues cv = new ContentValues();
        cv.put(NOTIF_MESSAGE, message);
        cv.put(NOTIF_DATE, date);
        cv.put(NOTIF_TIME, time);
        cv.put(NOTIF_MARKED, 0);

        return database.insert(NOTIF_TABLE, null, cv);
    }

    public ArrayList<Notification> getAllNotifications() {

        ArrayList<Notification> list = new ArrayList<>();

        String query = "SELECT * FROM " + NOTIF_TABLE +
                " ORDER BY " +
                "(SUBSTR(" + NOTIF_DATE + ", 7, 4) || SUBSTR(" + NOTIF_DATE + ", 1, 2) || SUBSTR(" + NOTIF_DATE + ", 4, 2)) DESC, " +
                NOTIF_TIME + " DESC";

        Cursor cursor = database.rawQuery(query, null);

        int msgIndex = cursor.getColumnIndex(NOTIF_MESSAGE);
        int dateIndex = cursor.getColumnIndex(NOTIF_DATE);
        int timeIndex = cursor.getColumnIndex(NOTIF_TIME);
        int markedIndex = cursor.getColumnIndex(NOTIF_MARKED);

        if (cursor.moveToFirst()) {
            do {
                Notification notif = new Notification();
                notif.setMessage(cursor.getString(msgIndex));
                notif.setDate(cursor.getString(dateIndex));
                notif.setTime(cursor.getString(timeIndex));
                notif.setMarkedRead(cursor.getInt(markedIndex) == 1);

                list.add(notif);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public ArrayList<Notification> getReadNotifications(String currentDate) {

        ArrayList<Notification> list = new ArrayList<>();

        String[] parts = currentDate.split("/");
        String reformattedDate = parts[2] + parts[0] + parts[1];

        String query = "SELECT * FROM " + NOTIF_TABLE +
                " WHERE " + NOTIF_MARKED + " = 1 AND " +
                "(SUBSTR(" + NOTIF_DATE + ", 7, 4) || SUBSTR(" + NOTIF_DATE + ", 1, 2) || SUBSTR(" + NOTIF_DATE + ", 4, 2)) >= ?" +
                " ORDER BY " +
                "(SUBSTR(" + NOTIF_DATE + ", 7, 4) || SUBSTR(" + NOTIF_DATE + ", 1, 2) || SUBSTR(" + NOTIF_DATE + ", 4, 2)) DESC, " +
                NOTIF_TIME + " DESC";

        Cursor cursor = database.rawQuery(query, new String[]{reformattedDate});

        int msgIndex = cursor.getColumnIndex(NOTIF_MESSAGE);
        int dateIndex = cursor.getColumnIndex(NOTIF_DATE);
        int timeIndex = cursor.getColumnIndex(NOTIF_TIME);
        int markedIndex = cursor.getColumnIndex(NOTIF_MARKED);

        if (cursor.moveToFirst()) {
            do {
                Notification notif = new Notification();
                notif.setMessage(cursor.getString(msgIndex));
                notif.setDate(cursor.getString(dateIndex));
                notif.setTime(cursor.getString(timeIndex));
                notif.setMarkedRead(cursor.getInt(markedIndex) == 1);

                list.add(notif);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public ArrayList<Notification> getUnreadNotifications(String currentDate) {

        ArrayList<Notification> list = new ArrayList<>();

        String[] parts = currentDate.split("/");
        String reformattedDate = parts[2] + parts[0] + parts[1];

        String query = "SELECT * FROM " + NOTIF_TABLE +
                " WHERE " + NOTIF_MARKED + " = 0 AND " +
                "(SUBSTR(" + NOTIF_DATE + ", 7, 4) || SUBSTR(" + NOTIF_DATE + ", 1, 2) || SUBSTR(" + NOTIF_DATE + ", 4, 2)) >= ?" +
                " ORDER BY " +
                "(SUBSTR(" + NOTIF_DATE + ", 7, 4) || SUBSTR(" + NOTIF_DATE + ", 1, 2) || SUBSTR(" + NOTIF_DATE + ", 4, 2)) DESC, " +
                NOTIF_TIME + " DESC";

        Cursor cursor = database.rawQuery(query, new String[]{reformattedDate});

        int msgIndex = cursor.getColumnIndex(NOTIF_MESSAGE);
        int dateIndex = cursor.getColumnIndex(NOTIF_DATE);
        int timeIndex = cursor.getColumnIndex(NOTIF_TIME);
        int markedIndex = cursor.getColumnIndex(NOTIF_MARKED);

        if (cursor.moveToFirst()) {
            do {
                Notification notif = new Notification();
                notif.setMessage(cursor.getString(msgIndex));
                notif.setDate(cursor.getString(dateIndex));
                notif.setTime(cursor.getString(timeIndex));
                notif.setMarkedRead(cursor.getInt(markedIndex) == 1);

                list.add(notif);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public int markNotificationAsRead(int id) {
        ContentValues cv = new ContentValues();
        cv.put(NOTIF_MARKED, 1);
        return database.update(NOTIF_TABLE, cv, NOTIF_ID + "=?", new String[]{String.valueOf(id)});
    }

    public void insertDummyNotifications() {
        insertNotification("Welcome to the app!", "04/30/2025", "09:00");
        insertNotification("Your task is due tomorrow", "05/01/2025", "10:30");
        insertNotification("This is a reminder", "05/01/2025", "18:00");
    }

    private class MyOpenHelper extends SQLiteOpenHelper
    {
        public MyOpenHelper(Context c)
        {
            super(c, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String query = "CREATE TABLE " + TABLE_NAME + "(" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_NAME + " TEXT NOT NULL," +
                    COLUMN_DESC + " TEXT," +
                    COLUMN_DATE + " TEXT," +
                    COLUMN_START_TIME + " TEXT NOT NULL," +
                    COLUMN_END_TIME + " TEXT NOT NULL," +
                    COLUMN_REMINDER + " TEXT)";
            db.execSQL(query);

            String notificationQuery = "CREATE TABLE notifications (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "message TEXT NOT NULL," +
                    "date TEXT," +
                    "time TEXT," +
                    "marked_read INTEGER DEFAULT 0)";
            db.execSQL(notificationQuery);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + NOTIF_TABLE);
//            onCreate(db);
            if (oldVersion <= 3) {
                String notificationQuery = "CREATE TABLE notifications (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "message TEXT NOT NULL," +
                        "date TEXT," +
                        "time TEXT," +
                        "marked_read INTEGER DEFAULT 0)";
                db.execSQL(notificationQuery);
            }
        }
    }

}
