package apk.tool.patcher.entity;

import android.os.Handler;
import android.util.Log;

import androidx.collection.ArrayMap;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdsPatcher {
    private static final String TAG = "AdsPatcher";
    public static String smali = ".smali";
    public static String xml = ".xml";
    public static String nol = "";

    private static AdsPatcher repository;

    public AdsPatcher() {
        repository = this;
    }

    public static AdsPatcher get() {
        if (repository == null) {
            repository = new AdsPatcher();
        }
        return repository;
    }

    //ЗАМЕНА В СМАЛИ
    public void RemoveAds1(Handler h, int i, String directoryName, ArrayMap<Pattern, String> pat) {
        Log.d(TAG, "RemoveAds1() called with: h = [" + h + "], i = [" + i + "], directoryName = [" + directoryName + "], pat = [" + pat + "]");
        File directory = new File(directoryName);
        byte[] bytes;
        BufferedInputStream buf;
        String content;
        FileOutputStream Out;
        OutputStreamWriter OutWriter;
        Matcher mat;
        boolean saveFile = false;
        File[] fList = directory.listFiles();
        for (File file : fList) {
            if (file.isFile()) {
                if (file.getAbsolutePath().endsWith(smali)) {
                    try {
                        bytes = new byte[(int) file.length()];
                        buf = new BufferedInputStream(new FileInputStream(file));
                        buf.read(bytes, 0, bytes.length);
                        buf.close();
                        content = new String(bytes);
                        for (ArrayMap.Entry<Pattern, String> mEntry : pat.entrySet()) {

                            mat = mEntry.getKey().matcher(content);
                            if (mat.find()) {
                                i++;
                                h.sendEmptyMessage(i);
                                content = mat.replaceAll(mEntry.getValue());
                                saveFile = true;
                            }
                        }
                        if (saveFile) {
                            Out = new FileOutputStream(file);
                            OutWriter = new OutputStreamWriter(Out);
                            OutWriter.append(content);
                            OutWriter.close();
                            Out.flush();
                            Out.close();
                            saveFile = false;
                        }
                    } catch (Exception e) {
                        System.out.println(e.toString());
                    }
                }
            } else if (file.isDirectory()) {
                RemoveAds1(h, i, file.getAbsolutePath(), pat);
            }
        }
    }
}
