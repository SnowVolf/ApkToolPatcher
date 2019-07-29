package apk.tool.patcher;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import androidx.annotation.AttrRes;
import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.multidex.MultiDexApplication;
import androidx.preference.PreferenceManager;

import com.yandex.metrica.YandexMetrica;
import com.yandex.metrica.YandexMetricaConfig;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import apk.tool.patcher.api.Project;
import apk.tool.patcher.util.LocaleHelper;


@ReportsCrashes
        (mailTo = "buntar888@mail.ru, dev.dog@yandex.ru",
                mode = ReportingInteractionMode.TOAST,
                resToastText = R.string.crash_toast_text)
public class App extends MultiDexApplication {
    private static App instance;
    private static Project sCurrentProject;
    private SharedPreferences preferences;

    public Uri extCardUri;

    private static Handler mApplicationHandler = new Handler();
    private HandlerThread sBackgroundHandlerThread;
    private static Handler sBackgroundHandler;

    public App() {
        instance = this;
    }

    public static App get() {
        if (instance == null) {
            instance = new App();
        }
        return instance;
    }

    /**
     * Используй, когда надо будет прокинуть строковой ресурс в труднодоступные места
     *
     * @see String#format(String, Object...)
     * @see Context#getString(int)
     */
    public static String bindString(@StringRes int resId) {
        return get().getString(resId);
    }

    /**
     * Используй, когда надо будет прокинуть строковой ресурс в труднодоступные места
     * то же, что и выше, только с Блек Джеком и шлюхами
     *
     * @see String#format(String, Object...)
     * @see Context#getString(int, Object...)
     */
    public static String bindString(@StringRes int resId, Object... formatArgs) {
        return get().getString(resId, formatArgs);
    }

    /**
     * Получение цвета из attr. Пригодится, когда тебе нужно будет изменить цвет какого-то
     * элемента из кода, но при этом требуется, чтобы он соответствовал текущей
     * теме приложения.
     * Этот метод очень сильно зависит от контекста, который ты передашь, поэтому ЖЕЛАТЕЛЬНО
     * использовать контекст объекта, а не контекст приложения.
     * <p>
     * Например, для Activity: <b>this</b>
     * Для фрагмента: {@link Fragment#getContext()}
     * Для View: {@link android.view.View#getContext()}
     *
     * @param context контекст текущего обьекта
     * @param attr    ресурс, из которого нужно извлечь цвет
     * @return цвет или {@link Color#RED} если контекст темы не содержит данного атрибута
     */
    @ColorInt
    public static int getColorFromAttr(Context context, @AttrRes int attr) {
        TypedValue typedValue = new TypedValue();
        if (context != null && context.getTheme().resolveAttribute(attr, typedValue, true))
            return typedValue.data;
        else
            return Color.RED;
    }

    /**
     * Получение изображения из attr. Пригодится, когда тебе нужно будет изменить изображение какого-то
     * элемента из кода, но при этом требуется, чтобы он соответствовал текущей
     * теме приложения.
     * Этот метод очень сильно зависит от контекста, который ты передашь, поэтому ЖЕЛАТЕЛЬНО
     * использовать контекст объекта, а не контекст приложения.
     * <p>
     * Например, для Activity: <b>this</b>
     * Для фрагмента: {@link Fragment#getContext()}
     * Для View: {@link android.view.View#getContext()}
     *
     * @param context контекст текущего обьекта
     * @param attr    ресурс, из которого нужно извлечь цвет
     * @return id drawable ресурса
     */
    @DrawableRes
    public static int getDrawableResAttr(Context context, @AttrRes int attr) {
        TypedArray a = context.getTheme().obtainStyledAttributes(new int[]{attr});
        int attributeResourceId = a.getResourceId(0, 0);
        a.recycle();
        return attributeResourceId;
    }

    /**
     * Конвертация dpi в пиксели
     *
     * @param dp число DPI
     * @return число пикселей, эквивалентное DPI
     */
    public static int dpToPx(int dp) {
        DisplayMetrics displayMetrics = get().getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static Project getCurrentProject() {
        if (sCurrentProject == null) {
            sCurrentProject = new Project("");
        }
        return sCurrentProject;
    }

    public static void setCurrentProject(Project currentProject) {
        sCurrentProject = currentProject;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ACRA.init(this);

        sBackgroundHandlerThread = new HandlerThread("app_background");
        sBackgroundHandlerThread.start();
        sBackgroundHandler = new Handler(sBackgroundHandlerThread.getLooper());

        YandexMetricaConfig config =
                YandexMetricaConfig.newConfigBuilder("21979b37-2bd9-4893-8409-16497bc582d2").build();
        YandexMetrica.activate(getApplicationContext(), config);
        // Отслеживание активности пользователей
        YandexMetrica.enableActivityAutoTracking(this);

        PreferenceManager.setDefaultValues(this, R.xml.patcher_preferences, false);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        hackVmPolicy();
    }

    @Override
    protected void attachBaseContext(Context base) {
        // Иначе не будут ресолвиться стринги из ресурсов, и половина аппы будет на языке системы,
        // а другая половина на языке, который выбран в настройках
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Иначе не будут ресолвиться стринги из ресурсов, и половина аппы будет на языке системы,
        // а другая половина на языке, который выбран в настройках
        LocaleHelper.onAttach(this);
    }

    /**
     * Получение дефолтных настроек приложения
     *
     * @return инстанс {@link SharedPreferences}
     */
    public SharedPreferences getPreferences() {
        if (preferences == null)
            preferences = PreferenceManager.getDefaultSharedPreferences(this);
        return preferences;
    }

    /**
     * Отключаем проверку на FileUriExposue
     * Начиная с Android N нам нужно использовать content:// URI вместо file:// URI
     * и получать доступ к файлам через провайдеры.
     * <p>
     * Данный хак отключает это ограничение
     *
     * @see android.os.FileUriExposedException
     */
    private void hackVmPolicy() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }
    }

    /**
     * Post a runnable to handler. Use this in case we don't have any restriction to execute after
     * this runnable is executed, and in case we need
     * to execute something after execution in background
     */
    public static void runInBackground(Runnable runnable) {
        synchronized (sBackgroundHandler) {
            sBackgroundHandler.post(runnable);
        }
    }

    /**
     * A compact AsyncTask which runs which executes whatever is passed by callbacks.
     * Supports any class that extends an object as param array, and result too.
     */
    public static <Params, Result> void runInParallel(final CustomAsyncCallbacks<Params, Result> customAsyncCallbacks) {

        synchronized (customAsyncCallbacks) {

            new AsyncTask<Params, Void, Result>() {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    customAsyncCallbacks.onPreExecute();
                }

                @Override
                protected Result doInBackground(Object... params) {
                    return customAsyncCallbacks.doInBackground();
                }

                @Override
                protected void onPostExecute(Result aVoid) {
                    super.onPostExecute(aVoid);
                    customAsyncCallbacks.onPostExecute(aVoid);
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, customAsyncCallbacks.parameters);
        }
    }

    /**
     * Interface providing callbacks utilized by
     */
    public static abstract class CustomAsyncCallbacks<Params, Result> {
        public final @Nullable Params[] parameters;

        public CustomAsyncCallbacks(@Nullable Params[] params) {
            parameters = params;
        }

        public abstract Result doInBackground();

        public void onPostExecute(Result result) { }

        public void onPreExecute() { }
    }

    /**
     * Run a runnable in the main application thread
     *
     * @param r Runnable to run
     */
    public void runInApplicationThread(Runnable r) {
        mApplicationHandler.post(r);
    }

}
