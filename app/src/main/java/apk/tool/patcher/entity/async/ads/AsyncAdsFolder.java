package apk.tool.patcher.entity.async.ads;

import android.content.res.AssetManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.afollestad.async.Action;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Locale;

import apk.tool.patcher.App;

public class AsyncAdsFolder extends Action<Integer> {
    private static final String TAG = "AsyncAdsFolder";
    public static String smali = ".smali";
    public static String xml = ".xml";

    @NonNull
    @Override
    public String id() {
        return "remove-ads-folder";
    }

    @Nullable
    @Override
    protected Integer run(String... params) throws InterruptedException {
        Log.d(TAG, "run() called with: params = [" + Arrays.toString(params) + "]");
        //Здесь вся тяжелая работа
        try {
            copyFolder(params[0] + "/smali", "RemoveAds");
            postProgress(this, progress);
        } catch (Exception err) {
            postError(err);
            err.printStackTrace();
        }
        postEvent(String.format(Locale.ENGLISH, "%d matches replaced", progress));
        //Возврат должен быть... любой
        return progress;
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
                progress = 1;
                f.close();
                postProgress(this, progress);
            }
        } catch (Exception e) {
            e.printStackTrace();
            progress = 0;
            postProgress(this, progress);
        }
    }
}
