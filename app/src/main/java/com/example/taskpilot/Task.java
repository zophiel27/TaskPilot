package com.example.taskpilot;

public class Task {
    String title;
    String description;
    String date;
    String time;

    public String getReminder() {
        return reminder;
    }

    public void setReminder(String reminder) {
        this.reminder = reminder;
    }

    String reminder;

    public Task() {
    }

    public Task(String title, String description, String date, String time, String reminder) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.time = time;
        this.reminder = reminder;
    }

    public Task(String title, String description, String date, String time) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


}
