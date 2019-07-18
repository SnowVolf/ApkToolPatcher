package apk.tool.patcher.ui.modules.settings;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import apk.tool.patcher.App;
import apk.tool.patcher.R;
import apk.tool.patcher.util.Cs;
import ru.svolf.melissa.swipeback.SwipeBackActivity;

/**
 * Created by Snow Volf on 02.09.2017, 12:20
 */

public class SettingsActivity extends SwipeBackActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patcher_settings);
        setEdgeLevel(App.dpToPx(150));

        if (savedInstanceState == null) {
            int anInt = Integer.parseInt(Cs.TAB_MAIN);
            // Скроллим нужный таб  в зависимости от интента
            if (getIntent().getStringExtra(Cs.ARG_PREF_TAB) != null &&
                    !getIntent().getStringExtra(Cs.ARG_PREF_TAB).isEmpty()) {
                anInt = Integer.parseInt(getIntent().getStringExtra(Cs.ARG_PREF_TAB));
            }

            // Create the fragment only when the activity is created for the first time.
            // ie. not after orientation changes
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(SettingsHostFragment.FRAGMENT_TAG);
            if (fragment == null) {
                fragment = SettingsHostFragment.newInstance(anInt);
            }
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, fragment, SettingsHostFragment.FRAGMENT_TAG)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();
        }
    }
}
