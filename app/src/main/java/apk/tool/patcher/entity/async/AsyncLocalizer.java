package apk.tool.patcher.entity.async;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.afollestad.async.Action;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Locale;

import apk.tool.patcher.util.Preferences;

public class AsyncLocalizer extends Action<Integer> {
    private static final String TAG = "AsyncLocalizer";
    private int s;

    private int totalDefault(String filePath) {
        Log.d(TAG, "totalDefault() called with: filePath = [" + filePath + "]");
        filePath += "/res/values/strings.xml";
        File file = new File(filePath);
        int l = 0;
        BufferedReader bufferedReader;
        try {
            FileReader fileReader = new FileReader(file);
            bufferedReader = new BufferedReader(fileReader);
            while (bufferedReader.readLine() != null)
                l++; // количество строк в /res/values/strings.xml
            bufferedReader.close();
        } catch (Exception err) {
            postError(err);
            err.printStackTrace();
        }
        postEvent(String.format(Locale.ENGLISH, "Number of Strings in /res/values/strings.xml: %d", l));
        return l;
    }

    @NonNull
    @Override
    public String id() {
        return "remove-locales";
    }

    @Nullable
    @Override
    protected Integer run(String... params) throws InterruptedException {
        Log.d(TAG, "run() called with: params = [" + Arrays.toString(params) + "]");
        //Здесь вся тяжелая работа
        try {
            int total = totalDefault(params[0]);
            delLocale(params[0] + "/res", total);
        } catch (Exception err) {
            postError(err);
            err.printStackTrace();
        }
        //Возврат должен быть... любой
        return progress;
    }

    private void delLocale(String filePath, int totalStrings) {
        Log.d(TAG, "delLocale() called with: filePath = [" + filePath + "], totalStrings = [" + totalStrings + "]");
        File resDir = new File(filePath);
        File[] dirs = resDir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if (pathname.isDirectory()) {
                    if (!Preferences.isExperimentalMode()) {
                        if (pathname.getName().contains("values-ru") ||
                                pathname.getName().contains("values-uk") ||
                                !pathname.getName().contains("values")) {
                            return false;
                        }

                        return pathname.getName().contains("values-");
                    } else {
                        return !Preferences.hasExcludedLanguage(pathname.getName());
                    }
                }
                return false;
            }
        });

        for (File file : dirs) {
            File[] filesInDir = file.listFiles();
            for (File fileInDir : filesInDir) {
                if (fileInDir.isFile()) {
                    if (fileInDir.getAbsolutePath().endsWith("strings.xml")) {
                        s = 0;
                        BufferedReader bufferedReader;
                        try {
                            FileReader fileReader = new FileReader(fileInDir);
                            bufferedReader = new BufferedReader(fileReader);
                            while (bufferedReader.readLine() != null)
                                s++; // количество строк в других string.xml
                            bufferedReader.close();
                            if (s <= totalStrings) { // если в /res/values/strings.xml строк больше или равно, чем в остальных, тогда удаляем.
                                fileInDir.delete();
                                progress++;
                                if (ETA + 299 < progress) {
                                    postProgress(this, progress);
                                    ETA = progress;
                                }
                            }
                            s = 0;
                        } catch (Exception err) {
                            postError(err);
                            err.printStackTrace();
                        }
                    }
                }

            }
        }
    }
}
