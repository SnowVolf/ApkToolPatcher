package apk.tool.patcher.filesystem;

import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.gmail.heagoo.common.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Утилитарный класс для работы с файловой системой
 */
public class FastFs {

    /**
     * Конвертирует массив файлов в лист
     *
     * @param files массив файлов
     * @return ArrayList с файлами
     */
    public static ArrayList<File> asList(File[] files) {
        return new ArrayList<>(Arrays.asList(files));
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
                                    host.requireActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            e.printStackTrace();
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

//    //FIXME replace reflection with direct call when SDK target can be changed.
//    @SuppressWarnings("JavaReflectionMemberAccess")
//    @TargetApi(Build.VERSION_CODES.N)
//    private void openStorageLinkDocumentTreeActivityImplQ() {
//        final StorageVolume volume = findVolume();
//        if (volume == null) {
//            openStorageLinkDocumentTreeActivityImpl();
//            return;
//        }
//
//        try {
//            final Method m = StorageVolume.class.getMethod("createOpenDocumentTreeIntent");
//            final Intent intent = (Intent) m.invoke(volume);
//            startActivityForResult(intent, FXIntent.REQUEST_ID_STORAGE_LINK_DOCUMENT_TREE);
//        } catch (final NoSuchMethodException ignored) {
//        } catch (final IllegalAccessException ignored) {
//        } catch (final InvocationTargetException ignored) {
//        }
//    }
}
