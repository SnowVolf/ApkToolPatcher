package apk.tool.patcher.entity.async;

import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.afollestad.async.Action;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import apk.tool.patcher.App;
import apk.tool.patcher.R;

public class AsyncToast extends Action<Integer> {
    private static final String TAG = "AsyncToast";
    private static final String saveToast = "text_toast";
    private static String lauchableActivity;

    @NonNull
    @Override
    public String id() {
        return "startup-toast";
    }

    @Nullable
    @Override
    protected Integer run(String... params) throws InterruptedException {
        Log.d(TAG, "run() called with: params = [" + Arrays.toString(params) + "]");
        try {
            startToast(params[0], params[1]);
        } catch (Exception err) {
            postError(err);
            err.printStackTrace();
        }
        postEvent(String.format(Locale.ENGLISH, "%d matches replaced", progress));
        //Возврат должен быть... любой
        return progress;
    }

    private void startToast(String dir, String message) {
        SharedPreferences sPref;
        try {
            copyFolder(dir + "/smali", "toast");

            sPref = App.get().getPreferences();
            SharedPreferences.Editor ed = sPref.edit();
            ed.putString(saveToast, message);
            ed.apply();
            activity(dir + "/AndroidManifest.xml");
            startToast2(dir + "/smali/" + lauchableActivity + ".smali", message);
            postEvent(App.bindString(R.string.message_done));
        } catch (Exception err) {
            sPref = App.get().getPreferences();
            SharedPreferences.Editor ed = sPref.edit();
            ed.putString(saveToast, message);
            ed.apply();
            postEvent(App.bindString(R.string.message_processing));
            startToast2(dir + "/smali_classes2/" + lauchableActivity + ".smali", message);
            postEvent(App.bindString(R.string.message_done));
        }
    }

    public void activity(String filePath) {
        Log.d(TAG, "activity() called with: filePath = [" + filePath + "]");
        postEvent("Resolve launcher activity for: " + filePath);
        String regexp = "package=\"([^\"]+)\"";
        Pattern pattern = Pattern.compile(regexp);
        String regexp2 = "<activity (.+) android:name=\"([^\"]+)\"(.+)";
        Pattern pattern2 = Pattern.compile(regexp2);
        File fileToBeModified = new File(filePath); // путь до файла
        String url;
        BufferedReader reader = null;
        FileWriter writer = null;
        try {
            Log.d(TAG, "activity: read manifest");
            reader = new BufferedReader(new FileReader(fileToBeModified));
            String line = reader.readLine();
            StringBuilder urlBuilder = new StringBuilder();
            while (line != null) {
                urlBuilder.append(line).append("\n");
                line = reader.readLine();
            }
            url = urlBuilder.toString();
            Log.d(TAG, "activity: parsed manifest: " + url);

            Matcher m1 = pattern2.matcher(url);

            if (m1.find())
                Log.d(TAG, "activity: matcher find");
            if (m1.group(2).contains("\\.*.\\.*")) {
                Log.d(TAG, "activity: matcher contains = " + "\\.*.\\.*");
                String activ2 = m1.group(2);
                Log.d(TAG, "activity: activ2 = " + activ2);
                lauchableActivity = activ2.replace(".", "/");
                Log.d(TAG, "activity: launchableActivity = " + lauchableActivity);
            } else {
                Log.d(TAG, "activity: matcher not found");
                Matcher m = pattern.matcher(url);
                Log.d(TAG, "activity: new matcher = " + m);
                if (m.find()) {
                    Log.d(TAG, "activity: new matcher find");
                    String packageName = m.group(1);
                    Log.d(TAG, "activity: package name = " + packageName);
                    String activityName = "." + m1.group(2);
                    Log.d(TAG, "activity: activity name = " + activityName);
                    String startIntent = packageName + activityName;
                    Log.d(TAG, "activity: startIntent = " + startIntent);
                    lauchableActivity = startIntent.replace(".", "/");
                    Log.d(TAG, "activity: launchableActivity = " + lauchableActivity);
                }
            }
            postEvent(String.format("Resolved Activity: %s", lauchableActivity));
            writer = new FileWriter(fileToBeModified);
            writer.write(url);
        } catch (IOException e) {
            postError(e);
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
                postError(e);
                e.printStackTrace();
            }
        }
    }

    private void startToast2(String filePath, String saveToast2) {
        Log.d(TAG, "startToast2() called with: filePath = [" + filePath + "], saveToast2 = [" + saveToast2 + "]");
        filePath = filePath.replace("//", "/");
        postEvent("Failed to add a toast with standard method. Use fallback strategy...");
        postEvent(filePath);
        String regexp = "\\.method (.+) onCreate\\(Landroid/os/Bundle;\\)V\n {4}\\.locals (\\d+)";
        Pattern pattern = Pattern.compile(regexp);

        File fileToBeModified = new File(filePath); // путь до файлa
        String url;
        BufferedReader reader = null;
        FileWriter writer = null;
        try {
            reader = new BufferedReader(new FileReader(fileToBeModified));
            String line = reader.readLine();
            StringBuilder urlBuilder = new StringBuilder();
            while (line != null) {
                urlBuilder.append(line).append("\n");
                line = reader.readLine();
            }
            url = urlBuilder.toString();
            Matcher m = pattern.matcher(url);
            if (m.find()) {
                url = m.replaceAll(m.group(0) + "\n        const-string v0, \"" + saveToast2 + "\"\n\n    invoke-static {p0, v0}, Lapkeditor/Utils;->showToast\\(Landroid/content/Context;Ljava/lang/String;\\)V");
            }
            writer = new FileWriter(fileToBeModified);
            writer.write(url);
        } catch (IOException e) {
            postError(e);
            e.printStackTrace();
        }
        try {
            if (reader != null) reader.close();
            if (writer != null) writer.close();
        } catch (IOException e) {
            postError(e);
            e.printStackTrace();
        }
    }

    private void copyFolder(String outPath, String basePath) {
        Log.d(TAG, "copyFolder() called with: outPath = [" + outPath + "], basePath = [" + basePath + "]");
        InputStream inputStream;
        AssetManager assetManager = App.get().getAssets();
        try {
            String[] assets = assetManager.list(basePath);
            for (String s : assets) {
                String[] tmp = assetManager.list(basePath + "/" + s);
                if (tmp.length > 0) {
                    File dir = new File(outPath + "/" + s);
                    dir.mkdir();
                    copyFolder(outPath + "/" + s, basePath + "/" + s);
                    continue;
                }
                byte[] inputBuffer = new byte[1000];
                int count;
                FileOutputStream f = new FileOutputStream(outPath + "/" + s);
                inputStream = assetManager.open(basePath + "/" + s);
                while ((count = inputStream.read(inputBuffer)) > 0)
                    f.write(inputBuffer, 0, count);
                f.close();
            }
        } catch (Exception e) {
            postError(e);
            e.printStackTrace();
        }
    }
}
