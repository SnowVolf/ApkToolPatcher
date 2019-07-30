package jp.sblo.pandora.aGrep;

public class CheckedString {
    public boolean checked;
    public String string;

    public CheckedString(String str) {
        this(true, str);
    }

    public CheckedString(boolean checked, String str) {
        this.checked = checked;
        string = str;
    }

    public String toString() {
        return (checked ? "true" : "false") + "|" + string;
    }

}
