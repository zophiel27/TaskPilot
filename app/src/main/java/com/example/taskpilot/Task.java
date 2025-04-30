package com.example.taskpilot;

public class Task {
    String name;
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

    public Task(String name, String description, String date, String time, String reminder) {
        this.name = name;
        this.description = description;
        this.date = date;
        this.time = time;
        this.reminder = reminder;
    }

    public Task(String name, String description, String date, String time) {
        this.name = name;
        this.description = description;
        this.date = date;
        this.time = time;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
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
