package apk.tool.patcher.entity.async.ads;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.afollestad.async.Action;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import apk.tool.patcher.util.RegexpRepository;

public class AsyncAdsActivities extends Action<Integer> {
    private static final String TAG = "AsyncAdsActivities";
    public static String smali = ".smali";
    public static String xml = ".xml";
    public static String nol = "";
    private int progress;

    @NonNull
    @Override
    public String id() {
        return "remove-ads-activities";
    }

    @Nullable
    @Override
    protected Integer run(String... params) throws InterruptedException {
        Log.d(TAG, "run() called with: params = [" + Arrays.toString(params) + "]");
        //Здесь вся тяжелая работа
        try {
            deleteByPattern(params[0] + "/AndroidManifest.xml", RegexpRepository.get().ADS_ACTS);

//            int d = -1;
//            mProgressHandler.sendEmptyMessage(d);
        } catch (Exception err) {
            postError(err);
            err.printStackTrace();
        }
        postEvent(String.format(Locale.ENGLISH, "%d matches replaced", progress));
        //Возврат должен быть... любой
        return progress;
    }

    private void deleteByPattern(String filePath, Pattern pattern) {
        Log.d(TAG, "deleteByPattern() called with: filePath = [" + filePath + "], pattern = [" + pattern + "]");
        File fileToBeModified = new File(filePath);
        StringBuilder url = new StringBuilder(nol);
        BufferedReader reader = null;
        FileWriter writer = null;
        try {
            reader = new BufferedReader(new FileReader(fileToBeModified));
            String line = reader.readLine();
            while (line != null) {
                url.append(line).append("\n");
                line = reader.readLine();
            }
            Matcher m = pattern.matcher(url.toString());
            if (m.find()) {
                progress++;
                if (ETA + 20 < progress) {
                    postProgress(this, progress);
                    ETA = progress;
                }
                url = new StringBuilder(m.replaceAll(""));
            }
            writer = new FileWriter(fileToBeModified);
            writer.write(url.toString());
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
