package apk.tool.patcher.entity.async.ads;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.ArrayMap;

import com.afollestad.async.Action;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AsyncAdsXml extends Action<Integer> {
    private static final String TAG = "AsyncAdsXml";
    public static String smali = ".smali";
    public static String xml = ".xml";
    public static String nol = "";
    private int progress;

    @NonNull
    @Override
    public String id() {
        return "remove-ads-xml";
    }

    @Nullable
    @Override
    protected Integer run(String... params) throws InterruptedException {
        Log.d(TAG, "run() called with: params = [" + Arrays.toString(params) + "]");
        //Здесь вся тяжелая работа
        try {
            ArrayMap<Pattern, String> pat1 = new ArrayMap<Pattern, String>();
            pat1.put(Pattern.compile("ca-app-pub"), "=");
            pat1.put(Pattern.compile("<com\\.google\\.android\\.gms\\.ads\\.AdView(.*)android:layout_width=\"(fill_parent|wrap_content)\" android:layout_height=\"(fill_parent|wrap_content)\"(.*)"), "<com.google.android.gms.ads.AdView$1android:layout_width=\"0dip\" android:layout_height=\"0dip\"$4");
            RemoveAdsXml(params[0] + "/res", pat1);
        } catch (Exception err) {
            postError(err);
            err.printStackTrace();
        }
        postEvent(String.format(Locale.ENGLISH, "%d matches replaced", progress));
        //Возврат должен быть... любой
        return progress;
    }

    public void RemoveAdsXml(String directoryName, ArrayMap<Pattern, String> pat) {
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
                if (file.getAbsolutePath().endsWith(xml)) {
                    try {
                        bytes = new byte[(int) file.length()];
                        buf = new BufferedInputStream(new FileInputStream(file));
                        buf.read(bytes, 0, bytes.length);
                        buf.close();
                        content = new String(bytes);
                        for (ArrayMap.Entry<Pattern, String> mEntry : pat.entrySet()) {
                            mat = mEntry.getKey().matcher(content);
                            if (mat.find()) {
                                progress++;
                                if (ETA + 20 < progress) {
                                    postProgress(this, progress);
                                    ETA = progress;
                                }
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
                    } catch (IOException e) {
                        System.out.println(e.toString());
                    } catch (Exception e) {
                        System.out.println(e.toString());
                    }
                }
            } else if (file.isDirectory()) {
                RemoveAdsXml(file.getAbsolutePath(), pat);
            }
        }
    }
}
