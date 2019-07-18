package ru.svolf.melissa.model;

public class LogItem {
    private String tag;
    private String message;

    public LogItem(String tag, String message) {
        this.tag = tag;
        this.message = message;
    }

    public String getTag() {
        return tag;
    }

    public String getMessage() {
        return message;
    }
}
