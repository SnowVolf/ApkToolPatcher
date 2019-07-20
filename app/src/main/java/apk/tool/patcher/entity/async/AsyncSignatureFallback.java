package apk.tool.patcher.entity.async;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.afollestad.async.Action;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Locale;

public class AsyncSignatureFallback extends Action<Integer> {
    public static String smali = ".smali";
    public static String xml = ".xml";
    private static byte[] signatures;
    private static String apli;

    public static void createDir(String folder) {
        File f1 = new File(folder); //Создаем файловую переменную
        if (!f1.exists()) { //Если папка не существует
            //noinspection ResultOfMethodCallIgnored
            f1.mkdirs(); //создаем её
        }
    }

    @NonNull
    @Override
    public String id() {
        return "signature-fallback";
    }

    @Nullable
    @Override
    protected Integer run(String... params) throws InterruptedException {
        //Здесь вся тяжелая работа
        try {
            postEvent("Fuck you, developer...");
            SignatureHack2(params[0]);
            SignatureHack3(params[0], "assets/META-INF");
        } catch (Exception err) {
            getError(err);
        }
        postEvent(String.format(Locale.ENGLISH, "%d matches replaced", progress));
        //Возврат должен быть... любой
        return progress;
    }

    private void SignatureHack2(String a) {
        String asse2 = a + "/original/META-INF";
        String fold2 = a + "/assets/META-INF";
        copyFolder2(asse2, fold2);
    }

    private boolean copyFolder2(String from, String to) {
        try {
            File fFrom = new File(from);
            // Если директория, копируем все ее содержимое
            if (fFrom.isDirectory()) {
                createDir(to);
                String[] FilesList = fFrom.list();
                for (String aFilesList : FilesList)
                    if (!copyFolder2(from + "/" + aFilesList, to + "/" + aFilesList))
                        return false; // Если при копировании произошла ошибка
            } else if (fFrom.isFile()) { // Если файл просто копируем его
                File fTo = new File(to);
                InputStream in = new FileInputStream(fFrom); // Создаем потоки
                OutputStream out = new FileOutputStream(fTo);
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close(); // Закрываем потоки
                out.close();
            }
        } catch (IOException e) { // Обработка ошибок
            postEvent(getError(e));
        }
        return true; // При удачной операции возвращаем true
    }

    public void SignatureHack3(String directoryName, String replacement) {
        String search = "META-INF";
        File directory = new File(directoryName);
        byte[] bytes; //String s;
        File[] fList = directory.listFiles();
        for (File file : fList) {
            if (file.isFile()) {
                String fileName = file.getAbsolutePath();
                String formatFile = fileName.substring(fileName.lastIndexOf(".") + 1);

                if (formatFile.equals("smali")) {
                    try {
                        bytes = new byte[(int) file.length()];
                        BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
                        buf.read(bytes, 0, bytes.length);
                        buf.close();
                        boolean isNewSb = false;
                        String content = new String(bytes);
                        String newContent = content.replace(search, replacement);
                        if (!content.equals(newContent)) {
                            isNewSb = true;
                        }
                        if (isNewSb) {
                            FileOutputStream Out = new FileOutputStream(file);
                            OutputStreamWriter OutWriter = new OutputStreamWriter(Out);
                            OutWriter.append(newContent);
                            OutWriter.close();
                            postEvent("Close streams...");
                            Out.flush();
                            Out.close();
                        }
                    } catch (Exception e) {
                        postError(e);
                    }
                }
            } else if (file.isDirectory()) {
                SignatureHack3(file.getAbsolutePath(), replacement);
            }
        }
    }
}
