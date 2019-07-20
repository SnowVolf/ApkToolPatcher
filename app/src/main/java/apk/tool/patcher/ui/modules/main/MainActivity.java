package apk.tool.patcher.ui.modules.main;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import apk.tool.patcher.App;
import apk.tool.patcher.R;
import apk.tool.patcher.entity.OnAsyncJobListener;
import apk.tool.patcher.ui.modules.base.BaseActivity;
import apk.tool.patcher.ui.modules.misc.XiaomiGovnoFragment;
import apk.tool.patcher.util.Preferences;
import apk.tool.patcher.util.SystemsDetector;
import apk.tool.patcher.util.TextUtil;
import ru.svolf.melissa.fragment.dialog.SweetContentDialog;
import ru.svolf.melissa.fragment.dialog.SweetListDialog;


public class MainActivity extends BaseActivity implements OnAsyncJobListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_patcher);

        if (!Preferences.isChangelogShowed()) {
            showAssetDialog(this, getString(R.string.caption_changelog), "changelog.txt");
        }
        if (savedInstanceState == null) {
            // Create the fragment only when the activity is created for the first time.
            // ie. not after orientation changes
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(MainFragment.FRAGMENT_TAG);
            if (fragment == null) {
                fragment = new MainFragment();
            }
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, fragment, MainFragment.FRAGMENT_TAG)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();
        }
    }

    /**
     * Представляет диалог с текстом из assets
     *
     * @param context   контекст
     * @param title     титл
     * @param assetPath путь к txt файлу
     */
    public void showAssetDialog(Context context, String title, String assetPath) {
        final StringBuilder sb = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(context.getAssets().open(assetPath), StandardCharsets.UTF_8));
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        final SweetContentDialog dialog = new SweetContentDialog(context);
        dialog.setTitle(title);
        dialog.setMessage(sb);
        dialog.setCancelable(false);
        dialog.setPositive(R.drawable.ic_lucky, context.getString(R.string.great), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Preferences.saveVersionCode();
                dialog.dismiss();
                if (SystemsDetector.isBomzhomi()) {
                    // Create the fragment only when the activity is created for the first time.
                    // ie. not after orientation changes
                    Fragment fragment = getSupportFragmentManager().findFragmentByTag(XiaomiGovnoFragment.FRAGMENT_TAG);
                    if (fragment == null) {
                        fragment = new XiaomiGovnoFragment();
                    }
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.content_frame, fragment, XiaomiGovnoFragment.FRAGMENT_TAG)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .addToBackStack(null)
                            .commit();
                }
            }
        });
        dialog.show();
    }

    @Override
    public void onError(final Exception e) {
        Handler wait = new Handler();
        wait.postDelayed(new Runnable() {
            @Override
            public void run() {
                final SweetContentDialog dialog = new SweetContentDialog(MainActivity.this);
                dialog.setTitle(e.getClass().getSimpleName());
                dialog.setMessage(e.getMessage());
                dialog.setPositive(R.drawable.expand, getString(R.string.details), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        StringBuilder sb = new StringBuilder();
                        sb.append(e.getMessage()).append("\n\n=== === ===\n\n");
                        for (StackTraceElement traceElement : e.getStackTrace()) {
                            sb.append(traceElement).append("\n");
                        }
                        dialog.setMessage(sb);
                    }
                });
                dialog.show();
            }
        }, 1000);

    }

    @Override
    public void onJobFinished() {
        Handler wait = new Handler();
        wait.postDelayed(new Runnable() {
            @Override
            public void run() {
                showAdvert();
            }
        }, 2000);
    }

    /**
     * Позолоти ручку. Выноси в отдельный метод всё, что используешь
     * более одного раза.
     */
    private void showAdvert() {
        final SweetListDialog dialog = new SweetListDialog(this);
        dialog.setTitle(App.bindString(R.string.caption_donate));
        dialog.setItems(R.array.donate_services);
        dialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        TextUtil.copyToClipboard("+79042585040");
                        Toast.makeText(MainActivity.this, R.string.donate_addr_copied, Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        TextUtil.goLink(MainActivity.this, "https://money.yandex.ru/to/410013858440166");
                        break;
                    case 2:
                        TextUtil.goLink(MainActivity.this, "https://paypal.me/htc600");
                        break;
                    case 3:
                        TextUtil.goLink(MainActivity.this, "https://t.me/VolfsChannel");
                    default:
                        break;
                }
            }
        });
        dialog.show();
    }
}
