package apk.tool.patcher.ui.modules.main;


import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.collection.ArrayMap;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.yandex.metrica.YandexMetrica;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import apk.tool.patcher.App;
import apk.tool.patcher.R;
import apk.tool.patcher.api.Project;
import apk.tool.patcher.entity.AdsPatcher;
import apk.tool.patcher.entity.LogicalCore;
import apk.tool.patcher.ui.modules.base.adapters.AdvancedAdapter;
import apk.tool.patcher.ui.modules.base.adapters.ExtendedMenuAdapter;
import apk.tool.patcher.ui.modules.base.adapters.MenuAdapter;
import apk.tool.patcher.ui.modules.inspector.InspectorFragment;
import apk.tool.patcher.ui.modules.misc.SelectActivity;
import apk.tool.patcher.ui.modules.odex.OdexPatchFragment;
import apk.tool.patcher.ui.modules.settings.SettingsActivity;
import apk.tool.patcher.ui.widget.FontTextView;
import apk.tool.patcher.util.Cs;
import apk.tool.patcher.util.Preferences;
import apk.tool.patcher.util.RegexpRepository;
import apk.tool.patcher.util.StreamUtil;
import apk.tool.patcher.util.TextUtil;
import ru.svolf.appmanager.AppManagerFragment;
import ru.svolf.melissa.AdvancedItems;
import ru.svolf.melissa.ExtendedItems;
import ru.svolf.melissa.MenuItems;
import ru.svolf.melissa.compat.Compat;
import ru.svolf.melissa.fragment.dialog.SweetContentDialog;
import ru.svolf.melissa.fragment.dialog.SweetInputDialog;
import ru.svolf.melissa.fragment.dialog.SweetListDialog;
import ru.svolf.melissa.fragment.dialog.SweetViewDialog;
import ru.svolf.melissa.fragment.dialog.SweetWaitDialog;
import ru.svolf.melissa.model.AdvancedItem;
import ru.svolf.melissa.model.ExtendedMenuItem;
import ru.svolf.melissa.model.MenuItem;
import sun.security.pkcs.PKCS7;

/**
 * Created by Snow Volf on 02.09.2017, 12:29
 */

@SuppressWarnings("ConstantConditions, ResultOfMethodCallIgnored, unchecked, InflateParams, HandlerLeak")
public class MainFragment extends Fragment {
    public static final String FRAGMENT_TAG = "main_fragment";
    private static final String TAG = "MainFragment";
    public static String premium;
    public static String saveToast = "text_toast", saveCode = "edoc_prem";
    public static String extXml = ".xml",
            nol = "", ssha, sss,
    // Premium
    muimerp, secret, ANDROID_ID, mydeviceaddress, edoc, codepremium2 = "null";
    private static byte[] signatures;
    private static int i;
    public FontTextView mGeneralInput;
    Context mContext;
    Handler mProgressHandler;
    private View rootView;
    private boolean isPremium = false;
    private SweetWaitDialog mWaitDialog;
    private RecyclerView mList, mListAdvanced;
    private MenuItems allMenuItems = new MenuItems();
    private AdvancedItems allAdvancedItems = new AdvancedItems();
    private CardView mCard;
    private Project mProject;

    static void addMapsKey(String filePath) {
        File fileToBeModified = new File(filePath);
        StringBuilder url = new StringBuilder(nol);
        BufferedReader reader = null;
        FileWriter writer = null;
        try {
            reader = new BufferedReader(new FileReader(fileToBeModified));
            String line = reader.readLine();
            while (line != null) {
                url.append(line).append("\n");
                line = reader.readLine();
            }
            Matcher m = RegexpRepository.get().MAPS_KEY.matcher(url.toString());
            if (m.find()) {
                url = new StringBuilder(m.replaceAll("<meta-data android:name=\"com.google.android." + m.group(1) + ".API_KEY\" android:value=\"AIzaSyCVqD1_AkEk9eW5HWbZw3A34bNIHJY90zI\"/>"));
            }
            writer = new FileWriter(fileToBeModified);
            writer.write(url.toString());
            Toast.makeText(App.get(), R.string.message_done, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static byte[] matchParentLayout(String file) throws Exception {
        //.dsa
        String bse642 = "LkRTQQ==";
        byte[] ggg2 = android.util.Base64.decode(bse642, android.util.Base64.DEFAULT);
        String asd = new String(ggg2, StandardCharsets.UTF_8);
        // .rsa
        String bse641 = "LlJTQQ==";
        byte[] ggg1 = android.util.Base64.decode(bse641, android.util.Base64.DEFAULT);
        String asr = new String(ggg1, StandardCharsets.UTF_8);
        //мета инф
        String bse64 = "TUVUQS1JTkYv";
        // Receiving side
        byte[] ggg = android.util.Base64.decode(bse64, android.util.Base64.DEFAULT);
        String vvv = new String(ggg, StandardCharsets.UTF_8);
        File apkFile = new File(file);
        ZipFile zipFile = new ZipFile(apkFile);
        Enumeration<ZipEntry> entries = (Enumeration<ZipEntry>) zipFile.entries();
        while (entries.hasMoreElements()) {
            ZipEntry ze = entries.nextElement();
            String name = ze.getName().toUpperCase();
            if (name.startsWith(vvv) && (name.endsWith(asr) || name.endsWith(asd))) {
                PKCS7 pkcs7 = new PKCS7(StreamUtil.readBytes(zipFile.getInputStream(ze)));
                Certificate[] certs = pkcs7.getCertificates();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                DataOutputStream dos = new DataOutputStream(baos);
                dos.write(certs.length);
                for (int i = 0; i < certs.length; i++) {
                    byte[] data = certs[i].getEncoded();
                    System.out.printf("  --SignatureHash[%d]: %08x\n", i, Arrays.hashCode(data));
                    dos.writeInt(data.length);
                    dos.write(data);
                }
                return baos.toByteArray();
            }
        }
        throw new Exception("not found asr and asd");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_main, container, false);
        Log.wtf(TAG, "Hello pidor");
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState != null) {
            mProject = savedInstanceState.getParcelable(Cs.ARG_PATH_NAME);
        }
        mContext = getContext();

        ImageButton menu = view.findViewById(R.id.search_menu);
        ImageButton search = view.findViewById(R.id.search_find);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create the fragment only when the activity is created for the first time.
                // ie. not after orientation changes
                Snackbar.make(mCard, "This feature has been disabled until next alpha :(", Snackbar.LENGTH_LONG).show();
//                Fragment fragment = getActivity().getSupportFragmentManager().findFragmentByTag(SearchSettingsFragment.FRAGMENT_TAG);
//                if (fragment == null) {
//                    fragment = new SearchSettingsFragment();
//                }
//                Bundle args = new Bundle();
//                args.putString(Cs.ARG_PATH_NAME, getProjectDir());
//                fragment.setArguments(args);
//
//                // Запускаем транзакцию
//                getActivity().getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.content_frame, fragment, SearchSettingsFragment.FRAGMENT_TAG)
//                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
//                        .addToBackStack(null)
//                        .commit();
            }
        });
        // Добавляем иконку меню
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Инфлейтим диалог с контейнером
                final SweetViewDialog dialog = new SweetViewDialog(mContext);
                // Инициализируем пункты меню
                List<ExtendedMenuItem> extendedMenuItems = new ArrayList<>();
                // Добавляем пункты
                extendedMenuItems.add(new ExtendedMenuItem(
                        R.drawable.settings_outline,
                        "#607d8b",
                        App.bindString(R.string.menu_settings),
                        ExtendedItems.SETTINGS)
                );
                extendedMenuItems.add(new ExtendedMenuItem(
                        R.drawable.ic_apkeditor,
                        "#ffb600",
                        App.bindString(R.string.menu_components),
                        ExtendedItems.PLUGIN)
                );
                extendedMenuItems.add(new ExtendedMenuItem(
                        R.drawable.apps_list,
                        "#f57c00",
                        App.bindString(R.string.menu_appslist),
                        ExtendedItems.APPS_LIST)
                );
                extendedMenuItems.add(new ExtendedMenuItem(
                        R.drawable.verified,
                        "#00c853",
                        App.bindString(R.string.menu_buy_premium),
                        ExtendedItems.PREMIUM)
                );
                extendedMenuItems.add(new ExtendedMenuItem(
                        R.drawable.ic_info,
                        "#448aff",
                        App.bindString(R.string.menu_about),
                        ExtendedItems.ABOUT)
                );

                // Инициализация адаптера из пунктов
                ExtendedMenuAdapter adapter = new ExtendedMenuAdapter(extendedMenuItems);
                // Слушатели нажатий
                adapter.setItemClickListener(new ExtendedMenuAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(ExtendedMenuItem item, int position) {
                        int id = item.getAction();
                        switch (id) {
                            case ExtendedItems.SETTINGS: {
                                YandexMetrica.reportEvent("Settings screen clicked");
                                Intent intent1 = new Intent(mContext, SettingsActivity.class);
                                startActivity(intent1);
                                getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                break;
                            }
                            case ExtendedItems.PLUGIN:
                                YandexMetrica.reportEvent("Plugin screen clicked");
                                try {
                                    mContext.startActivity(mContext.getPackageManager().getLaunchIntentForPackage("htc.patch.lib"));
                                } catch (Exception err) {
                                    Toast.makeText(getActivity(), R.string.message_app_not_found, Toast.LENGTH_LONG).show();
                                    TextUtil.goLink(getActivity(), "http://4pda.ru/forum/index.php?showtopic=461675&view=findpost&p=68457132");
                                }
                                break;
                            case ExtendedItems.ABOUT: {
                                YandexMetrica.reportEvent("About screen clicked");
                                Intent intent1 = new Intent(mContext, SettingsActivity.class);
                                intent1.putExtra(Cs.ARG_PREF_TAB, Cs.TAB_ABOUT);
                                startActivity(intent1);
                                getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                break;
                            }
                            case ExtendedItems.APPS_LIST: {
                                YandexMetrica.reportEvent("Apps List screen clicked");
                                // Create the fragment only when the activity is created for the first time.
                                // ie. not after orientation changes
                                Fragment fragment = getActivity().getSupportFragmentManager().findFragmentByTag(AppManagerFragment.FRAGMENT_TAG);
                                if (fragment == null) {
                                    fragment = new AppManagerFragment();
                                }
                                // Запускаем транзакцию
                                getActivity().getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.content_frame, fragment, AppManagerFragment.FRAGMENT_TAG)
                                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                        .addToBackStack(null)
                                        .commit();
                                break;
                            }
                            case ExtendedItems.PREMIUM:
                                YandexMetrica.reportEvent("Activation dialog showed");
                                achk();//PREMIUM ACTIVATION DIALOG
                                break;
                            default:
                                break;
                        }
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }
                });

                // Инфлейтим контент
                View content = LayoutInflater.from(getContext()).inflate(R.layout.dialog_exp_items, null);
                // Находим наш сптсок
                RecyclerView recyclerView = content.findViewById(R.id.list);
                // Присваиваем лайаут менеджер
                recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
                // Устанавливаем адаптер
                recyclerView.setAdapter(adapter);
                //
                recyclerView.setHasFixedSize(true);

                dialog.setTitle(getString(R.string.caption_main_menu));
                dialog.setView(recyclerView);
                dialog.show();
            }
        });
//
        mGeneralInput = view.findViewById(R.id.field_input);
        mList = view.findViewById(R.id.list_patches);
        mListAdvanced = view.findViewById(R.id.list_patches_advanced);
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mCard = view.findViewById(R.id.card);

        // Check PREMIUM
        try {
            settingsLayout();
            BApro();
            kcehc();
        } catch (Exception e) {
            e.printStackTrace();
        }

        kcehc1(); // PREMIUM

        if (savedInstanceState != null) {
            mProject = (Project) savedInstanceState.getSerializable(Cs.ARG_PATH_NAME);
            if (mProject != null)
                mGeneralInput.setText(mProject.getPath());
        }

        mGeneralInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View p1) {
                final Intent intent = new Intent(mContext, SelectActivity.class);
                startActivityForResult(intent, 1);
                getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        // Куда же ты полез, парень? Это статья...
        byte[] sbs = android.util.Base64.decode("0JrRg9C00LAg0LbQtSDRgtGLINC/0L7Qu9C10LcsINC/0LDRgNC10L3RjD8hINCt0YLQviDRgdGC0LDRgtGM0Y8uLi4=", Base64.DEFAULT);
        // Щютка
        if (isPremium) {
            Snackbar.make(mList, new String(sbs), Snackbar.LENGTH_INDEFINITE).show();
        }

        mProgressHandler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == -1) {
                    Toast.makeText(getActivity(), App.bindString(R.string.message_patched_over, i), Toast.LENGTH_LONG).show();
                    i = 0;
                    if (mWaitDialog != null && mWaitDialog.isShowing())
                        mWaitDialog.dismiss();
                    showAdvert();
                } else {
                    Toast.makeText(getActivity(), App.bindString(R.string.message_patched, msg.what), Toast.LENGTH_LONG).show();
                }
            }
        };
        initRecycler();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) return;
        String path = data.getStringExtra("path");
        Toast.makeText(mContext, path, Toast.LENGTH_SHORT).show();
//        if (sss.contains(ssha)) {
        mGeneralInput.setText(path);
        mProject = new Project(path);
        App.setCurrentProject(mProject);
        //TODO: Разкомментить в релизе
//        } else {
//            String base64 = "YWQ=";
//            // Receiving side
//            byte[] ggg = android.util.Base64.decode(base64, android.util.Base64.DEFAULT);
//            try {
//                mGeneralInput.setText(path);
//                if (isValidProjectDir(path)) {
//                    publicString2(getProjectDir());
//                }
//                String vvv = new String(ggg, "UTF-8");
//                mGeneralInput.setText(String.format("%s%s", path, vvv));
//            } catch (UnsupportedEncodingException ignored) {
//
//            }
//        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (mProject != null) {
            outState.putParcelable(Cs.ARG_PATH_NAME, mProject);
        }
    }

    @Override
    public void onDestroyView() {
        rootView = null;
        super.onDestroyView();
    }

    /**
     * Коверкаем имена стрингов, если подпись не совпадает
     * <p>
     * ЗАМЕНА ТОЛЬКО В XML
     */
    public void publicString2(final String guf) {
        Thread t = new Thread(new Runnable() {
            public void run() {
                ArrayMap<Pattern, String> pat1 = new ArrayMap<Pattern, String>();
                pat1.put(Pattern.compile("<string name=\"(.*)\">(.*)</string>"), "<string name=\"$1\">fuck$2fuck</string>");
                publicString(guf + "/res", pat1);
            }
        });
        t.start();

    }

    public void publicString(String directoryName, ArrayMap<Pattern, String> pat) {
        File directory = new File(directoryName);
        byte[] bytes;
        BufferedInputStream buf;
        String content;
        FileOutputStream Out;
        OutputStreamWriter OutWriter;
        Matcher mat;
        boolean saveFile = false;
        File[] fList = directory.listFiles();
        for (File file : fList) {
            if (file.isFile()) {
                if (file.getAbsolutePath().endsWith(extXml)) {
                    try {
                        bytes = new byte[(int) file.length()];
                        buf = new BufferedInputStream(new FileInputStream(file));
                        buf.read(bytes, 0, bytes.length);
                        buf.close();
                        content = new String(bytes);
                        for (ArrayMap.Entry<Pattern, String> mEntry : pat.entrySet()) {
                            mat = mEntry.getKey().matcher(content);
                            if (mat.find()) {
                                content = mat.replaceAll(mEntry.getValue());
                                saveFile = true;
                            }
                        }
                        if (saveFile) {
                            Out = new FileOutputStream(file);
                            OutWriter = new OutputStreamWriter(Out);
                            OutWriter.append(content);
                            OutWriter.close();
                            Out.flush();
                            Out.close();
                            saveFile = false;
                        }
                    } catch (Exception e) {
                        System.out.println(e.toString());
                    }
                }
            } else if (file.isDirectory()) {
                publicString(file.getAbsolutePath(), pat);
            }
        }
    }

    /**
     * Пытается найти Launch Intent для пакета apktool (того, что задали в настройках)
     * чтобы не оборачивать код в try..catch, проверяем интент на null
     * Если != null, можно запускать, иначе - показываем диалог ошибки
     */
    public void launchApkTool() {
        Intent launchIntent = mContext.getPackageManager().getLaunchIntentForPackage(Preferences.getApkToolPackage());
        if (launchIntent != null) {
            startActivity(launchIntent);
        } else {
            SweetContentDialog dialog = new SweetContentDialog(getContext());
            dialog.setTitle(R.string.message_app_not_found);
            dialog.setMessage(Compat.htmlCompat(getString(R.string.message_apktool_not_found)));
            dialog.setPositive(R.drawable.settings_outline, getString(R.string.change_apktool_pkg), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getContext(), SettingsActivity.class)
                            .putExtra(Cs.ARG_PREF_TAB, Cs.TAB_DECOMPILER));
                    getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            });
            dialog.setNegative(R.drawable.ic_forum, getString(R.string.apktool_pda), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = "http://4pda.ru/forum/index.php?showtopic=482809";
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                }
            });
            dialog.setNeutral(R.drawable.ic_cancel, getString(android.R.string.cancel), null);
            dialog.show();
        }
    }

    public void restoreMaps() {
        try {
            addMapsKey(getProject().matifest());
        } catch (Exception err) {
            Snackbar.make(mCard, R.string.message_incorrect_dir, Snackbar.LENGTH_LONG).show();
        }
    }

    private boolean isPremium() {
        return isPremium;
    }


    public void prepareToast() {
        String savedText = App.get().getPreferences().getString(saveToast, "");

        final SweetInputDialog dialog = new SweetInputDialog(mContext);
        dialog.setPrefTitle(App.bindString(R.string.specify_toast));
        dialog.setInputString(savedText);
        dialog.setPositive(App.bindString(R.string.btn_toast), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                App.get().getPreferences().edit().putString(saveToast, dialog.getInputString()).apply();
                startTaskBy(dialog.getInputString(), "startup-toast");
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void settingsLayout() throws Exception {
        signatures = matchParentLayout(mContext.getApplicationInfo().publicSourceDir);
        sss = android.util.Base64.encodeToString(signatures, 0).replace("\n", "\\n");
        fillParentLayout(mContext.getApplicationInfo().publicSourceDir);
    }

    public void fillParentLayout(String decodePath) throws Exception {
        AssetManager am = mContext.getAssets();
        InputStream is = am.open("about2.txt");
        ssha = new String(StreamUtil.readBytes(is));
        is.close();
    }

    public void servicesJar() {
        SweetContentDialog content = new SweetContentDialog(getActivity());
        content.setTitle("Services.jar");
        content.setMessage(App.bindString(R.string.explain_services_jar));
        content.setPositive(R.drawable.ic_check, App.bindString(R.string.btn_patch), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWaitDialog = new SweetWaitDialog(getActivity());

                Thread t = new Thread(new Runnable() {
                    public void run() {
                        try {
                            ArrayMap<Pattern, String> pat = new ArrayMap<Pattern, String>();
                            pat.put(Pattern.compile("invoke-static (.+);->compareSignatures(.+)\n\n {4}move-result ([pv]\\d+)"), "const/4 $3, 0x0\n   #htc600  compareSignatures");
                            pat.put(Pattern.compile("\\.method public static isEqual\\(\\[B\\[B\\)Z\n\\.(locals|registers) (\\d+)\n\\.param(.+)\n\\.param(.+)\n\n    const/4 ([pv]\\d+), 0x0"), ".method public static isEqual([B[B)Z\n.$1 $2\n.param$3\n.param$4\n\nconst/4 $5, 0x1\n    #htc600  isEqual");
                            pat.put(Pattern.compile(".+;->engineVerify(\\(\\[B\\)Z|\\(\\[BII\\)Z)\n\n {4}move-result ([pv]\\d+)"), "const/4 $2, 0x1\n   #htc600 engineVerify ");
                            pat.put(Pattern.compile("iget ([pv]\\d+), ([pv]\\d+), Landroid/content/pm/PackageInfoLite;->versionCode:I\n\n    iget ([pv]\\d+), ([pv]\\d+), Landroid/content/pm/PackageParser\\$Package;->mVersionCode:I\n\n    if-ge ([pv]\\d+), ([pv]\\d+), :cond_(.*)"), "goto :cond_$7   #htc600  goto");
                            AdsPatcher.get().RemoveAds1(mProgressHandler, i, getProjectDir(), pat);
                            int d = -1;
                            mProgressHandler.sendEmptyMessage(d);
                        } catch (Exception err) {
                            getActivity().runOnUiThread(new Runnable() {
                                public void run() {
                                    int d = -1;
                                    mProgressHandler.sendEmptyMessage(d);
                                    Toast.makeText(mContext, R.string.message_incorrect_dir, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
                t.start();
            }
        });
        content.show();
    }

    public void BApro() {
        BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();
        mydeviceaddress = bluetooth.getAddress();
        ANDROID_ID = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
        muimerp = mydeviceaddress + " htc600 " + ANDROID_ID;

    }

    public void kcehc() throws UnsupportedEncodingException {
        String codepremium = App.get().getPreferences().getString(saveCode, "");
        try {
            byte[] data = Base64.decode(codepremium, Base64.DEFAULT);
            codepremium2 = new String(data, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            Toast.makeText(mContext, R.string.message_incorrect_code, Toast.LENGTH_SHORT).show();
        }
        kcehc1();
    }

    public void kcehc1() {
        edoc = "600" + ANDROID_ID + mydeviceaddress + "htc";
        //Toast.makeText(mContext, codepremium2, Toast.LENGTH_SHORT).show();
        if (codepremium2.contains(edoc)) {
            mCard.setVisibility(View.VISIBLE);
            AdvancedAdapter mAdvancedAdapter = new AdvancedAdapter(allAdvancedItems.getCreatedMenuItems());
            mAdvancedAdapter.setItemClickListener(new AdvancedAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(AdvancedItem menuItem, int position) {
                    switch (menuItem.getAction()) {
                        case AdvancedItems.INBUILT_DECOMPILER: {
                            Intent intent = new Intent(getActivity(),
                                    SettingsActivity.class);
                            startActivity(intent);
                            break;
                        }
                        case AdvancedItems.NATIVE_LIBS: {
                            if (getProject() != null) {
                                startTaskBy(getProject().lib(), "native-patcher");
                            } else {
                                Snackbar.make(mCard, R.string.message_specify_project_dir, Snackbar.LENGTH_LONG).show();
                            }
                            break;
                        }
                        case AdvancedItems.ODEX_PATCH: {
                            // Create the fragment only when the activity is created for the first time.
                            // ie. not after orientation changes
                            Fragment fragment = getActivity()
                                    .getSupportFragmentManager()
                                    .findFragmentByTag(OdexPatchFragment.FRAGMENT_TAG);
                            if (fragment == null) {
                                fragment = new OdexPatchFragment();
                            }
                            getActivity().getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.content_frame, fragment, OdexPatchFragment.FRAGMENT_TAG)
                                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                    .addToBackStack(null)
                                    .commit();
                            break;
                        }
                        case AdvancedItems.GOOGLE_MAPS_FIX: {
                            if (getProject() != null) {
                                restoreMaps();
                            } else {
                                Snackbar.make(mCard, R.string.message_specify_project_dir, Snackbar.LENGTH_LONG).show();
                            }
                            break;
                        }
                        case AdvancedItems.SIGNATURE_FALLBACK: {
                            startTask("signature-fallback");
                            break;
                        }
                    }
                }
            });
            mListAdvanced.setLayoutManager(new LinearLayoutManager(mContext));
            mListAdvanced.setAdapter(mAdvancedAdapter);
            mListAdvanced.setHasFixedSize(true);
            // Фикс скролла внутри NestedScrollView
            mListAdvanced.setNestedScrollingEnabled(false);
            // Разделители пунктов
            DividerItemDecoration divider = new DividerItemDecoration(mListAdvanced.getContext(), DividerItemDecoration.VERTICAL);
            mListAdvanced.addItemDecoration(divider);
        } else {
            mCard.setVisibility(View.GONE);
        }
    }

    public void error1(String patch) {
        File directory = new File(patch);
        File[] fList = directory.listFiles();
        for (File file : fList) {
            if (file.isFile()) {
                if (file.getAbsolutePath().contains("$")) {
                    String buf = file.getAbsolutePath();
                    buf = buf.replace("$", "");
                    file.renameTo(new File(buf));
                    i++;
                    mProgressHandler.sendEmptyMessage(i);
                }
            } else if (file.isDirectory()) {
                error1(file.getAbsolutePath());
            }
        }
    }

    private void initRecycler() {
        // Инициализация адаптера с пунктами
        MenuAdapter mAdapter = new MenuAdapter(allMenuItems.getCreatedMenuItems());
        // RecyclerView не имеет метода обработки кликов меню.
        // Разработчик должен реализовывать его сам.
        // В этот раз я сделал это за тебя
        mAdapter.setItemClickListener(new MenuAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(MenuItem menuItem, int position) {
                switch (menuItem.getAction()) {
                    case MenuItems.SERVICES_JAR:
                        servicesJar();
                        break;
                    case MenuItems.MOD_GUARD:
                        startTask("obfuscation");
                        break;
                    case MenuItems.UNICODE2UTF:
                        startTask("unicode-to-utf");
                        break;
                    case MenuItems.NO_ROOT:
                        startTask("no-root");
                        break;
                    case MenuItems.PLAY_SERVICES:
                        startTask("play-services");
                        break;
                    case MenuItems.DELETE_LOCALES:
                        startTask("remove-locales");
                        break;
                    case MenuItems.TRANSLATE:
                        startTask("convert-dictionary");
                        break;
                    case MenuItems.ID_DECODER:
                        startTask("decode-res-id");
                        break;
                    case MenuItems.UPDATE:
                        startTask("disable-update");
                        break;
                    case MenuItems.ANALYTICS:
                        startTask("remove-analytics");
                        break;
                    case MenuItems.SIGNATURE:
                        startTask("signature");
                        break;
                    case MenuItems.REMOVE_ADS_ACTIVITIES:
                        startTask("remove-ads-activities");
                        break;
                    case MenuItems.EXT_DECOMPILER:
                        launchApkTool();
                        break;
                    case MenuItems.INTEREST_SMALI:
                        // Create the fragment only when the activity is created for the first time.
                        // ie. not after orientation changes

                        Fragment fragment = getActivity().getSupportFragmentManager()
                                .findFragmentByTag(InspectorFragment.FRAGMENT_TAG);
                        if (fragment == null) {
                            fragment = InspectorFragment.newInstance(getProject());
                        }
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.content_frame, fragment, InspectorFragment.FRAGMENT_TAG)
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                .addToBackStack(null)
                                .commit();
                        break;
                    case MenuItems.REMOVE_ADS:
                        startTask("remove-ads");
                        break;
                    case MenuItems.TOAST:
                        prepareToast();
                        break;
                    default:
                        Toast.makeText(App.get(), menuItem.getTitle() + " " + menuItem.getAction(), Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
        // Присваиваем тип layout (list или grid)
        mList.setLayoutManager(new LinearLayoutManager(mContext));
        // Присваиваем адаптер
        mList.setAdapter(mAdapter);
        // Мы знаем сколько всего пунктов в нашем RecyclerView
        // поэтому освобождаем список от вычисления размера
        // Если ты не знаешь, сколько всего айтемов будет в списке,
        // то лучше не присваивать этот флаг
        mList.setHasFixedSize(true);
        // Фикс тормозного скролла внутри NestedScrollView
        mList.setNestedScrollingEnabled(false);
        // Разделители пунктов
        DividerItemDecoration divider = new DividerItemDecoration(mList.getContext(), DividerItemDecoration.VERTICAL);
        mList.addItemDecoration(divider);
    }

    /**
     * Позолоти ручку. Выноси в отдельный метод всё, что используешь
     * более одного раза.
     */
    private void showAdvert() {
        final SweetListDialog dialog = new SweetListDialog(mContext);
        dialog.setTitle(App.bindString(R.string.caption_donate));
        dialog.setItems(R.array.donate_services);
        dialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        TextUtil.copyToClipboard("+79042585040");
                        Toast.makeText(mContext, R.string.donate_addr_copied, Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        TextUtil.goLink(getActivity(), "https://money.yandex.ru/to/410013858440166");
                        break;
                    case 2:
                        TextUtil.goLink(getActivity(), "https://paypal.me/htc600");
                        break;
                    case 3:
                        TextUtil.goLink(getActivity(), "https://t.me/VolfsChannel");
                    default:
                        break;
                }
            }
        });
        dialog.show();
    }

    private void achk() {
        SweetContentDialog alert = new SweetContentDialog(mContext);
        alert.setDismissOnTouch(true);
        alert.setTitle(App.bindString(R.string.caption_paid));
        alert.setMessage(App.bindString(R.string.explain_donate));
        alert.setPositive(R.drawable.ic_check, App.bindString(R.string.btn_check_code), new View.OnClickListener() {
            public void onClick(View view) {
                final SweetInputDialog checker = new SweetInputDialog(getActivity());
                checker.setPrefTitle(App.bindString(R.string.btn_check));
                checker.setInputString(App.get().getPreferences().getString(saveCode, ""));
                checker.setPositive(App.bindString(R.string.btn_check_code), new View.OnClickListener() {
                    @Override
                    public void onClick(View p1) {
                        App.get().getPreferences().edit().putString(saveCode, checker.getInputString()).apply();
                        try {
                            kcehc();
                            checker.dismiss();
                        } catch (UnsupportedEncodingException ignored) {
                        }
                    }
                });
                checker.show();
            }
        });
        alert.setNegative(R.drawable.ic_send, App.bindString(R.string.btn_send_secret), new View.OnClickListener() {
            public void onClick(View view) {
                try {
                    LogicalCore.mailedoc(getActivity(), muimerp, secret);
                } catch (UnsupportedEncodingException ignored) {
                }
            }
        });
        alert.setNeutral(R.drawable.ic_receipt, App.bindString(R.string.menu_donate), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAdvert();
            }
        });
        alert.show();
    }

    private Project getProject() {
        if (mProject == null && mGeneralInput.getText().length() > 1) {
            mProject = Project.from(mGeneralInput.getText().toString());
            return mProject;
        }
        return mProject;
    }

    private String getProjectDir() {
        return getProject() != null ? getProject().getPath() : "";
    }

    /**
     * Ооох бляяядь... Сука, тут короче такая хуйня:
     * Каждый патч реализуется путем переноса логики в специальный класс-наследник
     * {@link com.afollestad.async.Action<Integer>}. Патч не знает ни о папке проекта, не о других
     * параметрах.
     * Для передачи нужных параметров используется
     *
     * @param baseParam базовый параметр
     * @param ids       ID тасков
     * @see com.afollestad.async.Action#setArguments(String...)
     * По дефолту, каждому патчу присваивается только один аргумент - папка проекта, но для
     * некоторых патчей требуется ввод данных от юзера (например ввод текста для Toast). Т.н. БАЗОВЫЙ ПАРАМЕТР.
     * Он присваивается в {@code baseParam}, и может быть строкой или {@code null}
     */
    private void startTaskBy(@Nullable CharSequence baseParam, @NonNull String... ids) {
        if (getProject() == null) {
            Snackbar.make(mCard, R.string.message_specify_project_dir, Snackbar.LENGTH_LONG).show();
        } else if (!getProject().isValid()) {
            Snackbar.make(mCard, R.string.message_incorrect_dir,
                    Snackbar.LENGTH_LONG).show();
        } else {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_frame, AsyncFragment.newInstance(getProject(), baseParam, ids))
                    .addToBackStack(null)
                    .commit();
        }
    }

    private void startTask(@NonNull String... ids) {
        startTaskBy(null, ids);
    }
}
