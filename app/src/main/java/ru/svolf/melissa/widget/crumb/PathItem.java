package ru.svolf.melissa.widget.crumb;

class PathItem {
    private String folderName;
    private boolean last = false;

    public PathItem(String folderName, boolean isLast) {
        this.folderName = folderName;
        this.last = isLast;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public String getFolderName(){
        return folderName;
    }

    public void setLast(boolean last) {
        this.last = last;
    }

    public boolean isLast() {
        return last;
    }
}
