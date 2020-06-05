package apk.tool.patcher.util;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import apk.tool.patcher.App;
import apk.tool.patcher.net.Client;
import apk.tool.patcher.net.api.NetworkRequest;
import apk.tool.patcher.net.api.NetworkResponse;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Snow Volf on 01.10.2017, 0:50
 */

public class IntentHandler {
    private static final String TAG = "IntentHandler";

    public static void handleDownload(Context context, String url) {
        Log.d(TAG, "handleDownload " + url);
        String fileName = url;
        try {
            fileName = URLDecoder.decode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        int cut = fileName.lastIndexOf('/');
        if (cut != -1) {
            fileName = fileName.substring(cut + 1);
        }
        handleDownload(context, fileName, url);
    }

    public static void handleDownload(Context context, String fileName, String url) {
        Log.d(TAG, "handleDownload " + fileName + " : " + url);
        new AlertDialog.Builder(context)
                .setItems(new CharSequence[]{"System", "Custom"}, (dialogInterface, i) -> {
                    switch (i){
                        case 0:
                            systemDownloader(fileName, url);
                            break;
                        case 1:
                            redirectDownload(fileName, url);
                            break;
                    }
                })
                .show();
    }

    private static void redirectDownload(String fileName, String url) {
        Toast.makeText(App.get(), fileName, Toast.LENGTH_SHORT).show();
        Observable.fromCallable(() -> Client.get().request(new NetworkRequest.Builder().url(url).withoutBody().build()))
                .onErrorReturn(throwable -> new NetworkResponse(null))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if (response.getUrl() == null) {
                        Toast.makeText(App.get(), "Error", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    externalDownloader(response.getRedirect());
                });
    }

    private static void systemDownloader(String fileName, String url) {
        DownloadManager dm = (DownloadManager) App.get().getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
        request.setMimeType("application/vnd.android.package-archive");
        dm.enqueue(request);
    }

    public static void externalDownloader(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        App.get().startActivity(Intent.createChooser(intent, null).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }
}