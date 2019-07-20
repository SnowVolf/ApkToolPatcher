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
import apk.tool.patcher.util.SystemF;

public class AsyncObfuscator extends Action<Integer> {
    private static final String TAG = "AsyncObfuscator";

    @NonNull
    @Override
    public String id() {
        return "obfuscation";
    }

    @Nullable
    @Override
    protected Integer run(String... params) throws InterruptedException {
        Log.d(TAG, "run() called with: params = [" + Arrays.toString(params) + "]");
        try {
            ArrayMap<Pattern, String> pat = new ArrayMap<>();
            pat.put(Pattern.compile(" {4}return-void"), "    const/4 v0, 0x1\n    return-void");
            pat.put(Pattern.compile("\\.source \"(.+)"), ".source \"SourceFile\"");
            obfuscate(params[0], pat);
        } catch (Exception err) {
            postError(err);
            err.printStackTrace();
        }
        postEvent(String.format(Locale.ENGLISH, "%d matches replaced", progress));
        //Возврат должен быть... любой
        return progress;
    }

    private void obfuscate(String directoryName, ArrayMap<Pattern, String> pattern) {
        Log.d(TAG, "obfuscate() called with: directoryName = [" + directoryName + "], pattern = [" + pattern + "]");
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
                    // Не открываем файлы больше 500 КБ
                    if (SystemF.toKBs(file.length()) < 500L) {
                        try {
                            bytes = new byte[(int) file.length()];
                            buf = new BufferedInputStream(new FileInputStream(file));
                            buf.read(bytes, 0, bytes.length);
                            buf.close();
                            content = new String(bytes);

                            for (ArrayMap.Entry<Pattern, String> mEntry : pattern.entrySet()) {
                                mat = mEntry.getKey().matcher(content);
                                if (mat.find()) {
                                    progress++;
                                    if (ETA + 999 < progress) {
                                        Log.d(TAG, "obfuscate: update UI");
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
                        } catch (Exception e) {
                            Log.w(TAG, "obfuscate: failed to perform this action", e);
                        }
                    } else {
                        Log.d(TAG, "obfuscate: SKIP file larger than 500 KB");
                    }
                }
            } else if (file.isDirectory()) {
                obfuscate(file.getAbsolutePath(), pattern);
            }
        }
    }
}
