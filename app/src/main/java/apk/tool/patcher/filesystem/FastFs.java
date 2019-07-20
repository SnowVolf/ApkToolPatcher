package apk.tool.patcher.filesystem;

import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.gmail.heagoo.common.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Утилитарный класс для работы с файловой системой
 */
public class FastFs {

    /**
     * Конвертирует массив файлов в лист
     *
     * @param files массив файлов
     * @return ArrayList с файлами
     * @see ArrayList#addAll(Collection)
     */
    public static ArrayList<File> asList(File[] files) {
        ArrayList<File> arrayList = new ArrayList<>();
        arrayList.addAll(Arrays.asList(files));
        return arrayList;
    }

    /**
     * Копирование файла в контексте UI
     *
     * @param host хостовый фрагмент
     * @param test файл, который копируем
     * @param dest файл, в который копируем
     */
    public static void copyFile(final Fragment host, final File test, final File dest) {
        // https://t.me/VolfsChannel
        if (test.canRead()) {
            if (!dest.exists()) {
                try {
                    if (dest.createNewFile()) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    FileUtil.copyFile(test, dest);
                                } catch (final IOException e) {
                                    e.printStackTrace();
                                    host.getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(host.getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }
                        }).start();
                        Toast.makeText(host.getContext(), dest.getPath(), Toast.LENGTH_LONG).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(host.getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
