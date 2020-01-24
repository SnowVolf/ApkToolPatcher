package apk.tool.patcher.ui.modules.base;


import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import apk.tool.patcher.R;
import apk.tool.patcher.ui.modules.main.MainActivity;
import apk.tool.patcher.ui.modules.settings.SettingsActivity;
import apk.tool.patcher.util.LocaleHelper;
import ru.svolf.melissa.fragment.dialog.SweetContentDialog;


/**
 * Created by Snow Volf on 19.08.2017, 12:09
 */

public class BaseActivity extends AppCompatActivity {
    /**
     * Код текущего языка
     */
    private String lang = null;

    /**
     * Ресивер изменения темы
     */
    private final BroadcastReceiver mThemeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (SettingsActivity.class.equals(BaseActivity.this.getClass())) {
                finish();
                startActivity(getIntent());
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            } else recreate();
        }
    };

    public BaseActivity() {

    }

    /**
     * Рестарт процесса приложения. Нужно для того, чтобы языковые настройки изменились на новые
     * обычный {@link #finish()} тут не работает
     */
    protected static void restartApplication(Activity activity) {
        Intent mStartActivity = new Intent(activity, MainActivity.class);
        int mPendingIntentId = 123456;
        PendingIntent mPendingIntent = PendingIntent.getActivity(activity, mPendingIntentId, mStartActivity,
                PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
        activity.finish();
        System.exit(0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Регистрация ресивера
        LocalBroadcastManager.getInstance(this).registerReceiver(mThemeReceiver,
                new IntentFilter("org.openintents.action.REFRESH_THEME"));
        // Применение текущей темы
        ThemeWrapper.applyTheme(this);

        if (ThemeWrapper.isLightTheme()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                View decorView = getWindow().getDecorView();
                decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                View decorView = getWindow().getDecorView();
                decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }
        getWindow().setNavigationBarColor(ThemeWrapper.resolveNavBarColor(this));

        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Получаем теущий язык
        if (lang == null) {
            lang = LocaleHelper.getLanguage(this);
        }
        // Если язык из настроек не соответствует полученномму языку
        if (!LocaleHelper.getLanguage(this).equals(lang)) {
            // Новый контекст нужен для того, чтобы отобразить диалог изменения языка
            // в правильном формате
            Context newContext = LocaleHelper.onAttach(this);
            SweetContentDialog dialog = new SweetContentDialog(this);
            dialog.setTitle(newContext.getString(R.string.pref_sys_lang));
            dialog.setMessage(newContext.getString(R.string.lang_changed));
            dialog.setPositive(R.drawable.ic_check, newContext.getString(android.R.string.ok), view -> BaseActivity.restartApplication(BaseActivity.this));
            dialog.show();
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        // Иначе не будут ресолвиться стринги из ресурсов, и половина аппы будет на языке системы,
        // а другая половина на языке, который выбран в настройках
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

    @Override
    protected void onDestroy() {
        // Отписываемся от ресивера
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mThemeReceiver);
        super.onDestroy();
    }
}

