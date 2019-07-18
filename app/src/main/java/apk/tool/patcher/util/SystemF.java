package apk.tool.patcher.util;


import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;

import java.io.File;
import java.io.FileFilter;
import java.util.regex.Pattern;

import apk.tool.patcher.App;

public class SystemF {

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

    public static long toKBs(long sizeInBytes) {
        return sizeInBytes / 1024;
    }

    public static long toMBs(long sizeInBytes) {
        return toKBs(sizeInBytes) / 1024;
    }
}
