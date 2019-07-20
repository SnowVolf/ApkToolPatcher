package apk.tool.patcher.entity.async;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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

public class AsyncUnescaper extends Action<Integer> {
    private static final String TAG = "AsyncUnescaper";

    @NonNull
    @Override
    public String id() {
        return "unicode-to-utf";
    }

    @Nullable
    @Override
    protected Integer run(String... params) throws InterruptedException {
        Log.d(TAG, "run() called with: params = [" + Arrays.toString(params) + "]");
        //Здесь вся тяжелая работа
        try {
            String regexp = "const-string [pv]\\d+,.+\\\\u.{4}.*\"";
            Pattern pat = Pattern.compile(regexp);
            unicodeToUtf(params[0], pat);
        } catch (Exception err) {
            postError(err);
            err.printStackTrace();
        }
        postEvent(String.format(Locale.ENGLISH, "%d matches replaced", progress));
        //Возврат должен быть... любой
        return progress;
    }

    private void unicodeToUtf(String patch, Pattern pat) {
        Log.d(TAG, "unicodeToUtf() called with: patch = [" + patch + "], pat = [" + pat + "]");
        String input;
        File directory = new File(patch);
        byte[] bytes;
        BufferedInputStream buf;
        String content;
        FileOutputStream Out;
        OutputStreamWriter OutWriter;
        Matcher mat;
        Matcher mat2;
        Pattern p = Pattern.compile("\\\\u(.{4})");
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
                        mat = pat.matcher(content);
                        while (mat.find()) {
                            mat2 = p.matcher(mat.group(0));
                            input = mat.group(0);

                            while (mat2.find()) {
                                input = input.replace("\\u" + mat2.group(1), String.valueOf((char) Integer.parseInt(mat2.group(1), 16)));
                            }
                            content = content.replace(mat.group(0), mat.group(0) + "    #" + input);
                            progress++;
                            if (ETA + 299 < progress) {
                                postProgress(this, progress);
                                ETA = progress;
                            }
                            Out = new FileOutputStream(file);
                            OutWriter = new OutputStreamWriter(Out);
                            OutWriter.append(content);
                            OutWriter.close();
                            Out.flush();
                            Out.close();
                        }
                    } catch (Exception e) {
                        System.out.println(e.toString());
                    }
                }
            } else if (file.isDirectory()) {
                unicodeToUtf(file.getAbsolutePath(), pat);
            }
        }
    }
}
