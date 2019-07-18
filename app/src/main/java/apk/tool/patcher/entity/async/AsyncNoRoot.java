package apk.tool.patcher.entity.async;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.afollestad.async.Action;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AsyncNoRoot extends Action<Integer> {
    private static final String TAG = "AsyncNoRoot";
    private int progress;

    @NonNull
    @Override
    public String id() {
        return "no-root";
    }

    @Nullable
    @Override
    protected Integer run(String... params) throws InterruptedException {
        Log.d(TAG, "run() called with: params = [" + Arrays.toString(params) + "]");
        //Здесь вся тяжелая работа
        try {
            Pattern pat = Pattern.compile("\"su\"|\"/su\"|\"/system/app/Superuser\\.apk\"|\"/sbin/su\"|\"/system/bin/su\"|\"/system/xbin/su\"|\"/data/local/xbin/su\"|\"/data/local/bin/su\"|\"/system/sd/xbin/su\"|\"/system/bin/failsafe/su\"|\"/data/local/su\"|\"busybox\"|\"Busybox\"|\"BusyBox\"");
            String replacement = "\"fuck\"";
            noRoot2(params[0], pat, replacement);
        } catch (Exception err) {
            postError(err);
            err.printStackTrace();
        }
        //Возврат должен быть... любой
        return progress;
    }

    private void noRoot2(String directoryName, Pattern pat, String replacement) {
        Log.d(TAG, "noRoot2() called with: directoryName = [" + directoryName + "], pat = [" + pat + "], replacement = [" + replacement + "]");
        File directory = new File(directoryName);
        byte[] bytes;
        BufferedInputStream buf;
        String content;
        FileOutputStream Out;
        OutputStreamWriter OutWriter;
        Matcher mat;
        File[] fList = directory.listFiles();
        for (File file : fList) {
            if (file.isFile()) {
                if (file.getAbsolutePath().endsWith(".smali")) {
                    try {
                        bytes = new byte[(int) file.length()];
                        buf = new BufferedInputStream(new FileInputStream(file));
                        buf.read(bytes, 0, bytes.length);
                        buf.close();
                        content = new String(bytes);
                        mat = pat.matcher(content);
                        if (mat.find()) {
                            progress++;
                            postProgress(this, progress);
                            content = mat.replaceAll(replacement);
                            Out = new FileOutputStream(file);
                            OutWriter = new OutputStreamWriter(Out);
                            OutWriter.append(content);
                            OutWriter.close();
                            Out.flush();
                            Out.close();
                        }
                    } catch (Exception err) {
                        postError(err);
                        err.printStackTrace();
                    }
                }
            } else if (file.isDirectory()) {
                noRoot2(file.getAbsolutePath(), pat, replacement);
            }
        }
    }
}
