package apk.tool.patcher.ui.modules.base;

import android.app.Activity;

import apk.tool.patcher.App;
import apk.tool.patcher.R;


/**
 * Created by Snow Volf on 02.09.2017, 12:12
 */

public abstract class ThemeWrapper {
    /**
     * Применяем тему активити
     */
    public static void applyTheme(Activity ctx) {
        int theme;
        switch (Theme.values()[getThemeIndex()]) {
            case LIGHT:
                theme = R.style.AppTheme;
                break;
            case DARK:
                theme = R.style.AppTheme_Dark;
                break;
            case BLUE:
                theme = R.style.AppTheme_Blue;
                break;
            default:
                theme = R.style.AppTheme;
                break;
        }
        ctx.setTheme(theme);
    }

    /**
     * Получаем индекс темы из настроек
     */
    private static int getThemeIndex() {
        return Integer.parseInt(App.get().getPreferences().getString("ui.theme", String.valueOf(ThemeWrapper.Theme.LIGHT.ordinal())));
    }

    public static boolean isLightTheme() {
        return getThemeIndex() == Theme.LIGHT.ordinal();
    }

    /**
     * Список тем
     */
    public enum Theme {
        LIGHT,
        DARK,
        BLUE
    }
}
