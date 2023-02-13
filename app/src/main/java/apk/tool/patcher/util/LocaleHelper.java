package apk.tool.patcher.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;

import androidx.preference.PreferenceManager;

import java.util.Locale;

/**
 * Created by radiationx on 09.09.17.
 */
/* Original http://gunhansancar.com/change-language-programmatically-in-android/ */
public class LocaleHelper {

    private static final String SELECTED_LANGUAGE = "sys.language";

    public static Context onAttach(Context context) {
        String lang = getPersistedData(context, getDefaultLocale().getLanguage());
        return setLocale(context, lang);
    }

    public static Locale getDefaultLocale() {
        return Locale.getDefault(Locale.Category.DISPLAY);
    }

    public static Context onAttach(Context context, String defaultLanguage) {
        String lang = getPersistedData(context, defaultLanguage);
        return setLocale(context, lang);
    }

    public static String getLanguage(Context context) {
        return getPersistedData(context, getDefaultLocale().getLanguage());
    }

    public static Context setLocale(Context context, String language) {
        persist(context, language);

        return updateResources(context, language);

    }

    private static String getPersistedData(Context context, String defaultLanguage) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(SELECTED_LANGUAGE, defaultLanguage);
    }

    private static void persist(Context context, String language) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString(SELECTED_LANGUAGE, language);
        editor.apply();
    }

    private static Context updateResources(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Configuration configuration = context.getResources().getConfiguration();
        configuration.setLocale(locale);
        configuration.setLayoutDirection(locale);

        return context.createConfigurationContext(configuration);
    }
}
