package ru.svolf.melissa.model;

public class AdvancedItem {
    private String title;
    private boolean forApkeditor;
    private int action;

    public AdvancedItem(String title, boolean forApkeditor, int action) {
        this.title = title;
        this.forApkeditor = forApkeditor;
        this.action = action;
    }

    public String getTitle() {
        return title;
    }

    public boolean forApkEditor() {
        return forApkeditor;
    }

    public int getAction() {
        return action;
    }
}
