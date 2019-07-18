package apk.tool.patcher.entity.async;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

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

public class AsyncDictionary extends Action<Integer> {
    private static final String TAG = "AsyncDictionary";

    @NonNull
    @Override
    public String id() {
        return "convert-dictionary";
    }

    @Nullable
    @Override
    protected Integer run(String... params) throws InterruptedException {
        Log.d(TAG, "run() called with: params = [" + Arrays.toString(params) + "]");
        //Здесь вся тяжелая работа
        try {
            String directoryName = params[0] + "/dic.xml";
            convertDictionary(directoryName);
        } catch (Exception err) {
            postError(err);
            err.printStackTrace();
        }
        //Возврат должен быть... любой
        postEvent(String.format(Locale.ENGLISH, "%d matches replaced", progress));
        return progress;
    }

    public void convertDictionary(String filePath) {
        String regexp = "\\<\\?xml version=\"1\\.0\" encoding=\"UTF-8\" standalone=\"(yes|no)\"\\?\\>\n\\<translations name=\"(.+)\" version=\"1\\.0\"\\>\n";
        Pattern pattern = Pattern.compile(regexp);
        String regexp1 = "\\<translate from=(.+) to=(.+) /\\>";
        Pattern pattern1 = Pattern.compile(regexp1);
        String regexp4 = "\\</translations\\>";
        Pattern pattern4 = Pattern.compile(regexp4);
        String regexp5 = "\\\\&quot;";
        Pattern pattern5 = Pattern.compile(regexp5);
        File fileToBeModified = new File(filePath); // путь до файла
        String url;
        BufferedReader reader = null;
        FileWriter writer = null;
        try {
            reader = new BufferedReader(new FileReader(fileToBeModified));
            String line = reader.readLine();
            StringBuilder urlBuilder = new StringBuilder();
            while (line != null) {
                progress++;
                if (ETA + 299 < progress) {
                    postProgress(this, progress);
                    ETA = progress;
                }
                urlBuilder.append(line).append("\n");
                line = reader.readLine();
            }
            url = urlBuilder.toString();
            Matcher m = pattern.matcher(url);
            if (m.find()) {
                url = m.replaceAll("");
                Matcher m2 = pattern1.matcher(url);
                if (m2.find()) {
                    url = m2.replaceAll("{\n$1\n$2\n}");
                    Matcher m5 = pattern4.matcher(url);
                    if (m5.find()) {
                        url = m5.replaceAll("");
                        Matcher m6 = pattern5.matcher(url);
                        if (m6.find()) {
                            url = m6.replaceAll("");
                        }
                    }
                }
            }
            writer = new FileWriter(fileToBeModified);
            writer.write(url);
            postEvent(String.format("Converted dictionary has been successfully saved as: %s", fileToBeModified.getAbsolutePath()));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            postEvent("Close streams...");
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
