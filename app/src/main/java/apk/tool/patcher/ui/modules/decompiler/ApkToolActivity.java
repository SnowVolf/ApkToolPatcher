package apk.tool.patcher.ui.modules.decompiler;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.File;
import java.util.Arrays;

import apk.tool.patcher.R;
import apk.tool.patcher.ui.modules.base.BaseActivity;
import apk.tool.patcher.ui.modules.settings.DecompilerSettingsFragment;
import apk.tool.patcher.ui.modules.settings.SettingsActivity;
import apk.tool.patcher.util.Cs;
import apk.tool.patcher.util.SysUtils;
import apk.tool.patcher.util.TextUtil;
import ru.atomofiron.apknator.Managers.TaskManager;
import ru.atomofiron.apknator.Managers.ToolsManager;
import ru.atomofiron.apknator.Utils.Cmd;

public class ApkToolActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    FloatingActionButton fab;
    DrawerLayout drawer;
    ActionBarDrawerToggle toggle;
    Toolbar toolbar;
    TextView navTools;
    AlertDialog exitDialog;
    AlertDialog execDialog;
    AlertDialog executedDialog;
    MenuItem[] taskNavItems;
    MenuItem[] smaliNavItems;
    MenuItem sdcardMenuItem;
    MenuItem homeMenuItem;
    MenuItem toolsMenuItem;
    Menu parMenu;
    FilesFragment filesFragment;
    ToolsFragment toolsFragment;
    PreviewFragment previewFragment;
    DecompilerSettingsFragment prefsFragment;
    SmaliFragment[] smaliFragments = new SmaliFragment[]{null, null, null, null};
    EditText execEditText;
    TextView executedTextView;

    WakeLock wakeLock;
    SharedPreferences sp;
    FragmentManager fragmentManager;
    SysUtils.ActionListener actionListener;
    ApkToolActivity co;

    String dataPath;
    String toolsPath;
    int wasOpened = 0;
    String pathForSmali;
    String pathForPreview;
    String pathForKeyStore;
    String openedSmaliPath;
    String[] smaliPaths = new String[]{"", "", "", ""};
    int curSmaliFragment = -1;
    boolean turnedOn = false;
    boolean useRoot = false;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSystemService(Context.POWER_SERVICE) != null) {
            wakeLock = ((PowerManager) getSystemService(Context.POWER_SERVICE)).newWakeLock(PowerManager.FULL_WAKE_LOCK, getString(R.string.app_name));
        }
        setContentView(R.layout.activity_decompiler);

        co = this;
        sp = SysUtils.SP(co);
        dataPath = SysUtils.getDataPath(co);
        toolsPath = SysUtils.getToolsPath(co);
        useRoot = sp.getBoolean(SysUtils.PREFS_USE_ROOT, false);

        exitDialog = new AlertDialog.Builder(co).setTitle(getString(R.string.want_to_exit))
                .setPositiveButton(getString(R.string.yes),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int Int) {
                                finish();
                            }
                        })
                .setNegativeButton(getString(R.string.no), null)
                .create();

        SysUtils.Log(Build.HARDWARE);
        SysUtils.Log(Arrays.toString(Build.SUPPORTED_ABIS));

        execEditText = new EditText(co);
        execDialog = new AlertDialog.Builder(co)
                .setTitle(getString(R.string.exec))
                .setView(execEditText)
                .setNeutralButton(R.string.cancel, null)
                .setPositiveButton(R.string.exec, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        exec();
                    }
                }).create();

        ScrollView scrollView = new ScrollView(co); // (ScrollView)LayoutInflater.from(mContext).inflate(R.layout.mono_text, null);
        executedTextView = (TextView) LayoutInflater.from(co).inflate(R.layout.mono_textview, null);
        scrollView.addView(executedTextView);
        executedDialog = new AlertDialog.Builder(co)
                .setNeutralButton(R.string.copy, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        TextUtil.copyToClipboard(executedTextView.getText().toString());
                    }
                })
                .setNegativeButton(R.string.exec, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        execDialog.show();
                    }
                })
                .setPositiveButton(getString(R.string.close), null)
                .setTitle(getString(R.string.exec))
                .setView(scrollView)
                .create();


        drawer = findViewById(R.id.drawer_layout);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        fragmentManager = getSupportFragmentManager();
        actionListener = new ActionListener();
        filesFragment = new FilesFragment();
        toolsFragment = new ToolsFragment();
        previewFragment = new PreviewFragment();
        filesFragment.setActionListener(actionListener);
        toolsFragment.setActionListener(actionListener);

        setFragment(SysUtils.FRAGMENT_START);
    }


    public void turnOn() {
        SysUtils.Log("turnOn()");
        if (turnedOn)
            return;

        turnedOn = true;
        SysUtils.Log("turning...");

        // Design:
        fab = findViewById(R.id.fab);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setOnClickListener(co);

        toggle = new ActionBarDrawerToggle(co, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager.popBackStack(); // не доделано
            }
        });
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        toggle.setDrawerIndicatorEnabled(true);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(co);
        // :Design

        Menu menu = navigationView.getMenu();
        taskNavItems = new MenuItem[]{
                menu.findItem(R.id.nav_task_1),
                menu.findItem(R.id.nav_task_2),
                menu.findItem(R.id.nav_task_3),
                menu.findItem(R.id.nav_task_4),};
        smaliNavItems = new MenuItem[]{
                menu.findItem(R.id.nav_smali_1),
                menu.findItem(R.id.nav_smali_2),
                menu.findItem(R.id.nav_smali_3),
                menu.findItem(R.id.nav_smali_4),};
        sdcardMenuItem = menu.findItem(R.id.nav_sdcard);
        homeMenuItem = menu.findItem(R.id.nav_home);
        toolsMenuItem = menu.findItem(R.id.nav_tools);
        navTools = navigationView.getHeaderView(0).findViewById(R.id.tools);

        ToolsManager.liteReview(co);
        updateVersions();

        setFragment(SysUtils.FRAGMENT_FILES);

        Cmd.init(useRoot);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        SysUtils.Log("onNewIntent()");
        if (intent == null) return;
        String path;
        String action = intent.getAction();
        Uri data = intent.getData();
        String scheme = intent.getScheme();
        if (action != null &&
                data != null &&
                scheme != null &&
                intent.getAction().equals(Intent.ACTION_VIEW) &&
                (path = data.getPath()) != null &&
                path.endsWith(".smali") &&
                scheme.equals("file"))
            actionListener.onAction(SysUtils.FR_SMALI, path, 0, false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        ToolsManager.liteReview(co);
        updateVersions();
        TaskManager taskManager = filesFragment.getTaskManager();
        if (taskManager != null)
            taskManager.onStop(false);

        useRoot = sp.getBoolean(SysUtils.PREFS_USE_ROOT, false);
    }

    @Override
    protected void onStop() {
        super.onStop();

        TaskManager taskManager = filesFragment.getTaskManager();
        if (taskManager != null)
            taskManager.onStop(true);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
        else if (fragmentManager.getBackStackEntryCount() == 0)
            filesFragment.onBackPressed();
        else
            super.onBackPressed();

        // глубина стека не превышает 1, поэтому можно быть уверенным, что тут мы попали в проводник
        parMenu.setGroupVisible(R.id.menu_main, true);
        actionListener.onAction(SysUtils.FR_NAV_MENU, null, filesFragment.getMenuInt(), false);
    }

    public void onResume() {
        super.onResume();

        if (sp.getBoolean(SysUtils.PREFS_KEEP_SCREEN, false))
            wakeLock.acquire();
    }

    protected void onPause() {
        super.onPause();

        if (wakeLock.isHeld())
            wakeLock.release();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar:
                break;
        }
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitDialog.show();
            return false;
        }
        return super.onKeyLongPress(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_sdcard:
                setFragment(SysUtils.FRAGMENT_FILES);
                File dir = new File(sp.getString(SysUtils.PREFS_PATH_SDCARD, "/"));
                if (dir.canRead())
                    filesFragment.openDir(dir);
                else
                    SysUtils.Snack(fab, getString(R.string.storage_err));
                break;
            case R.id.nav_home:
                setFragment(SysUtils.FRAGMENT_FILES);
                filesFragment.openDir(sp.getString(SysUtils.PREFS_HOME_PATH, "/"));
                break;
            case R.id.nav_tools:
                setFragment(SysUtils.FRAGMENT_TOOLS);
                break;
            case R.id.nav_task_1:
                filesFragment.openTask(0);
                break;
            case R.id.nav_task_2:
                filesFragment.openTask(1);
                break;
            case R.id.nav_task_3:
                filesFragment.openTask(2);
                break;
            case R.id.nav_task_4:
                filesFragment.openTask(3);
                break;
            case R.id.nav_smali_1:
                pathForSmali = smaliPaths[0];
                setFragment(SysUtils.FRAGMENT_SMALI);
                break;
            case R.id.nav_smali_2:
                pathForSmali = smaliPaths[1];
                setFragment(SysUtils.FRAGMENT_SMALI);
                break;
            case R.id.nav_smali_3:
                pathForSmali = smaliPaths[2];
                setFragment(SysUtils.FRAGMENT_SMALI);
                break;
            case R.id.nav_smali_4:
                pathForSmali = smaliPaths[3];
                setFragment(SysUtils.FRAGMENT_SMALI);
                break;
            case R.id.nav_prefs:
                startActivity(new Intent(ApkToolActivity.this, SettingsActivity.class)
                        .putExtra(Cs.ARG_PREF_TAB, Cs.TAB_DECOMPILER));
                break;
            case R.id.nav_about: {
                startActivity(new Intent(ApkToolActivity.this, SettingsActivity.class)
                        .putExtra(Cs.ARG_PREF_TAB, Cs.TAB_ABOUT));
                break;
            }
            case R.id.nav_exit:
                finish();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public boolean onCreateOptionsMenu(Menu paramMenu) {
        getMenuInflater().inflate(R.menu.menu_decompiler, paramMenu);
        parMenu = paramMenu;
        if (getIntent() != null)
            onNewIntent(getIntent());

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.smali_clear:
                smaliFragments[curSmaliFragment].menuAction(SysUtils.MENU_CLEAR);
                break;
            case R.id.smali_search:
                smaliFragments[curSmaliFragment].menuAction(SysUtils.MENU_SEARCH);
                break;
            case R.id.smali_prev:
                smaliFragments[curSmaliFragment].menuAction(SysUtils.MENU_PREV);
                break;
            case R.id.smali_next:
                smaliFragments[curSmaliFragment].menuAction(SysUtils.MENU_NEXT);
                break;
            case R.id.smali_save:
                smaliFragments[curSmaliFragment].menuAction(SysUtils.MENU_SAVE);
                break;
            case R.id.smali_save_patch:
                smaliFragments[curSmaliFragment].menuAction(SysUtils.MENU_SAVE_PATCH);
                break;
            case R.id.smali_close:
                smaliFragments[curSmaliFragment].menuAction(SysUtils.MENU_CLOSE);
                break;
            case R.id.command:
                if (execEditText.length() == 0) {
                    execEditText.setText("cd \"" + filesFragment.getCurrentParentPath() + "\"\n");
                    execEditText.setSelection(execEditText.length());
                }
                if (filesFragment.getCurrentParentPath().contains(" ") && !useRoot)
                    SysUtils.Snack(fab, getString(R.string.replace_spaces));
                else
                    execDialog.show();
                execDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                break;
            case R.id.refresh:
                filesFragment.refreshDir();
                break;
        }
        return true;
    }


    // ---------------------------------------------------------------------------------------------


    void setFragment(String fragmentAlias) {
        SysUtils.Log("setFragment(" + fragmentManager.getBackStackEntryCount() + ")");

        Fragment newFragment = null;
        boolean toBackStack = false;

        if (parMenu != null) {
            parMenu.setGroupVisible(R.id.menu_main, false);
            parMenu.setGroupVisible(R.id.menu_smali, false);
            parMenu.setGroupVisible(R.id.menu_keystore, false);

            toolsMenuItem.setChecked(false);
            sdcardMenuItem.setChecked(false);
            homeMenuItem.setChecked(false);
        }

        switch (fragmentAlias) {
            case SysUtils.FRAGMENT_START:
                SysUtils.Log("_START");
                newFragment = new StartFragment();

                break;
            case SysUtils.FRAGMENT_FILES:
                SysUtils.Log("_FILES");
                if (parMenu != null)
                    parMenu.setGroupVisible(R.id.menu_main, true);

                fragmentManager.popBackStack();
                fragmentManager.beginTransaction().replace(R.id.container, filesFragment).commitAllowingStateLoss();

                actionListener.onAction(SysUtils.FR_NAV_MENU, null, wasOpened, false); // ох как зумутил, ой апиздц...
                return;
            case SysUtils.FRAGMENT_TOOLS:
                SysUtils.Log("_TOOLS");
                toBackStack = true;

                newFragment = toolsFragment;
                toolsMenuItem.setChecked(true);
                break;
            case SysUtils.FRAGMENT_PREVIEW:
                SysUtils.Log("_PREVIEW");
                toBackStack = true;

                newFragment = previewFragment;
                break;
            case SysUtils.FRAGMENT_SMALI:
                SysUtils.Log("_SMALI");
                toBackStack = true;
                parMenu.setGroupVisible(R.id.menu_smali, false);

                int k = 0;
                for (String path : smaliPaths)
                    if (path.equals(pathForSmali))
                        break;
                    else
                        k++;
                if (k < 4)
                    newFragment = smaliFragments[k];
                else {
                    k = 0;
                    for (String path : smaliPaths)
                        if (path.isEmpty())
                            break;
                        else
                            k++;
                    if (k > 3) {
                        SysUtils.Snack(fab, getString(R.string.no_free_smali));
                        return;
                    }
                    smaliPaths[k] = pathForSmali;
                    smaliNavItems[k].setVisible(true);
                    smaliNavItems[k].setTitle(SysUtils.getLastPart(pathForSmali, '/'));
                    smaliFragments[k] = SmaliFragment.newInstance(pathForSmali);
                    smaliFragments[k].setMenuArrs(parMenu.findItem(R.id.smali_prev), parMenu.findItem(R.id.smali_next));
                    newFragment = smaliFragments[k];
                }
                openedSmaliPath = pathForSmali;
                parMenu.setGroupVisible(R.id.menu_smali, true);
                curSmaliFragment = k;
                break;
            case SysUtils.FRAGMENT_KEYSTORE:
                parMenu.setGroupVisible(R.id.menu_keystore, false);
                SysUtils.Toast(co, "This feature is not implemented yet (keytool)");
                return;
            case SysUtils.FRAGMENT_PREFS:
                toBackStack = true;
                if (prefsFragment == null)
                    prefsFragment = new DecompilerSettingsFragment();
                newFragment = prefsFragment;
                break;
            default:
                toBackStack = true;
                break;
        }
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentManager.popBackStack();

        if (toBackStack)
            fragmentTransaction.addToBackStack("FRAGMENT");
        SysUtils.Log("FRAGMENT");
        fragmentTransaction.replace(R.id.container, newFragment);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);

        fragmentTransaction.commitAllowingStateLoss();
        SysUtils.Log("setFragment(" + fragmentManager.getBackStackEntryCount() + ") END");
    }

    public void updateVersions() {
        if (!turnedOn)
            return;

        String n = "";
        StringBuilder text = new StringBuilder();
        for (String toolName : SysUtils.TOOLS_ARR) {
            String ver = sp.getString(toolName, "");
            text.append(n);
            if (ver.isEmpty())
                text.append(toolName).append(getString(R.string.missing));
            else
                text.append(ver);
            n = "\n";
        }
        navTools.setText(text.toString());
    }

    public void closeSmali() {
        int k = 0;
        for (String path : smaliPaths)
            if (path.equals(pathForSmali))
                break;
            else
                k++;
        if (k < 4) {
            smaliFragments[k] = null;
            smaliPaths[k] = "";
            smaliNavItems[k].setVisible(false);
            pathForSmali = "";
        } else
            SysUtils.Log("--ERROR in smaliPaths (close)");
        setFragment(SysUtils.FRAGMENT_FILES);
    }

    void exec() {
        String com = execEditText.getText().toString();
        executedTextView.setText(Cmd.Exec(useRoot, com).getResult());
        executedDialog.show();
    }

    // ---------------------------------------------------------------------------------------------

    public void snack(String message) {
        SysUtils.Snack(fab, message);
    }

    private class ActionListener implements SysUtils.ActionListener {

        // следующий метод вызывается в совершенно разных, ничем не связанных,
        // и друг на друга никак не похожих, случаях
        // не пытайтесь понять в каких, что и как, не надо)
        @Override
        public void onAction(int action, String stringData, int intData, boolean boolData) {// Bundle bundle) {
            if (stringData == null)
                stringData = "--ERROR action=" + action;
            switch (action) {
                case SysUtils.FR_NAV_MENU:
                    wasOpened = intData; // уже догадываешься?...
                    sdcardMenuItem.setChecked(intData == SysUtils.MENU_SDCARD);
                    homeMenuItem.setChecked(intData == SysUtils.MENU_HOME);
                    break;
                case SysUtils.FR_NAV_TASK:
                    SysUtils.Log("stringData = " + stringData + " intData =" + intData + " boolData = " + boolData);
                    taskNavItems[intData].setVisible(boolData);
                    taskNavItems[intData].setTitle(stringData);
                    break;
                case SysUtils.FR_SNACK:
                    SysUtils.Snack(fab, boolData ? stringData : getString(intData));
                    break;
                case SysUtils.FR_SMALI:
                    pathForSmali = stringData;
                    setFragment(SysUtils.FRAGMENT_SMALI);
                    break;
                case SysUtils.FR_PREVIEW:
                    pathForPreview = stringData;
                    SysUtils.Log("pathForPreview: " + pathForPreview);
                    previewFragment.setPathFile(pathForPreview);
                    setFragment(SysUtils.FRAGMENT_PREVIEW);
                    break;
                case SysUtils.FR_KEYSTORE:
                    pathForKeyStore = stringData;
                    if (boolData)
                        setFragment(SysUtils.FRAGMENT_KEYSTORE);
                    else
                        setFragment(SysUtils.FRAGMENT_FILES);
                    break;
                case SysUtils.FR_VERSIONS_CHANGE:
                    updateVersions();
                    break;
            }
        }
    }

}
