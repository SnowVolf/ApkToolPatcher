package apk.tool.patcher.ui.modules.apps;


import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

import apk.tool.patcher.App;
import apk.tool.patcher.R;
import apk.tool.patcher.filesystem.FastFs;
import apk.tool.patcher.util.Cs;
import apk.tool.patcher.util.TextUtil;
import ru.svolf.melissa.swipeback.SwipeBackFragment;

public class AppsDetailsFragment extends SwipeBackFragment {
    public static final String FRAGMENT_TAG = "apps-details-fragment";
    private String mPackageId;

    // Header
    private RelativeLayout appBackground;
    private ImageView appIcon;
    private TextView appLabel;

    // Lists container
    private LinearLayout listsContainer;

    // Recyclers
    private RecyclerView listCommon;
    private RecyclerView listPermissions;
    private RecyclerView listActivities;
    private RecyclerView listServices;
    private RecyclerView listReceivers;
    private RecyclerView listProviders;

    // Buttons
    private ImageView buttonLaunch;
    private ImageView buttonExport;
    private ImageView buttonGPlay;

    // Adapters
    private ArrayList<AppInfoItem> commonItems = new ArrayList<>();
    private ArrayList<AppInfoItem> permissionsItems = new ArrayList<>();
    private ArrayList<AppInfoItem> activityItems = new ArrayList<>();
    private ArrayList<AppInfoItem> servicesItems = new ArrayList<>();
    private ArrayList<AppInfoItem> receiversItems = new ArrayList<>();
    private ArrayList<AppInfoItem> providersItems = new ArrayList<>();


    public static AppsDetailsFragment newInstance(String packageName) {
        AppsDetailsFragment fragment = new AppsDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Cs.ARG_APP_INFO, packageName);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return attachToSwipeBack(inflater.inflate(R.layout.fragment_app_info, container, false));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        appBackground = view.findViewById(R.id.app_info_header);
        appIcon = appBackground.findViewById(R.id.app_icon);
        appLabel = appBackground.findViewById(R.id.app_name);

        buttonLaunch = appBackground.findViewById(R.id.button_launch);
        buttonExport = appBackground.findViewById(R.id.button_export);
        buttonGPlay = appBackground.findViewById(R.id.button_play);

        listsContainer = view.findViewById(R.id.lists_container);

        listCommon = listsContainer.findViewById(R.id.list_common);
        listPermissions = listsContainer.findViewById(R.id.list_permissions);
        listActivities = listsContainer.findViewById(R.id.list_activities);
        listServices = listsContainer.findViewById(R.id.list_services);
        listReceivers = listsContainer.findViewById(R.id.list_receivers);
        listProviders = listsContainer.findViewById(R.id.list_providers);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getArguments() != null) {
            mPackageId = getArguments().getString(Cs.ARG_APP_INFO);
        }
        if (mPackageId != null) {
            try {
                prepare();
                getPackageIdInfo(mPackageId);
                complete();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void prepare() {
        buttonLaunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = App.get().getPackageManager().getLaunchIntentForPackage(mPackageId);
                if (intent != null) {
                    startActivity(intent);
                } else {
                    Toast.makeText(getContext(), "Cannot launch", Toast.LENGTH_LONG).show();
                }
            }
        });

        buttonExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final PackageManager manager = getContext().getPackageManager();
                try {
                    final PackageInfo appInfo = manager.getPackageInfo(mPackageId, PackageManager.GET_META_DATA);
                    final File test = new File(appInfo.applicationInfo.sourceDir);
                    if (test.exists()) {
                        final File doc = new File(Environment.DIRECTORY_DOCUMENTS);
                        if (!doc.exists()) {
                            doc.mkdirs();
                        }
                        final File apk = new File(String.format("%s/%s [%s].apk", doc,
                                appInfo.applicationInfo.loadLabel(manager), appInfo.versionName));
                        FastFs.copyFile(AppsDetailsFragment.this, test, apk);
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

        buttonGPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextUtil.goLink(getActivity(), "https://play.google.com/store/apps/details?id=" + mPackageId);
            }
        });

        listCommon.setLayoutManager(new GridLayoutManager(getContext(), 2));
        listActivities.setLayoutManager(new LinearLayoutManager(getContext()));
        listPermissions.setLayoutManager(new LinearLayoutManager(getContext()));
        listServices.setLayoutManager(new LinearLayoutManager(getContext()));
        listReceivers.setLayoutManager(new LinearLayoutManager(getContext()));
        listProviders.setLayoutManager(new LinearLayoutManager(getContext()));

        listCommon.setNestedScrollingEnabled(false);
        listActivities.setNestedScrollingEnabled(false);
        listPermissions.setNestedScrollingEnabled(false);
        listServices.setNestedScrollingEnabled(false);
        listReceivers.setNestedScrollingEnabled(false);
        listProviders.setNestedScrollingEnabled(false);
    }

    private void getPackageIdInfo(String packageId) throws PackageManager.NameNotFoundException {
        final PackageManager manager = getContext().getPackageManager();

        final PackageInfo appInfo = manager.getPackageInfo(packageId, PackageManager.GET_META_DATA);
        getMetaInfo(appInfo);

        final PackageInfo permissionsInfo = manager.getPackageInfo(packageId, PackageManager.GET_PERMISSIONS);
        getPermissions(permissionsInfo);

        final PackageInfo activitiesInfo = manager.getPackageInfo(packageId, PackageManager.GET_ACTIVITIES);
        getActivities(activitiesInfo);

        final PackageInfo servicesInfo = manager.getPackageInfo(packageId, PackageManager.GET_SERVICES);
        getServices(servicesInfo);

        final PackageInfo receiversInfo = manager.getPackageInfo(packageId, PackageManager.GET_RECEIVERS);
        getReceivers(receiversInfo);

        final PackageInfo providersInfo = manager.getPackageInfo(packageId, PackageManager.GET_PROVIDERS);
        getProviders(providersInfo);
    }

    private void getMetaInfo(PackageInfo info) {
        appLabel.setText(info.applicationInfo.loadLabel(getContext().getPackageManager()));
        appIcon.setImageDrawable(info.applicationInfo.loadIcon(getContext().getPackageManager()));

        commonItems.add(new AppInfoItem(getString(R.string.appinfo_pkg_name)));
        commonItems.add(new AppInfoItem(info.packageName));

        commonItems.add(new AppInfoItem(getString(R.string.appinfo_ver)));
        commonItems.add(new AppInfoItem(info.versionName));

        commonItems.add(new AppInfoItem(getString(R.string.appinfo_ver_code)));
        commonItems.add(new AppInfoItem(Integer.toString(info.versionCode)));

        commonItems.add(new AppInfoItem("UID"));
        commonItems.add(new AppInfoItem(Integer.toString(info.applicationInfo.uid)));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            commonItems.add(new AppInfoItem(getString(R.string.appinfo_msdk)));
            commonItems.add(new AppInfoItem(Integer.toString(info.applicationInfo.minSdkVersion)));
        }

        commonItems.add(new AppInfoItem(getString(R.string.appinfo_tsdk)));
        commonItems.add(new AppInfoItem(Integer.toString(info.applicationInfo.targetSdkVersion)));

        commonItems.add(new AppInfoItem(getString(R.string.appinfo_apk_dir)));
        commonItems.add(new AppInfoItem(info.applicationInfo.sourceDir));

        commonItems.add(new AppInfoItem(getString(R.string.appinfo_pkg_data)));
        commonItems.add(new AppInfoItem(info.applicationInfo.dataDir));

    }

    private void getActivities(PackageInfo info) {
        if (info.activities != null) {
            for (ActivityInfo activityInfo : info.activities) {
                activityItems.add(new AppInfoItem(activityInfo.name));
            }
        } else {
            activityItems.add(new AppInfoItem(getString(R.string.no_data)));
        }
    }

    private void getPermissions(PackageInfo info) {
        if (info.requestedPermissions == null && info.permissions == null) {
            permissionsItems.add(new AppInfoItem(getString(R.string.no_data)));
        } else {
            // Получение списка системных разрешений (те, которые в манифесте объявлены)
            if (info.requestedPermissions != null) {
                for (String perm : info.requestedPermissions) {
                    permissionsItems.add(new AppInfoItem(perm));
                }
            }
            // Получение списка разрешений (всякие C2D_MESSAGE для гугловских пушей)
            if (info.permissions != null) {
                for (PermissionInfo permissionInfo : info.permissions) {
                    permissionsItems.add(new AppInfoItem(permissionInfo.name));
                }
            }
        }
    }

    private void getServices(PackageInfo info) {
        if (info.services != null) {
            for (ServiceInfo serviceInfo : info.services) {
                servicesItems.add(new AppInfoItem(serviceInfo.name));
            }
        } else {
            servicesItems.add(new AppInfoItem(getString(R.string.no_data)));
        }
    }

    private void getReceivers(PackageInfo info) {
        if (info.receivers != null) {
            for (ActivityInfo activityInfo : info.receivers) {
                receiversItems.add(new AppInfoItem(activityInfo.name));
            }
        } else {
            receiversItems.add(new AppInfoItem(getString(R.string.no_data)));
        }
    }

    private void getProviders(PackageInfo info) {
        if (info.providers != null) {
            for (ProviderInfo providerInfo : info.providers) {
                providersItems.add(new AppInfoItem(providerInfo.name));
            }
        } else {
            providersItems.add(new AppInfoItem(getString(R.string.no_data)));
        }
    }

    private void complete() {
        listCommon.setAdapter(new ExtendedAppAdapter(commonItems));
        listPermissions.setAdapter(new SimpleAppAdapter(permissionsItems));
        listActivities.setAdapter(new SimpleAppAdapter(activityItems));
        listServices.setAdapter(new SimpleAppAdapter(servicesItems));
        listReceivers.setAdapter(new SimpleAppAdapter(receiversItems));
        listProviders.setAdapter(new SimpleAppAdapter(providersItems));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        commonItems = null;
        permissionsItems = null;
        activityItems = null;
        servicesItems = null;
        receiversItems = null;
    }
}