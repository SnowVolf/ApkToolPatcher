package apk.tool.patcher.entity.async;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.util.Log;

import com.afollestad.async.Action;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AsyncIdDecoder extends Action<Integer> {
    private static final String TAG = "AsyncIdDecoder";
    private ArrayList<File> FileOrDirList = new ArrayList<>(); //это список всех файлов или папок

    @NonNull
    @Override
    public String id() {
        return "decode-res-id";
    }

    @Nullable
    @Override
    protected Integer run(String... params) throws InterruptedException {
        Log.d(TAG, "run() called with: params = [" + Arrays.toString(params) + "]");
        //Здесь вся тяжелая работа
        try {
            addInfoToId(params[0]);
        } catch (Exception err) {
            postError(err);
            err.printStackTrace();
        }
        // Сотни совпадений не стоит каждый раз обновлять
        // достаточно вывести общее количество совпадений
        postProgress(this, progress);
        //Возврат должен быть... любой
        return progress;
    }

    //основной метод
    //directoryName папка с проектом
    private void addInfoToId(String directoryName) {
        Pattern idPat = Pattern.compile("(con.+ [pv]\\d+, (0x.+))");
        Pattern pubPat = Pattern.compile("type=\\\".+\\\" name=\\\"(.+)\\\" id=\\\"(.+)\\\"");
        ArrayMap<String, String> dict = new ArrayMap<String, String>(); //тут будут храниться все имена строк и id, найденные в public.xml
        byte[] bytes;
        BufferedInputStream buf;
        String content;
        FileOutputStream Out;
        OutputStreamWriter OutWriter;
        Matcher mat;
        boolean saveFile = false;
        StringBuffer sb;
        //чтение и парсинг public.xml
        File pub = new File(directoryName, "res/values/public.xml");
        try {
            bytes = new byte[(int) pub.length()];

            buf = new BufferedInputStream(new FileInputStream(pub));
            buf.read(bytes, 0, bytes.length);
            buf.close();

            content = new String(bytes);
            mat = pubPat.matcher(content);
            while (mat.find()) //пока регулярка будет находить нужное
            {
                progress++;
                if (ETA + 499 < progress) {
                    postProgress(this, progress);
                    ETA = progress;
                }
                //добавляем найденное в словарь
                dict.put(mat.group(2), mat.group(1));
            }
        } catch (Exception e) {
            System.out.println(e.toString());
            //Toast.makeText(getApplicationContext(),e.toString(),2).show();
        }
        //конец чтения и парсинга public.xml

        //получим список всех smali файлов в папке проекта
        getAllDirOrFile(directoryName, true, ".smali");

        //обработка найденных smali файлов
        for (File file : this.FileOrDirList) {
            try {
                bytes = new byte[(int) file.length()];

                buf = new BufferedInputStream(new FileInputStream(file));
                buf.read(bytes, 0, bytes.length);
                buf.close();

                content = new String(bytes);

                mat = idPat.matcher(content);
                sb = new StringBuffer(); //будет хранить части текста между найденными регуляркой строками(как я понял)
                while (mat.find()) {
                    if (dict.containsKey(mat.group(2))) //если найденный id есть в словаре
                    {
                        if (ETA + 999 < progress) {
                            postProgress(this, progress);
                            ETA = progress;
                        }
                        //заменим найденную часть на нужное. appendReplacement поместит готовую строку с куском текста до предыдущего вхождения в sb
                        mat.appendReplacement(sb, "$1 #" + dict.get(mat.group(2)));
                        saveFile = true;
                    }
                }
                mat.appendTail(sb); //склеим все части. По сути это то же содержимое файла, только с заменами
                content = sb.toString(); //заменяем прочитанное содержимое файла на новое

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
                //Toast.makeText(getApplicationContext(),e.toString(),2).show();
            }
        }
    }

    //метод, который добавит в FileOrDirList все файлы или папки
    //fileOrNot=true только файлы
    //fileOrNot=false только папки
    //ext это расширение нужных файлов
    public void getAllDirOrFile(String directoryName, boolean fileOrNot, String ext) {
        File directory = new File(directoryName);

        File[] fList = directory.listFiles();
        for (File file : fList) {
            if (file.isFile()) {
                if (fileOrNot && file.getAbsolutePath().endsWith(ext)) {
                    this.FileOrDirList.add(file);
                }
            } else if (file.isDirectory()) {
                if (!fileOrNot) {
                    this.FileOrDirList.add(file);
                }
                getAllDirOrFile(file.getAbsolutePath(), fileOrNot, ext);
            }
        }
    }

}
