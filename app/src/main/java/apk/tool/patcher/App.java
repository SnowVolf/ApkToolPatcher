package apk.tool.patcher;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import com.yandex.metrica.YandexMetrica;
import com.yandex.metrica.YandexMetricaConfig;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import java.util.Observer;

import apk.tool.patcher.api.Project;
import apk.tool.patcher.entity.SimpleObservable;
import apk.tool.patcher.util.LocaleHelper;
import apk.tool.patcher.util.Preferences;


@ReportsCrashes
        (mailTo = "buntar888@mail.ru, dev.dog@yandex.ru",
                mode = ReportingInteractionMode.TOAST,
                resToastText = R.string.crash_toast_text)
public class App extends Application {
    public static Handler UI = new Handler(Looper.getMainLooper());
    private static App instance;
    private static Project sCurrentProject;
    private SharedPreferences preferences;

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

}
