package apk.tool.patcher.entity.async;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.util.Log;

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

public class AsyncUpdater extends Action<Integer> {
    private static final String TAG = "AsyncUpdater";

    @NonNull
    @Override
    public String id() {
        return "disable-update";
    }

    @Nullable
    @Override
    protected Integer run(String... params) throws InterruptedException {
        Log.d(TAG, "run() called with: params = [" + Arrays.toString(params) + "]");
        //Здесь вся тяжелая работа
        try {
            String directoryName = params[0] + "/apktool.yml";
            upgradeAppVersion(directoryName, params[0]);
        } catch (Exception err) {
            postError(err);
            err.printStackTrace();
        }
        postEvent(String.format(Locale.ENGLISH, "%d matches replaced", progress));
        //Возврат должен быть... любой
        return progress;
    }

    private void upgradeAppVersion(String filePath, String patch) {
        Log.d(TAG, "upgradeAppVersion() called with: filePath = [" + filePath + "], patch = [" + patch + "]");
        String regexp = "versionName: (.+)";
        Pattern pattern = Pattern.compile(regexp);
        String regexp3 = "versionCode: '(.+)'";
        Pattern pattern3 = Pattern.compile(regexp3);
        File fileToBeModified = new File(filePath); // путь до файла
        StringBuilder url = new StringBuilder();
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
                String name = "\"" + m.group(1) + "\"";
                String name1 = name.replace("(", "\\(");
                String name2 = name1.replace(")", "\\)");
                url = new StringBuilder(m.replaceAll("versionName: 999999"));
                Matcher m2 = pattern3.matcher(url.toString());
                if (m2.find()) {
                    String code = "\"" + m2.group(1) + "\"";
                    url = new StringBuilder(m2.replaceAll("versionCode: '999999'"));
                    ArrayMap<Pattern, String> pat = new ArrayMap<Pattern, String>();
                    pat.put(Pattern.compile(name2), "\"999999\"");
                    pat.put(Pattern.compile(code), "\"999999\"");
                    noUpgrade(patch, pat);
                }
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

    private void noUpgrade(String directoryName, ArrayMap<Pattern, String> pat) {
        Log.d(TAG, "noUpgrade() called with: directoryName = [" + directoryName + "], pat = [" + pat + "]");
        File directory = new File(directoryName);
        byte[] bytes;
        BufferedInputStream buf;
        String content = "";
        FileOutputStream Out;
        OutputStreamWriter OutWriter;
        Matcher mat;
        boolean saveFile = false;
        File[] fList = directory.listFiles();
        for (File file : fList) {
            if (file.isFile()) {
                if (!file.getName().contains(".") || file.getAbsolutePath().endsWith(".xml") ||
                        file.getAbsolutePath().endsWith(".smali") || file.getAbsolutePath().endsWith(".txt") ||
                        file.getAbsolutePath().endsWith(".js")) {
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
                        System.out.println(e.toString());
                    }
                }
            } else if (file.isDirectory()) {
                noUpgrade(file.getAbsolutePath(), pat);
            }
        }
    }
}
