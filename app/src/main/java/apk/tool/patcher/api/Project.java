package apk.tool.patcher.api;


import android.annotation.SuppressLint;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.io.File;
import java.util.ArrayList;

@SuppressLint("unused")
public class Project implements Parcelable {
    public static final Creator<Project> CREATOR = new Creator<Project>() {
        @Override
        public Project createFromParcel(Parcel in) {
            return new Project(in);
        }

        @Override
        public Project[] newArray(int size) {
            return new Project[size];
        }
    };
    /**
     * Путь к папке проекта
     */
    private static String sSrcPath;
    /**
     * Количество dex-ов в проекте
     * <p>
     * Минимум - 1
     */
    public int volumes;
    /**
     * Тип проекта. Определяется по наличию в его директории определённых файлов
     * <p>
     * Может быть одним из:
     * {@link TYPES#APK_TOOL}, {@link TYPES#APK_EDITOR}, {@linkplain TYPES#UNKNOWN}
     * <p>
     * По умолчанию равен UNKNOWN
     */
    private int TYPE = TYPES.UNKNOWN.ordinal();
    /**
     * Список путей до папок со smali-файлами.
     * <p>
     * Каждый член массива содержит в себе абсолютную ссылку на папку, начиная со
     * {@code /storage/emulated/...}
     * <p>
     * Минимальный размер - 1
     */
    private ArrayList<String> dexPaths = new ArrayList<>();

    /**
     * Главный конструктор
     * <p>
     * Выполняется первичная проверка типа проекта, а также вычисление количества dex
     *
     * @param srcPath путь до папки с проектом
     * @throws IllegalProjectException если путь к папке пуст
     */
    public Project(@NonNull String srcPath) {
        if (srcPath.isEmpty()) {
            restrict("Unable to find project at: ''");
        }
        sSrcPath = srcPath;

        TYPE = checkType();
        volumes = countVolumes();
    }

    /**
     * Инициализатор парсела
     *
     * @param in входной парсел
     * @see Parcel
     * @see Parcelable
     */
    protected Project(Parcel in) {
        TYPE = in.readInt();
        volumes = in.readInt();
    }

    public static Project from(@NonNull String path) {
        return new Project(path);
    }

    /**
     * Проверка структуры проекта
     *
     * @param baseDir папка проекта, которую необходимо проверить
     * @return true - если папка проекта верная
     */
    private static boolean isValidProjectDir(String baseDir) {
        final File dir = new File(baseDir);
        final File res = new File(dir, "res/");
        final File dex = new File(dir, "smali/");
        final File dic = new File(baseDir, "dic.xml");
        return dic.exists() || (res.exists() && dex.exists());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(TYPE);
        dest.writeInt(volumes);
    }

    /**
     * Удобная обертка для получения папки проекта в виде файловой переменной
     *
     * @return если {@link Project#sSrcPath} представляет собой действительную
     * ссылку на папку в памяти устройства, то возвращается обьект типа File,
     * являющийся по сути ссылкой на папку проекта.
     * Иначе - путь до папки с документами.
     */
    public File getDir() {
        File dir = new File(sSrcPath);
        if (dir.exists() && dir.isDirectory()) {
            return dir;
        }
        return new File(Environment.DIRECTORY_DOCUMENTS);
    }

    /**
     * Получение пути до папки проекта в виде строки
     *
     * @return путь до папки проекта
     * @see Project#getDir()
     * @see Project#getDir()
     */
    public String getPath() {
        return getDir().getPath();
    }

    /**
     * Получение имени проекта для отображения
     * Например, <i>/storage/emulated/0/Pineapple.apk_src/</i> будет преобразован в <b>Pineapple</b>
     *
     * @return имя проекта для отображения
     * @see Project#getPath()
     */
    public String getName() {
        int index = getPath().lastIndexOf('/') + 1;

        return getPath().substring(index).replaceAll("(\\.apk)?_src", "");
    }

    /**
     * Выбрасывает исключения
     *
     * @param reason причина ошибки
     */
    private void restrict(@NonNull String reason) {
        throw new IllegalProjectException(getClass(), reason);
    }

    /**
     * Проверка структуры проекта
     *
     * @return {@link Project#isValidProjectDir(String)}
     * @see Project#isValidProjectDir(String)
     */
    public boolean isValid() {
        return isValidProjectDir(getPath());
    }

    /**
     * Проверка папки проекта на присутствие пробелов в имени
     *
     * @return true, если пробелы присутствуют
     */
    public boolean isIllegalName() {
        return getName().contains(" ");
    }

    /**
     * Получение типа проекта
     *
     * @return тип проекта
     * @see TYPES
     */
    public int getType() {
        return TYPE;
    }

    /**
     * @return true, если проект является проектом ApkTool
     */
    public boolean forApkTool() {
        if (!isValid()) {
            restrict("Invalid project! You need to check Project.isValid() before call this");
        }
        return TYPE == TYPES.APK_TOOL.ordinal();
    }

    /**
     * @return true, если проект является проектом Apk Editor
     */
    public boolean forApkEditor() {
        if (!isValid()) {
            restrict("Invalid project! You need to check Project.isValid() before call this");
        }
        return TYPE == TYPES.APK_EDITOR.ordinal();
    }

    /**
     * Проверка типа проекта
     *
     * @return тип проекта, или {@link TYPES#UNKNOWN}
     * @see Project(String)
     */
    private int checkType() {
        if (sSrcPath != null) {
            File apktool = new File(getDir(), "apktool.yml");
            if (apktool.exists()) {
                return TYPES.APK_TOOL.ordinal();
            } else
                return TYPES.APK_EDITOR.ordinal();
        }
        return TYPES.UNKNOWN.ordinal();
    }

    /**
     * Подсчёт количества директорий со smali файлами
     *
     * @return количество директорий, или 1, если проект не имеет поддержки MultiDex
     */
    private int countVolumes() {
        // У нас уже есть один dex. Считаем только его придатки
        int multi = 1;
        dexPaths.add(getPath() + "/smali");
        if (sSrcPath != null) {
            for (File folder : getDir().listFiles()) {
                if (folder.isDirectory() && folder.getName().contains("_classes")) {
                    dexPaths.add(folder.getPath());
                    multi++;
                }
            }
        }
        return multi;
    }

    /**
     * Проверка поддержки MultiDex
     *
     * @return true, если проект имеет поддержку MultiDex
     */
    public boolean isMultiDexed() {
        return volumes > 1;
    }

    /**
     * Путь к файлу манифеста
     */
    public String matifest() {
        return getPath() + "/AndroidManifest.xml";
    }

    /**
     * Путь к папке ресурсов
     */
    public String res() {
        return getPath() + "/res";
    }

    /**
     * Путь к папке с нативными библиотеками
     */
    public String lib() {
        return getPath() + "/lib";
    }

    /**
     * Путь к папке с внешними ресурсами
     */
    public String assets() {
        return getPath() + "/assets";
    }

    /**
     * Путь к директории подписи
     */
    public String meta() {
        return getPath() + "/META-INF";
    }

    /**
     * Перечисление типов проектов
     */
    private enum TYPES {
        APK_TOOL,
        APK_EDITOR,
        UNKNOWN
    }

}
