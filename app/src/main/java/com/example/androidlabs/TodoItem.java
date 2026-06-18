package com.example.androidlabs;

public class TodoItem {

    private long id;
    private String text;
    private boolean urgent;

    // Used when a new item is first created in the app
    public TodoItem(String text, boolean urgent) {
        this.id = -1;
        this.text = text;
        this.urgent = urgent;
    }

    // Used when an item is loaded from the database
    public TodoItem(long id, String text, boolean urgent) {
        this.id = id;
        this.text = text;
        this.urgent = urgent;
    }

    public long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public boolean isUrgent() {
        return urgent;
    }
}