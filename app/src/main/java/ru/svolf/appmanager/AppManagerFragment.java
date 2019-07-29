package ru.svolf.appmanager;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import apk.tool.patcher.R;
import apk.tool.patcher.util.SystemsDetector;
import ru.svolf.melissa.swipeback.SwipeBackFragment;
import ru.svolf.melissa.swipeback.SwipeBackLayout;


public class AppManagerFragment extends SwipeBackFragment implements SearchView.OnQueryTextListener {
    public static final String FRAGMENT_TAG = "AppManagerFragment";
    private static final String TAG = FRAGMENT_TAG;
    // General variables
    private List<AppInfo> appList;
    private List<AppInfo> appSystemList;

    private AppAdapter appAdapter;
    private AppAdapter appSystemAdapter;

    // Configuration variables
    private View rootView;
    private Toolbar toolbar;
    private Activity activity;
    private Context context;
    private RecyclerView recyclerView;
    private MenuItem searchItem;
    private SearchView searchView;
    private static LinearLayout noResults;

    private SwipeRefreshLayout swipeRefresh;
    private ProgressBar progressWheel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setEdgeLevel(SwipeBackLayout.EdgeLevel.MED);
        this.activity = getActivity();
        this.context = getContext();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_app_manage, container, false);
        return attachToSwipeBack(rootView);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setInitialConfiguration();
        checkAndAddPermissions(activity);
        setAppDir();

        recyclerView = view.findViewById(R.id.appList);
        swipeRefresh = view.findViewById(R.id.pull_to_refresh);
        progressWheel = view.findViewById(R.id.progress);
        noResults = view.findViewById(R.id.noResults);

        swipeRefresh.setEnabled(false);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        progressWheel.setVisibility(View.VISIBLE);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        new getInstalledApps().execute();
    }

    private void setInitialConfiguration() {
        toolbar = rootView.findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.app_search);
        onCreateOptionsMenu(toolbar.getMenu());

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                return false;
            }
        });
    }

    class getInstalledApps extends AsyncTask<Void, String, Void> {
        private Integer totalApps;
        private Integer actualApps;

        getInstalledApps() {
            actualApps = 0;

            appList = new ArrayList<>();
            appSystemList = new ArrayList<>();
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.d(TAG, "doInBackground() called with: params = [" + Arrays.toString(params) + "]");
            long pkgCall = System.currentTimeMillis();
            final PackageManager packageManager = getContext().getPackageManager();
            long pkgCallEnd = System.currentTimeMillis();

            long pkgGet = System.currentTimeMillis();
            List<PackageInfo> packages = packageManager.getInstalledPackages(PackageManager.GET_META_DATA);
            long pkgGetEnd = System.currentTimeMillis();
            totalApps = packages.size();
            // Get Sort Mode
            switch ("1") {
                default:
                    // Comparator by Name (default)
                    Collections.sort(packages, new Comparator<PackageInfo>() {
                        @Override
                        public int compare(PackageInfo p1, PackageInfo p2) {
                            return packageManager.getApplicationLabel(p1.applicationInfo).toString().toLowerCase().compareTo(packageManager.getApplicationLabel(p2.applicationInfo).toString().toLowerCase());
                        }
                    });
                    break;
                case "2":
                    // Comparator by Size
                    Collections.sort(packages, new Comparator<PackageInfo>() {
                        @Override
                        public int compare(PackageInfo p1, PackageInfo p2) {
                            Long size1 = new File(p1.applicationInfo.sourceDir).length();
                            Long size2 = new File(p2.applicationInfo.sourceDir).length();
                            return size2.compareTo(size1);
                        }
                    });
                    break;
                case "3":
                    // Comparator by Installation Date (default)
                    Collections.sort(packages, new Comparator<PackageInfo>() {
                        @Override
                        public int compare(PackageInfo p1, PackageInfo p2) {
                            return Long.toString(p2.firstInstallTime).compareTo(Long.toString(p1.firstInstallTime));
                        }
                    });
                    break;
                case "4":
                    // Comparator by Last Update
                    Collections.sort(packages, new Comparator<PackageInfo>() {
                        @Override
                        public int compare(PackageInfo p1, PackageInfo p2) {
                            return Long.toString(p2.lastUpdateTime).compareTo(Long.toString(p1.lastUpdateTime));
                        }
                    });
                    break;
            }

            // Installed & System Apps
            for (PackageInfo packageInfo : packages) {
                if (!(packageManager.getApplicationLabel(packageInfo.applicationInfo).equals("") || packageInfo.packageName.equals(""))) {

                    if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                        try {
                            // Non System Apps
                            AppInfo tempApp = new AppInfo(packageInfo.packageName, false);
                            appList.add(tempApp);
                        } catch (OutOfMemoryError e) {
                            //TODO Workaround to avoid FC on some devices (OutOfMemoryError). Drawable should be cached before.
                            AppInfo tempApp = new AppInfo(packageInfo.packageName, false);
                            appList.add(tempApp);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            // System Apps
                            AppInfo tempApp = new AppInfo(packageInfo.packageName, true);
                            appSystemList.add(tempApp);
                        } catch (OutOfMemoryError e) {
                            //TODO Workaround to avoid FC on some devices (OutOfMemoryError). Drawable should be cached before.
                            AppInfo tempApp = new AppInfo(packageInfo.packageName, true);
                            appSystemList.add(tempApp);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                actualApps++;
                publishProgress(Integer.toString((actualApps * 100) / totalApps));
            }
            long pkgStop = System.currentTimeMillis();
            Log.e("BLYA", "doInBackground ended in " + (pkgStop - pkgCall) + "ms, PackageManager call = "
                    + (pkgCallEnd - pkgCall) + "ms, getInstalledPackages call = " + (pkgGetEnd - pkgGet) + "ms");
            return null;
        }

        @Override
        protected void onProgressUpdate(String... progress) {
            super.onProgressUpdate(progress);
            //progressWheel.setProgress(Integer.parseInt(progress[0]));
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            appAdapter = new AppAdapter(appList, context);
            appSystemAdapter = new AppAdapter(appSystemList, context);

            recyclerView.setAdapter(appAdapter);
            swipeRefresh.setEnabled(true);
            progressWheel.setVisibility(View.GONE);
            searchItem.setVisible(true);

            setSwipeRefresh(swipeRefresh);
        }

    }

        private void setSwipeRefresh(final SwipeRefreshLayout swipeRefresh) {
            swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    appAdapter.clear();
                    appSystemAdapter.clear();
                    recyclerView.setAdapter(null);
                    new getInstalledApps().execute();

                    swipeRefresh.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            swipeRefresh.setRefreshing(false);
                        }
                    }, 2000);
                }
            });
        }

        private void checkAndAddPermissions(Activity activity) {
            UtilsApp.checkPermissions(activity);
        }

        private void setAppDir() {
            File appDir = UtilsApp.getAppFolder();
            if (!appDir.exists()) {
                appDir.mkdir();
            }
        }

        @Override
        public boolean onQueryTextChange(String search) {
            if (search.isEmpty()) {
                ((AppAdapter) recyclerView.getAdapter()).getFilter().filter("");
            } else {
                ((AppAdapter) recyclerView.getAdapter()).getFilter().filter(search.toLowerCase());
            }

            return false;
        }

        public static void setResultsMessage(Boolean result) {
            if (result) {
                noResults.setVisibility(View.VISIBLE);
            } else {
                noResults.setVisibility(View.GONE);
            }
        }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    public void onCreateOptionsMenu(Menu menu) {

        searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);

        SearchManager searchManager = (SearchManager) getContext().getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
    }
}

