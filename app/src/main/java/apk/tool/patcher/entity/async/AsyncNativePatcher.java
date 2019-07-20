package apk.tool.patcher.entity.async;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.afollestad.async.Action;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import apk.tool.patcher.entity.LogicalCore;

public class AsyncNativePatcher extends Action<Integer> {
    private static final String TAG = "AsyncNativePatcher";

    private static ArrayList indexOfBlock(byte[] where, byte[] what) {
        ArrayList result = new ArrayList();//в этом списке будут храниться найденные позиции массива
        for (int ix = 0; ix < where.length - what.length + 1; ++ix) {
            boolean found = true;
            for (int j = 0; j < what.length; ++j) {
                if (where[ix + j] != what[j]) {
                    found = false;
                    break;
                }
            }
            if (found) result.add(ix);
        }
        return result;//возвращаем результат
    }

    @NonNull
    @Override
    public String id() {
        return "native-patcher";
    }

    @Nullable
    @Override
    protected Integer run(String... params) throws InterruptedException {
        Log.d(TAG, "run() called with: params = [" + Arrays.toString(params) + "]");
        //Здесь вся тяжелая работа
        try {
            so1(params[0]);
            String patch = params[0];
            String apk = patch.replaceAll("(\\.apk)?_src", ".apk");
            LogicalCore.copyDex(apk, patch);
        } catch (Exception err) {
            postError(err);
            err.printStackTrace();
        }
        postEvent(String.format(Locale.ENGLISH, "%d matches replaced", progress));
        //Возврат должен быть... любой
        return progress;
    }

    private void so1(String patch) {
        Log.d(TAG, "so1() called with: patch = [" + patch + "]");
        File directory = new File(patch);
        File[] fList = directory.listFiles();
        for (File file : fList) {
            if (file.isFile()) {
                if (file.getAbsolutePath().endsWith(".so")) {
                    postEvent(String.format("Checking: %s", file
                            .getAbsolutePath()));
                    replaceBytes(file.getAbsolutePath(),
                            "63 6C 61 73 73 65 73 2E 64 65 78",
                            "78 6C 61 73 73 65 73 2E 64 65 78");


                }
            } else if (file.isDirectory()) {
                so1(file.getAbsolutePath());
            }

        }
    }

    private void replaceBytes(String pathToFile, String oldBytes, String newBytes) {
        Log.d(TAG, "replaceBytes() called with: pathToFile = [" + pathToFile + "], oldBytes = [" + oldBytes + "], newBytes = [" + newBytes + "]");
        RandomAccessFile data;//тут будет храниться открытый файл
        long offset;//смещение. Грубо говоря позиция в файле тут будет храниться
        byte[] obytes = new byte[oldBytes.split(" ").length];//массив байтов из того ЧТО_ЗАМЕНИТЬ
        byte[] nbytes = new byte[newBytes.split(" ").length];////массив байтов из того НА_ЧТО_ЗАМЕНИТЬ
        byte[] buf = new byte[1000];//буфер. Сюда будут считываться байты по 1000 штук
        int k = 0;//ввел только для того чтобы строки в байты перевести
        boolean end = false;//это типа сигнал, что файл закончился

        //перевод строки ЧТО_ЗАМЕНИТЬ в массив байтов
        for (String sx : oldBytes.split(" ")) {
            obytes[k] = (byte) Integer.parseInt(sx, 16);
            k++;
        }
        k = 0;
        //перевод строки НА_ЧТО_ЗАМЕНИТЬ в массив байтов
        for (String sx : newBytes.split(" ")) {
            nbytes[k] = (byte) Integer.parseInt(sx, 16);
            k++;
        }

        try {
            //открываем файл
            data = new RandomAccessFile(pathToFile, "rw");
            data.seek(0);//устанавливаем позицию в файле в начало
            //в цикле читаем по 1000 байтов, ищем нужный кусок и пишем в файл новое
            while (true) {
                try {
                    // Тут проверка на конец файла.
                    // то есть если текущая позиция+размер буфера(1000) больше чем размер файла,
                    // то размер буфера=размер файла-текущая позиция
                    if (data.getFilePointer() + buf.length > data.length()) {
                        buf = new byte[(int) (data.length() - data.getFilePointer())];
                        end = true;
                    }
                    data.readFully(buf);//читаем 1000 байтов в буфер
                    ArrayList index = indexOfBlock(buf, obytes);//получаем позиции массива ЧТО_ЗАМЕНИТЬ в буфере
                    if (index.size() != 0) {//если позиции есть то обрабатываем
                        long pos = data.getFilePointer();//сохраняем текущую позицию, чтобы потом с нее же продолжить читать файл
                        //супер сложные расчеты)
                        for (Object ic : index) {
                            //устанавливаем позицию на найденный кусок байтов
                            data.seek(pos - buf.length + (int) ic);
                            //вместо старого пишем новые байты
                            data.write(nbytes);
                            progress++;
                            postProgress(this, progress);
                        }
                        //возвращаем указатель туда, где остановились читать файл
                        data.seek(pos);
                    }
                    //высчитываем новую позицию в файле, то есть тупо возвращаемся назад на количество байтов в ЧТО_ЗАМЕНИТЬ
                    offset = data.getFilePointer() - obytes.length;
                    data.seek(offset);//смещаемся на новый указатель(назад)
                    if (end) {
                        break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }

            data.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Integer.parseInt("31",16) hex to int
        //String.format("%X ", this.data.read()) int to hex
    }
}
