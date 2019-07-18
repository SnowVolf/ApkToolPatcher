package apk.tool.patcher.entity;

import android.app.Activity;
import android.content.Intent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class LogicalCore {
    public static String smali = ".smali";

    private static LogicalCore repository;
    public ArrayList<File> FileOrDirList = new ArrayList<File>(); //это список всех файлов или папок

    public LogicalCore() {
        repository = this;
    }

    public static LogicalCore get() {
        if (repository == null) {
            repository = new LogicalCore();
        }
        return repository;
    }

    public static void mailedoc(Activity activity, String muimerp, String secret) throws UnsupportedEncodingException {
        byte[] data = muimerp.getBytes(StandardCharsets.UTF_8);
        secret = android.util.Base64.encodeToString(data, android.util.Base64.DEFAULT);

        // Receiving side
        //   byte[] data = Base64.decode(base64, Base64.DEFAULT);
        //  String text = new String(data, "UTF-8");
        final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);

        emailIntent.setType("plain/text");
        // Кому
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
                new String[]{
                        "buntar888@mail.ru"
                });
        // Зачем
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
                "Покупка ApkToolPatcher");
        // О чём
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT,
                secret + "\nНе удаляйте текст выше⬆⬆⬆");
        // С чем
        //    emailIntent.putExtra(
        //        android.content.Intent.EXTRA_STREAM,
        //        Uri.parse("file://"
        //                  + Environment.getExternalStorageDirectory()
        //                  + "/Клипы/SOTY_ATHD.mp4"));
        //
        //    emailIntent.setType("text/video");
        // Поехали!
        activity.startActivity(Intent.createChooser(emailIntent,
                null));
    }

    public static void copyDex(String oldFile, String newFile) throws IOException {


        ZipInputStream zin = new ZipInputStream(new FileInputStream(oldFile));

        ZipEntry entry;
        String name;
        int length;

        while ((entry = zin.getNextEntry()) != null) {
            name = entry.getName(); // получим название файла
            if (name.equals("classes.dex")) {
                // распаковка
                FileOutputStream fout = new FileOutputStream(newFile + "/xlasses.dex");
                byte[] buffer = new byte[1024];
                while ((length = zin.read(buffer)) != -1) {
                    fout.write(buffer, 0, length);
                }
                fout.flush();
                zin.closeEntry();
                fout.close();
            }
        }
    }

}
