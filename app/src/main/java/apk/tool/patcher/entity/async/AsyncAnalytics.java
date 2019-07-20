package apk.tool.patcher.entity.async;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.afollestad.async.Action;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import apk.tool.patcher.util.Preferences;

public class AsyncAnalytics extends Action<Integer> {
    private static final String TAG = "AsyncAnalytics";

    @NonNull
    @Override
    public String id() {
        return "remove-analytics";
    }

    @Nullable
    @Override
    protected Integer run(String... params) throws InterruptedException {
        Log.d(TAG, "run() called with: params = [" + Arrays.toString(params) + "]");
        //Здесь вся тяжелая работа
        try {
            Pattern pat = Pattern.compile("\"https://graph\\.%s\"|\".*api\\.branch\\.io\"|\".*crashlytics\\.com.*\"|\".*wzrkt\\.com*\"|\".*appboy\\.com*\"|\".*.appsflyer\\.com/.*\"|\".*google-analytics\\.com.*\"|\"ssl\\.google-analytics\\.com.*\"|\".*.google-analytics\\.com.*\"|\".*measurement\\.com.*\"|\".*data.flurry\\.com.*\"|\".*googletagmanager\\.com.*\"|\".*hockeyapp\\.net.*\"|\".*scorecardresearch\\.com.*\"|\".*YandexMetricaNativeModule*\"|\".*amplitude\\.com.*\"|\".*azure\\.com.*\"|\".*firebaseapp\\.com.*\"|\".*startappservice\\.com.*\"|\".*startappexchange\\.com.*\"|\".*smaato\\.com.*\"|\".*api\\.crittercism\\.com\"|\".*appmetrica\\.yandex\\.ru\"|\".*app\\.adjust\\.com\"|\".*cloudfront\\.net.*\"");
            String replacement = "\"fuck\"";
            noRoot2(params[0], pat, replacement);
            String regexp = "<service android:exported=\"(.+)\" android:name=\"com\\.google\\.firebase(.+)\">\n {9}<intent-filter android:priority=\"(.+)\">\n {13}<action android:name=\"com\\.google\\.firebase(.+)\" />\n {11}</intent-filter>\n {6}</servic {6}";
            Pattern pattern = Pattern.compile(regexp);
            deleteByPattern(params[0] + "/AndroidManifest.xml", pattern);
            String regexp2 = "<receiver android:exported=\"(.+)\" android:name=\"com\\.google\\.firebase(.+)\" />";
            Pattern pattern2 = Pattern.compile(regexp2);
            deleteByPattern(params[0] + "/AndroidManifest.xml", pattern2);
        } catch (Exception err) {
            postError(err);
            err.printStackTrace();
        }
        postEvent(String.format(Locale.ENGLISH, "%d matches replaced", progress));
        //Возврат должен быть... любой
        return progress;
    }

    private void noRoot2(String directoryName, Pattern pat, String replacement) {
        Log.d(TAG, "noRoot2() called with: directoryName = [" + directoryName + "], pat = [" + pat + "], replacement = [" + replacement + "]");
        File directory = new File(directoryName);
        byte[] bytes;
        BufferedInputStream buf;
        String content = "";
        FileOutputStream Out;
        OutputStreamWriter OutWriter;
        Matcher mat;
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
                        if (mat.find()) {
                            progress++;
                            if (ETA + 399 < progress) {
                                postProgress(this, progress);
                                ETA = progress;
                            }
                            content = mat.replaceAll(replacement);
                            Out = new FileOutputStream(file);
                            OutWriter = new OutputStreamWriter(Out);
                            OutWriter.append(content);
                            OutWriter.close();
                            Out.flush();
                            Out.close();
                        }
                    } catch (IOException e) {
                        //Toast.makeText(mContext, e.toString(), Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        //Toast.makeText(mContext, e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            } else if (file.isDirectory()) {
                noRoot2(file.getAbsolutePath(), pat, replacement);
            }
        }
    }

    public void deleteByPattern(String filePath, Pattern pattern) {
        File fileToBeModified = new File(filePath);
        String url = "";
        BufferedReader reader = null;
        FileWriter writer = null;
        try {
            reader = new BufferedReader(new FileReader(fileToBeModified));
            String line = reader.readLine();
            while (line != null) {
                url = url + line + "\n";
                line = reader.readLine();
            }
            Matcher m = pattern.matcher(url);
            if (m.find()) {
//                i++;
//                mProgressHandler.sendEmptyMessage(i);
                url = m.replaceAll("");
            }
            writer = new FileWriter(fileToBeModified);
            writer.write(url);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
