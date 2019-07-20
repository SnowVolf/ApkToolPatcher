package apk.tool.patcher.entity.async;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.ArrayMap;

import com.afollestad.async.Action;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import apk.tool.patcher.util.Preferences;

public class AsyncPlayServices extends Action<Integer> {
    private static final String TAG = "AsyncPlayServices";
    private int progress;

    @NonNull
    @Override
    public String id() {
        return "play-services";
    }

    @Nullable
    @Override
    protected Integer run(String... params) throws InterruptedException {
        Log.d(TAG, "run() called with: params = [" + Arrays.toString(params) + "]");
        //Здесь вся тяжелая работа
        try {
            ArrayMap<Pattern, String> pat = new ArrayMap<Pattern, String>();
            pat.put(Pattern.compile("\\(Landroid/content/Context;\\)I\n {4}\\.locals (\\d+)\n {4}(\\.annotation runtime Ljava/lang/Deprecated;\n|\\.param.+\n {4}\\.annotation runtime Ljava/lang/Deprecated;\n) {4}\\.end annotation"), "\\(Landroid/content/Context;\\)SysUtils\n    \\.registers $1\n    \\.annotation runtime Ljava/lang/Deprecated;\n    \\.end annotation\n   const/4 v0, 0x0\n    return v0 #htc600");
            RemoveAds1(params[0], pat);
        } catch (Exception err) {
            postError(err);
            err.printStackTrace();
        }
        postEvent(String.format(Locale.ENGLISH, "%d matches replaced", progress));
        //Возврат должен быть... любой
        return progress;
    }

    public void RemoveAds1(String directoryName, ArrayMap<Pattern, String> pat) {
        Log.d(TAG, "RemoveAds1() called with: directoryName = [" + directoryName + "], pat = [" + pat + "]");
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
                if (file.getAbsolutePath().endsWith(".smali") && !Preferences.hasExcludedPackage(file.getAbsolutePath())) {
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
                                postProgress(this, progress);
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
                        postError(e);
                        System.out.println(e.toString());
                    }
                }
            } else if (file.isDirectory()) {
                RemoveAds1(file.getAbsolutePath(), pat);
            }
        }
    }
}
