package com.example.taskpilot;

public class Notification {

    String message;
    String date;
    String time;
    boolean marked_read;

    public String getMessage() {
        return message;
    }

    public Notification() {
    }

    public Notification(String message, String date, String time) {
        this.message = message;
        this.date = date;
        this.time = time;
        marked_read = false;
    }

    public Notification(String message, String date, String time, boolean marked_read) {
        this.message = message;
        this.date = date;
        this.time = time;
        this.marked_read = marked_read;
    }

    public void setMessage(String message) {
        this.message = message;
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

    public boolean getMarkedRead() {
        return marked_read;
    }
    public void setMarkedRead(boolean markedRead) { this.marked_read = markedRead; }

}
