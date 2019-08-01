package apk.tool.patcher.util;


import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

import apk.tool.patcher.App;
import apk.tool.patcher.filesystem.ExternalCard;
import ru.svolf.melissa.compat.PropertiesCompat;

public class SystemF {
    private static final String PREFIX_STR = "(s)";
    private static final int PREFIX_STR_LENGTH = PREFIX_STR.length();

    /**
     * Gets the number of cores available in this device, across all processors.
     * Requires: Ability to peruse the filesystem at "/sys/devices/system/cpu"
     *
     * @return The number of cores, or 1 if failed to get result
     */
    @SuppressWarnings("WeakerAccess")
    public static int getCpuNumber() {
        //Private Class to display only CPU devices in the directory listing
        class CpuFilter implements FileFilter {
            @Override
            public boolean accept(File pathname) {
                //Check if filename is "cpu", followed by a single digit number
                return Pattern.matches("cpu[0-9]+", pathname.getName());
            }
        }

        int cpuNum;

        try {
            //Get directory containing CPU info
            File dir = new File("/sys/devices/system/cpu/");
            //Filter to only list the devices we care about
            File[] files = dir.listFiles(new CpuFilter());
            //Return the number of cores (virtual CPU devices)
            cpuNum = files.length;
        } catch (Exception e) {
            //Default to return 1 core
            cpuNum = 1;
        }

        cpuNum = Math.max(cpuNum, Runtime.getRuntime().availableProcessors());
        return cpuNum > 0 ? cpuNum : 1;
    }

    @SuppressWarnings("WeakerAccess")
    public static long getFreeMem() {
        long freeMem;
        MemoryInfo memoryInfo = new MemoryInfo();
        ((ActivityManager) App.get().getSystemService(Context.ACTIVITY_SERVICE)).getMemoryInfo(memoryInfo);
        freeMem = memoryInfo.totalMem - memoryInfo.threshold;
        return (freeMem < 0x4000000L) ? 0x4000000L : freeMem;
    }

    @SuppressWarnings("WeakerAccess")
    public static long getTotalMem() {
        long totalMem;
        MemoryInfo memoryInfo = new MemoryInfo();
        ((ActivityManager) App.get().getSystemService(Context.ACTIVITY_SERVICE)).getMemoryInfo(memoryInfo);
        totalMem = memoryInfo.totalMem;
        return totalMem;
    }

    @SuppressWarnings("WeakerAccess")
    public static String getExtCardSettingsName() {
        String path = ExternalCard.getPath(true);
        return path == null ? null : PathF.addEndSlash(path) + ".settings";
    }

    private static HashMap<String, String> getExtCardSettings() {
        String extCardSettingsName = getExtCardSettingsName();
        if (extCardSettingsName == null) {
            return null;
        }
        HashMap<String, String> hashMap = new HashMap<>();
        try {
            PropertiesCompat properties = new PropertiesCompat();
            properties.load(new FileInputStream(extCardSettingsName));
            for (String s : properties.stringPropertyNamesSupport()) {
                hashMap.put(s, properties.get(s).toString());
            }
        } catch (Exception ignored) {
        }
        return hashMap;
    }

    @SuppressWarnings("WeakerAccess")
    public static void addExtCardSettingsString(String key, String uriPath) {
        Map<String, String> extCardSettings = getExtCardSettings();
        if (extCardSettings != null) {
            extCardSettings.put(key, uriPath == null ? "null" : PREFIX_STR + uriPath);
            String extCardSettingsName = getExtCardSettingsName();
            if (extCardSettingsName != null) {
                try {
                    Properties properties = new Properties();
                    properties.putAll(extCardSettings);
                    properties.store(new FileOutputStream(extCardSettingsName), null);
                } catch (Exception ignored) {
                }
            }
        }
    }

    @SuppressWarnings("WeakerAccess")
    public static String getExtCardSettingsString(String key) {
        HashMap<String, String> extCardSettings = getExtCardSettings();
        if (extCardSettings == null) {
            return null;
        }
        String uriPath = extCardSettings.get(key);
        return (uriPath == null || !uriPath.startsWith(PREFIX_STR)) ? null : uriPath.substring(PREFIX_STR_LENGTH);
    }

    public static long toKBs(long sizeInBytes) {
        return sizeInBytes / 1024;
    }

    public static long toMBs(long sizeInBytes) {
        return toKBs(sizeInBytes) / 1024;
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
