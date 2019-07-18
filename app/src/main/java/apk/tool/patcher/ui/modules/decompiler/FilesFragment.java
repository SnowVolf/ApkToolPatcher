package apk.tool.patcher.ui.modules.decompiler;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import apk.tool.patcher.App;
import apk.tool.patcher.R;
import apk.tool.patcher.ui.widget.FastScroller;
import apk.tool.patcher.util.Preferences;
import apk.tool.patcher.util.SysUtils;
import ru.atomofiron.apknator.Adapters.RecyclerAdapter;
import ru.atomofiron.apknator.Managers.FileManager;
import ru.atomofiron.apknator.Managers.KeyStoreManager;
import ru.atomofiron.apknator.Managers.TaskManager;
import ru.atomofiron.apknator.Managers.ToolsManager;
import ru.atomofiron.apknator.Utils.Cmd;


public class FilesFragment extends Fragment implements TaskManager.TaskListener, RecyclerAdapter.OnItemClickListener
        /*, SwipeRefreshLayout.OnRefreshListener*/ {

    TextView mPathTextView;
    FastScroller mScroller;
    View savedView = null;
    RecyclerView mFileView;
    SharedPreferences mPreferences;
    //EditText inputPatch;
    Context mContext;
    ApkToolActivity mActivity;
    File currentParent;
    File[] mCurrentFiles;
    AlertDialog exitDialog;
    AlertDialog decompileDialog;
    AlertDialog archiveDialog;
    AlertDialog keystoreDialog;
    ProgressDialog progressDialog;
    RecyclerAdapter mFileAdapter;
    boolean fragmentCreated = false;
    String uri;
    String dataPath;
    String filesPath;
    String filesPathSdcard;
    String toolsPath;
    String toolsPathSdcard;
    String sdCardPath;
    String homePath;
    String scriptsPath;
    boolean seriously;
    private boolean stopAnims = true;
    private TaskManager taskManager;
    private KeyStoreManager keyStoreManager;
    private SysUtils.ActionListener actionListener;

    public FilesFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SysUtils.Log("onCreate()");
        mContext = getContext();
        mActivity = (ApkToolActivity) getActivity();
        taskManager = new TaskManager(mActivity, actionListener);
        taskManager.setTaskListener(this);
        mPreferences = SysUtils.SP(mContext);
        keyStoreManager = new KeyStoreManager(mContext);

        dataPath = mContext.getApplicationInfo().dataDir;
        filesPath = mContext.getFilesDir().getAbsolutePath();
        filesPathSdcard = mContext.getExternalFilesDir(null).getAbsolutePath();
        toolsPath = mContext.getFilesDir() + "/tools";
        toolsPathSdcard = mContext.getExternalFilesDir(null) + "/tools";
        sdCardPath = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ?
                Environment.getExternalStorageDirectory().getAbsolutePath() : "/";
        scriptsPath = SysUtils.getScriptsPath(mContext);

        progressDialog = new ProgressDialog(mActivity);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        exitDialog = new AlertDialog.Builder(mContext)
                .setMessage(getString(R.string.want_to_exit))
                .setNegativeButton(getString(R.string.no), null)
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mActivity.finish();
                    }
                })
                .create();
        decompileDialog = new AlertDialog.Builder(mContext)
                .setTitle(R.string.decompile)
                .setPositiveButton(getString(R.string.cancel), null)
                .setItems(R.array.mode_arr, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        taskManager.startDecompleApk(uri, which);
                    }
                })
                .create();
        archiveDialog = new AlertDialog.Builder(mContext)
                .setItems(R.array.arch_arr, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                String what = "classes.dex";
                                SysUtils.Log("getParentPath = " + SysUtils.getParentPath(uri));
                                if (!new File(SysUtils.getParentPath(uri) + "/classes.dex").exists())
                                    taskManager.startExtract(uri, what, R.string.extract_finish);
                                else
                                    actionListener.onAction(SysUtils.FR_SNACK, null, R.string.dex_exist, false);
                                break;
                            case 1:
                                what = "classes.dex";
                                taskManager.startArchDelete(uri, what, R.string.delete_finish);
                                break;
                            case 2:
                                what = "classes.dex";
                                if (new File(SysUtils.getParentPath(uri) + "/classes.dex").exists())
                                    taskManager.startArchive(uri, what, R.string.add_finish);
                                else
                                    actionListener.onAction(SysUtils.FR_SNACK, null, R.string.dex_not_exist, false);
                                break;
                            case 3:
                                what = "META-INF";
                                SysUtils.Log("getParentPath = " + SysUtils.getParentPath(uri));
                                if (!new File(SysUtils.getParentPath(uri) + "/META-INF").exists())
                                    taskManager.startExtract(uri, what, R.string.extract_finish);
                                else
                                    actionListener.onAction(SysUtils.FR_SNACK, null, R.string.dex_exist, false);
                                break;
                            case 4:
                                what = "META-INF";
                                taskManager.startArchDelete(uri, what, R.string.delete_finish);
                                break;
                            case 5:
                                what = "META-INF";
                                if (new File(SysUtils.getParentPath(uri) + "/META-INF").exists())
                                    taskManager.startArchive(uri, what, R.string.add_finish);
                                else
                                    actionListener.onAction(SysUtils.FR_SNACK, null, R.string.dex_not_exist, false);
                                break;
                        }
                    }
                })
                .setPositiveButton(getString(R.string.cancel), null)
                .create();
        keystoreDialog = new AlertDialog.Builder(mContext)
                .setItems(R.array.keystore_arr, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0: // keytool -keystore keys.jks -list -storepass PASS
                                keyStoreManager.list(uri);
                                break;
                            case 1: // keytool -keystore keys.jks -exportcert -alias raslav -storepass PASS -file pk8
                                keyStoreManager.exportPK8(uri);
                                break;
                            case 2: // keytool -keystore keys.jks -exportcert -alias raslav -storepass PASS -file x509 -rfc
                                keyStoreManager.exportX509(uri);
                                break;
                            case 3: // keytool -keystore keys.jks -importcert -alias raslav -storepass PASS -file pk8 -noprompt
                                keyStoreManager.importPK8(uri);
                                break;
                            case 4: // keytool -keystore keys.jks -importcert -alias raslav -storepass PASS -file x509 -noprompt
                                keyStoreManager.importX509(uri);
                                break;
                            case 5: // keytool -keystore keys.jks -delete -alias raslav -storepass PASS
                                keyStoreManager.delete(uri);
                                break;
                        }
                    }
                })
                .setPositiveButton(getString(R.string.cancel), null)
                .create();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        SysUtils.Log("onCreateView()");
        if (savedView != null)
            return savedView;
        final View view = inflater.inflate(R.layout.fragment_files, container, false);
        if (!fragmentCreated)
            init(view);

        return view;
    }

    void init(View view) {
        fragmentCreated = true;

        mPathTextView = view.findViewById(R.id.tvpath);

        String path = mPreferences.getString(SysUtils.PREFS_PATH_SDCARD, sdCardPath);
        path = mPreferences.getBoolean(SysUtils.PREFS_REMEMBER_PATH, false) ? mPreferences.getString(SysUtils.PREFS_REMEMBERED_PATH, path) : mPreferences.getString(SysUtils.PREFS_HOME_PATH, path);
        if (!new File(path).canRead())
            path = sdCardPath;
        if (!new File(path).canRead())
            path = "/";
        homePath = path;
        currentParent = new File(path);
        mScroller = view.findViewById(R.id.fast_scroll);
        mScroller.setPressedHandleColor(App.getColorFromAttr(getContext(), R.attr.colorAccent));
        mFileView = view.findViewById(R.id.list_view);
        mFileView.setHasFixedSize(true);
        mFileView.setLayoutManager(new GridLayoutManager(mContext, Preferences.getGridSize()));
        mFileAdapter = new RecyclerAdapter(mActivity, mFileView);
        mFileView.setAdapter(mFileAdapter);
        mFileAdapter.setOnItemClickListener(this);
        mScroller.setRecyclerView(mFileView, Preferences.getGridSize());
        refreshDir();
    }

    @Override
    public void onStart() {
        super.onStart();
        mFileAdapter.lineEffect = mPreferences.getBoolean(SysUtils.PREFS_LINE_EFFECT, true);
    }

    public void openTask(int num) {
        taskManager.openTask(num);
    }

    public void openDir(String path) {
        if (!currentParent.getAbsolutePath().equals(path))
            openDir(new File(path));
    }

    public void openDir(File dir) {
        if (!dir.isDirectory() || currentParent.equals(dir))
            return;
        if (dir.canRead() || mPreferences.getBoolean(SysUtils.PREFS_USE_ROOT, false)) {
            currentParent = dir;
            refreshDir();
        } else
            SysUtils.Toast(mContext, getString(R.string.directory_no_permission));
    }

    public void refreshDir() {
        uri = currentParent.getAbsolutePath();
        mCurrentFiles = currentParent.listFiles();
        if (mCurrentFiles == null && mPreferences.getBoolean(SysUtils.PREFS_USE_ROOT, false)) {
            String parentPath = currentParent.getAbsolutePath();
            mCurrentFiles = FileManager.Filek.getFiles(parentPath);
        }
        if (mCurrentFiles == null)
            mCurrentFiles = new File[0];
        if (mCurrentFiles.length > 0)
            mPreferences.edit().putString(SysUtils.PREFS_REMEMBERED_PATH, currentParent.getAbsolutePath()).apply();
        try {
            actionListener.onAction(SysUtils.FR_NAV_MENU, null, getMenuInt(), false);
        } catch (Exception err) {
            err.printStackTrace();
        }

        Animation animDown = AnimationUtils.loadAnimation(mContext, R.anim.list_down);
        animDown.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                updateRecyclerView();
                Animation animUp = AnimationUtils.loadAnimation(mContext, R.anim.list_up);
                mFileView.startAnimation(animUp);
            }
        });
        if (mPreferences.getBoolean(SysUtils.PREFS_LINE_EFFECT, false))
            mFileView.startAnimation(animDown);
        else
            updateRecyclerView();
    }

    public int getMenuInt() {
        return homePath.equals(currentParent.getAbsolutePath()) ? SysUtils.MENU_HOME :
                sdCardPath.equals(currentParent.getAbsolutePath()) ? SysUtils.MENU_SDCARD : 0;
    }

    private void updateRecyclerView() {
        mCurrentFiles = currentParent.listFiles();
        Arrays.sort(mCurrentFiles, new SysUtils.FileComparator());
        mPathTextView.setText(uri);
        mFileAdapter.setData(mCurrentFiles, seriously);
    }

    public void onBackPressed() {
        if (!currentParent.getAbsolutePath().equals("/") &&
                (mPreferences.getBoolean(SysUtils.PREFS_USE_ROOT, false) ||
                        currentParent.getParentFile().listFiles() != null)) {
            seriously = true;
            openDir(currentParent.getParentFile());
        } else
            exitDialog.show();
    }

    public String getCurrentParentPath() {
        return currentParent.getAbsolutePath();
    }

    @Override
    public void onStop() {
        super.onStop();
        savedView = getView();
    }

    @Override
    public void onResume() {
        super.onResume();
        mFileAdapter.animResId = SysUtils.ANIM_IDS[Integer.parseInt(mPreferences.getString(SysUtils.PREFS_LIST_ANIM, "1"))];
    }

    void actionConfig() {
        SysUtils.Log("actionConfig()");
        new AlertDialog.Builder(mContext)
                .setItems(uri.endsWith(".apk") ? R.array.config_arr : R.array.config_jar_arr, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int action) {
                        switch (action) {
                            case 0:
                                taskManager.startSign(uri);
                                break;
                            case 1:
                                taskManager.startZipalign(uri);
                                break;
                            case 2:
                                taskManager.startOdex(uri);
                                break;
                            case 3:
                                taskManager.startImport(uri);
                                break;
                            default:
                                SysUtils.Toast(mContext, "Error. no option");
                        }
                    }
                }).setNegativeButton(getString(R.string.cancel), null).create().show();
    }

    @Override
    public void onTask(int taskNum, String taskName, boolean start) {
        SysUtils.Log("onTask() taskNum = " + taskNum + " taskName = " + taskName + " start = " + start);
        if (!start) {
            seriously = false;
            refreshDir();
        }
        actionListener.onAction(SysUtils.FR_NAV_TASK, taskName, taskNum, start);
    }

    public void setActionListener(SysUtils.ActionListener listener) {
        actionListener = listener;
    }

    @Override
    public void onItemClick(View v, int position) {
        SysUtils.Log("onItemClick() " + position);
        File file = mCurrentFiles[position];
        //if (!file.canRead()) return;
        uri = file.getAbsolutePath();
        SysUtils.Log("File = " + file.getAbsolutePath());
        String name = file.getName();
        if (file.isDirectory()) {
            seriously = true;
            openDir(file);
        } else if (name.endsWith(".smali"))
            startActivity(new Intent()
                    .setAction(android.content.Intent.ACTION_VIEW)
                    .setDataAndType(Uri.fromFile(file), MimeTypeMap.getSingleton().getMimeTypeFromExtension("txt")));
        else if (name.endsWith(".bks") || name.endsWith(".jks") || name.endsWith(".keystore")) {
            actionListener.onAction(SysUtils.FR_KEYSTORE, uri, 0, true);
        } else {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    MimeTypeMap.getFileExtensionFromUrl("file." + SysUtils.getFormat(uri))); // некорректно работает с пробелами
            if (mime != null)
                SysUtils.Log("MIME " + mime);

            Uri uriIntent = Build.VERSION.SDK_INT < 24 ? Uri.fromFile(new File(uri)) :
                    FileProvider.getUriForFile(mContext, mContext.getApplicationContext().getPackageName() + ".provider", new File(uri));
            intent.setDataAndType(uriIntent, mime == null ? "*/*" : mime);

            List<ResolveInfo> resInfoList = mContext.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            for (ResolveInfo resolveInfo : resInfoList) {
                String packageName = resolveInfo.activityInfo.packageName;
                mContext.grantUriPermission(packageName, uriIntent, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }

            if (intent.resolveActivity(mContext.getPackageManager()) != null)
                startActivity(intent);
            else
                SysUtils.Toast(mContext, getString(R.string.no_activity));
        }
    }

    @Override
    public void onItemLongClick(View v, int position) {
        String path1 = mCurrentFiles[position].getAbsolutePath();
        //  actionListener.onAction(SysUtils.FR_PREVIEW, path, 0, false);

        //  final File file = path[position];

        final File file = new File(path1);

        if (file.isDirectory()) {
            if (file.canWrite()) {
                new AlertDialog.Builder(mContext).
                        setTitle("Set a folder").
                        setMessage("To select the \"" + file.getName() + "\" folder?").
                        setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface p1, int p2) {
                                Intent intent1 = new Intent();
                                intent1.putExtra("path", file.getPath());
                                Toast.makeText(getApplicationContext(), file.getPath(), Toast.LENGTH_SHORT).show();
                                int RESULT_OK = 0;
                                mActivity.setResult(RESULT_OK, intent1);
                                mActivity.finish();
                                // MainActivity.mContext.inputPatch.setText(path);
                                // finish();
                                // TODO: Implement this method
                            }

//                        private void setResult(boolean seriously, Intent intent1)
//                        {
//                            // TODO: Implement this method
//                        }
                        }).
                        setNegativeButton(android.R.string.cancel, null).
                        show();
            } else {
                Toast.makeText(getApplicationContext(), "No write permission", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), file.getName(), Toast.LENGTH_SHORT).show();
        }

    }

    private Context getApplicationContext() {
        // TODO: Implement this method
        return mContext;
    }

    @Override
    public void onItemButtonClick(int id, int position) {
        SysUtils.Log("onItemButtonClick() " + position);
        File file = mCurrentFiles[position];
        final boolean useRoot = mPreferences.getBoolean(SysUtils.PREFS_USE_ROOT, false);
        if (!file.canRead() && !useRoot)
            return;
        uri = mCurrentFiles[position].getAbsolutePath();
        switch (id) {
            case R.id.delete:
                delete(useRoot);
                break;
            case R.id.rename:
                rename(useRoot);
                break;
            case R.id.operation_1:
                operation1(file);
                break;
            case R.id.operation_2:
                operation2(file);
                break;
            case R.id.operation_3:
                SysUtils.Log("Archive!");
                archiveDialog.show();
                break;
        }
    }

    private void delete(final boolean useRoot) {
        new AlertDialog.Builder(mContext)
                .setTitle(getString(R.string.want_to_delete))
                .setPositiveButton(
                        getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(
                                    DialogInterface dialog,
                                    int which) {
                                File file = new File(uri);
                                if (file.isDirectory())
                                    taskManager.startDelete(uri, R.string.delete_finish);
                                else if (file.delete()) {
                                    seriously = false;
                                    refreshDir();
                                    actionListener.onAction(SysUtils.FR_SNACK, null, R.string.delete_finish, false);
                                } else if (useRoot) {
                                    Cmd.easySuExec("rm -r \"" + uri + "\"");
                                    seriously = false;
                                    refreshDir();
                                } else
                                    actionListener.onAction(SysUtils.FR_SNACK, null, R.string.del_error, false);
                            }
                        })
                .setNegativeButton(getString(R.string.cancel), null).create().show();
    }

    private void rename(final boolean useRoot) {
        final EditText et = new EditText(mContext);
        et.setText(new File(uri).getName());
        et.setSelection(et.length());
        AlertDialog alertDialog = new AlertDialog.Builder(mContext)
                .setTitle(R.string.rename)
                .setView(et)
                .setPositiveButton(
                        getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(
                                    DialogInterface dialog,
                                    int which) {
                                String newName = et.getText().toString();
                                if (!new File(uri).renameTo(new File(currentParent + "/" + newName))) {
                                    if (useRoot)
                                        Cmd.easySuExec("mv \"" + uri + "\" \"" + currentParent + "/" + newName + "\"");
                                    else
                                        actionListener.onAction(SysUtils.FR_SNACK, null, R.string.error, false);
                                }
                                seriously = false;
                                refreshDir();
                            }
                        })
                .setNegativeButton(getString(R.string.cancel), null).create();
        alertDialog.show();
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    private void operation1(File file) {
        String name;
        if (file.isDirectory()) {
            if ((name = file.getName()).endsWith("_src"))
                taskManager.startCompileApk(uri);
            else if (name.endsWith("_jar"))
                taskManager.startCompileJar(uri); // убрано
            else if (name.endsWith("_dex") || name.endsWith("_odex"))
                taskManager.startCompileDex(uri);
            else {
                mPreferences.edit().putString(SysUtils.PREFS_HOME_PATH, uri).apply();
                homePath = uri;
                actionListener.onAction(SysUtils.FR_NAV_MENU, null, getMenuInt(), false);
            }
        } else if ((name = file.getName()).endsWith(".apk"))
            decompileDialog.show();
        else if (name.endsWith(".dex"))
            taskManager.startDecompileDex(uri);
        else if (name.endsWith(".odex") || name.endsWith(".oat"))
            taskManager.startDecompileOdex(uri);
        else if (name.endsWith(".jar"))
            taskManager.startDecompileJar(uri);
        else if (name.endsWith(".smali"))
            actionListener.onAction(SysUtils.FR_SMALI, uri, 0, false);
        else if (name.endsWith(".class"))
            taskManager.startDx(uri);
        else if (name.endsWith(".java"))
            taskManager.startJavac(uri);
        else if (name.endsWith(".jks") || name.endsWith(".keystore"))
            keystoreDialog.show();

    }

    private void operation2(File file) {
        String name = file.getName();
        if (file.isDirectory()) {
            if (!new File(file.getAbsolutePath() + "/openjdk/bin").isDirectory()) {
                actionListener.onAction(SysUtils.FR_SNACK, null, R.string.bin_not_found, false);
                return;
            }
            if (!new File(file.getAbsolutePath() + "/openjdk/lib").isDirectory()) {
                actionListener.onAction(SysUtils.FR_SNACK, null, R.string.lib_not_found, false);
                return;
            }
            mPreferences.edit().putString(SysUtils.PREFS_TOOLS_PATH_SDCARD, uri).apply();
            progressDialog.setMessage(getString(R.string.init));
            progressDialog.show();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ToolsManager.updateTools(mContext);
                    ToolsManager.liteReview(mContext);
                    mActivity.runOnUiThread(new Runnable() {
                        public void run() {
                            mActivity.updateVersions();
                            progressDialog.dismiss();
                        }
                    });
                }
            }).start();
        } else if (name.endsWith(".apk") || name.endsWith(".jar"))
            actionConfig();
        else if (name.endsWith(".dex") || name.endsWith(".odex") || name.endsWith(".oat"))
            actionListener.onAction(SysUtils.FR_PREVIEW, uri, 0, false);
    }

    public RecyclerView getRecyclerView() {
        return mFileView;
    }

    public TaskManager getTaskManager() {
        return taskManager;
    }
}
