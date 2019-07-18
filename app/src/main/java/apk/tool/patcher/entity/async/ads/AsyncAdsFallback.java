package apk.tool.patcher.entity.async.ads;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.afollestad.async.Action;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import apk.tool.patcher.App;
import apk.tool.patcher.util.Preferences;

public class AsyncAdsFallback extends Action<Integer> {
    private static final String TAG = "AsyncAdsFallback";
    public static String smali = ".smali";
    public static String xml = ".xml";
    public static String nol = "";
    private int progress;

    @NonNull
    @Override
    public String id() {
        return "remove-ads-fallback";
    }

    @Nullable
    @Override
    protected Integer run(String... params) throws InterruptedException {
        Log.d(TAG, "run() called with: params = [" + Arrays.toString(params) + "]");
        //Здесь вся тяжелая работа
        try {
            Pattern pat3 = Pattern.compile("(<\\S*[^<]*)(android:id=\"@id/(?:[Aa][Dd][Ss]|[Bb][Aa][Nn][Nn][Ee][Rr]|[Aa][Dd][Vv][Ii][Ee][Ww]|[Aa][Dd][Vv][Ii][Ee][Ww]Layout)\") (android:layout_.+)=\"(.+nt)\" (.+)");
            RemoveAds2(params[0] + "/res", pat3);
            postEvent(String.format(Locale.ENGLISH, "%d matches replaced", progress));
        } catch (Exception err) {
            postError(err);
            err.printStackTrace();
        }
        //Возврат должен быть... любой
        return progress;
    }

    //НЕ ТРОГАТЬ - ИНДИВИДУАЛЬНЫЙ МЕТОД ДЛЯ СКРЫТИЯ БАНЕРОВ В РАЗМЕТКЕ
    public void RemoveAds2(String directoryName, Pattern pat) {
        File directory = new File(directoryName);
        byte[] bytes;
        BufferedInputStream buf;
        String content = nol;
        FileOutputStream Out;
        OutputStreamWriter OutWriter;
        Matcher m;
        File[] fList = directory.listFiles();
        for (File file : fList) {
            if (file.isFile()) {
                if (file.getAbsolutePath().endsWith(xml) && !Preferences.hasExcludedPackage(file.getAbsolutePath())) {
                    try {
                        bytes = new byte[(int) file.length()];
                        buf = new BufferedInputStream(new FileInputStream(file));
                        buf.read(bytes, 0, bytes.length);
                        buf.close();
                        content = new String(bytes);
                        m = pat.matcher(content);
                        if (m.find()) //если что-нибудь нашлось в файле, то все заменить и сохранить файл
                        {
                            progress++;
                            if (ETA + 20 < progress) {
                                postProgress(this, progress);
                                ETA = progress;
                            }
                            content = m.replaceAll(m.group(1) + m.group(2) + " " + m.group(3) + "=\"0.0dip\" " + m.group(5));
                            Out = new FileOutputStream(file);
                            OutWriter = new OutputStreamWriter(Out);
                            OutWriter.append(content);
                            OutWriter.close();
                            Out.flush();
                            Out.close();
                        }
                    } catch (IOException e) {
                        Toast.makeText(App.get(), e.toString(), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(App.get(), e.toString(), Toast.LENGTH_LONG).show();
                    }
                }
            } else if (file.isDirectory()) {
                RemoveAds2(file.getAbsolutePath(), pat);
            }
        }
    }
}
