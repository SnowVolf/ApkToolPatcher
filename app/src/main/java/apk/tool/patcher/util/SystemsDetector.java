package apk.tool.patcher.util;

/**
 * Created by SVolf on 05.02.2019, 16:17.
 */
public class SystemsDetector {

    public static boolean isBomzhomi() {
        return !getSystemProperty("ro.miui.ui.version.name").isEmpty();
    }

    private static String getSystemProperty(String name) {
        try {
            Class props = Class.forName("android.os.SystemProperties");
            return (String) props.getMethod("get", String.class).invoke(null, name);
        } catch (Exception e) {
            return "";
        }
    }
}