package jp.sblo.pandora.aGrep;

public class CheckedString {
    public boolean checked;
    public String string;

    public CheckedString(String _s) {
        this(true, _s);
    }

    public CheckedString(boolean _c, String _s) {
        checked = _c;
        string = _s;
    }

    public String toString() {
        return (checked ? "true" : "false") + "|" + string;
    }

}
