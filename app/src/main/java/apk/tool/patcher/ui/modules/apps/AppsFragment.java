package apk.tool.patcher.ui.modules.apps;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.afinal.simplecache.ACache;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import apk.tool.patcher.App;
import apk.tool.patcher.R;
import apk.tool.patcher.entity.ParallelTask;
import apk.tool.patcher.ui.modules.base.DataFragment;
import apk.tool.patcher.ui.modules.base.adapters.ViewPagerAdapter;
import apk.tool.patcher.ui.widget.BigTabsLayout;
import ru.svolf.melissa.model.AppItem;
import ru.svolf.melissa.swipeback.SwipeBackLayout;

public class AppsFragment extends DataFragment {
    public static final String FRAGMENT_TAG = "apps_parent_fragment";
    private static final String TAG = "AppsFragment";

    private Context mContext;
    private ViewPager mPager;
    private ACache mCache;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        mCache = ACache.get(mContext);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        baseInflateFragment(inflater, R.layout.activity_smali_inspector);
        return attachToSwipeBack(view);
    }

    @SuppressLint("HandlerLeak")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        BigTabsLayout tabLayout = findViewById(R.id.tab_layout);
        mPager = findViewById(R.id.tab_pager);
        tabLayout.setupWithPager(mPager);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ApplicationLoader loader = new ApplicationLoader();
        loader.execute();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        view = null;
    }

    /**
     * Наполнение пагера данными
     *
     * @param pager вью пагер
     * @param user  список с приложениями пользователя
     * @param sys   список с системными приложениями
     */
    private void setViewPager(ViewPager pager, ArrayList<AppItem> user, ArrayList<AppItem> sys) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(UserAppsFragment.newInstance(user), String.format(Locale.ENGLISH, getString(R.string.tab_apps_user), user.size()));
        adapter.addFragment(SystemAppsFragment.newInstance(sys), String.format(Locale.ENGLISH, getString(R.string.tab_apps_system), sys.size()));

        pager.setAdapter(adapter);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
                if (i == 0){
                    setEdgeLevel(SwipeBackLayout.EdgeLevel.MED);
                } else {
                    setEdgeLevel(App.dpToPx(1));
                }
            }

            @Override
            public void onPageSelected(int i) {

            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });
    }

    /**
     * Получение списка установленных приложений
     *
     * @param task выполняемый асинк-таск
     * @return лист с данными (объекты AppItem)
     */
    private ArrayList<AppItem> getInstalledApps(ApplicationLoader task) throws PackageManager.NameNotFoundException {
        ArrayList<AppItem> res = new ArrayList<>();
        Intent mainIntent = new Intent(Intent.ACTION_MAIN);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> pkgAppsList = AppsFragment.this.getContext().getPackageManager().queryIntentActivities(mainIntent, PackageManager.GET_RESOLVED_FILTER);

        int totalPackages = pkgAppsList.size();

        for (int i = 0; i < totalPackages; i++) {
            ResolveInfo resolved = pkgAppsList.get(i);

            int count = i + 1;

            final AppItem newInfo = new AppItem();
            newInfo.setPackageLabel(resolved.loadLabel(mContext.getPackageManager()).toString());

            task.doProgress(String.format(Locale.ENGLISH, "%d / %d", count, totalPackages));
            newInfo.setSystem(isSystemPackage(resolved));
            newInfo.setPackageName(resolved.resolvePackageName);
            newInfo.setPackageVersion(fromResolve(resolved).versionName);

            newInfo.setPackageFilePath(fromResolve(resolved).applicationInfo.publicSourceDir);
            newInfo.setPackageIcon(fromResolve(resolved).applicationInfo.loadIcon(mContext.getPackageManager()));
            res.add(newInfo);
        }
        Comparator<AppItem> AppNameComparator = new Comparator<AppItem>() {
            public int compare(AppItem o1, AppItem o2) {
                return o1.getPackageLabel().toLowerCase().compareTo(o2.getPackageLabel().toLowerCase());
            }
        };
        Collections.sort(res, AppNameComparator);
        return res;
    }

    /**
     * Проверка на принадлежность приложения к списку системных
     *
     * @param pkgInfo системная информация о приожении
     * @return true, сли приложение является системным
     */
    private boolean isSystemPackage(ResolveInfo pkgInfo) throws PackageManager.NameNotFoundException {
        return (fromResolve(pkgInfo).applicationInfo.flags  & ApplicationInfo.FLAG_SYSTEM) != 0;
    }

    public PackageInfo fromResolve(ResolveInfo resolveInfo) throws PackageManager.NameNotFoundException {
        return getContext().getPackageManager().getPackageInfo(resolveInfo.resolvePackageName, 0);
    }

    /**
     * Получение списка установленных приложений (основной метод)
     */
    private class ApplicationLoader extends ParallelTask<String, String, ArrayList<AppItem>> {

        @Override
        protected ArrayList<AppItem> doInBackground(String... params) {
            publishProgress("Retrieving installed application");
            try {
                return getInstalledApps(this);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<AppItem> AllPackages) {
            ArrayList<AppItem> user = new ArrayList<>();
            ArrayList<AppItem> sys = new ArrayList<>();

            for (AppItem item : AllPackages) {
                if (!item.isSystem()) {
                    user.add(item);
                } else {
                    sys.add(item);
                }
            }

            mCache.put("apps", AllPackages);

            AllPackages.clear();

            setViewPager(mPager, user, sys);
            setRefreshing(false);
        }

        void doProgress(String value) {
            publishProgress(value);
        }

        @Override
        protected void onPreExecute() {
            setRefreshing(true);
        }

        @Override
        protected void onProgressUpdate(String... text) {
            Log.d(TAG, "onProgressUpdate() called with: text = [" + Arrays.toString(text) + "]");
        }
    }
}