package ru.svolf.melissa.model;

public class FlexibleItem {
    private String title;
    private String content;

    public FlexibleItem(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }
}
