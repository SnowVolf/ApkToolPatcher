package ru.svolf.melissa.model;

import com.afollestad.async.Action;

public class MenuItem {
    private String title;
    private boolean forApkeditor;
    private int action;
    private Action executableAction;

    public MenuItem(String title, boolean forApkeditor, int action) {
        this.title = title;
        this.forApkeditor = forApkeditor;
        this.action = action;
    }

    public MenuItem(String title, boolean forApkeditor, Action action) {
        this.title = title;
        this.forApkeditor = forApkeditor;
        this.executableAction = action;
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

    public Action getExecutableAction() {
        return executableAction;
    }
}
