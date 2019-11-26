package ru.svolf.melissa.widget.crumb;

import java.util.List;

public class PathItem {
    private String path;
    private List<String> parents;
    private String folderName;
    private boolean last = false;

    public PathItem(String folderName, List<String> parents, boolean isLast) {
        this.folderName = folderName;
        this.parents = parents;
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

    public void setParents(List<String> parents){
        this.parents = parents;
    }

    public String getPath() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("/");
        for (int i = 0; i < parents.size(); i++) {
            stringBuilder.append(parents.get(i));
            stringBuilder.append("/");
        }

        String s = stringBuilder.toString();

        return s.substring(0, s.length() - 1);
    }
}
