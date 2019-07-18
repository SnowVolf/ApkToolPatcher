package ru.atomofiron.apknator.Utils;


import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import apk.tool.patcher.util.SysUtils;

public class Node {
    public boolean extended = false;
    public String path = null;
    public String title = null;
    public int level = 0;
    public boolean isPackage = true;
    private Pattern pattern = Pattern.compile("[^/]+");

    public Node(String path) {
        this.path = path;
        this.title = path.substring(path.lastIndexOf('/') + 1);
    }

    public Node(String parent, String name) {
        set(parent, name);
    }

    private Node() {
    }

    public static ArrayList<Node> parse(String[] lines, String offset) { // Lcom/android/egg
        SysUtils.Log("parse() " + offset);
        ArrayList<Node> nodes = new ArrayList<>();
        Node node = new Node();
        String s;
        for (String str : lines) { // Lcom/android/egg/neko/Cat
            s = str;
            if (!s.startsWith(offset) || s.equals(offset))
                continue;

            if (!offset.isEmpty())
                s = s.substring(offset.length() + 1); // neko/Cat
            int index = s.indexOf('/');
            if (index != -1)
                s = s.substring(0, index); // neko

            node.set(offset, s); // мне почему-то кажется, что это сэкономит память
            if (!nodes.contains(node)) { // иначе добавляет повторяющиеся, проверено
                node.isPackage = !node.path.equals(str);
                nodes.add(node);
                node = new Node();
            }
        }

        SysUtils.Log("return nodes;");
        return nodes;
    }

    private void set(String parent, String name) {
        this.path = parent.isEmpty() ? name : parent + '/' + name;
        this.title = name;

        level = 0;
        Matcher m = pattern.matcher(parent);
        while (m.find())
            level++;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !getClass().equals(obj.getClass()))
            return false;
        Node n = (Node) obj;
        return title == null && n.title == null || title.equals(n.title) && path.equals(n.path);

    }
}
