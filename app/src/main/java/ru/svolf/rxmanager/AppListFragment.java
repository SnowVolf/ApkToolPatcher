package ru.svolf.rxmanager;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.ListFragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.IOException;
import java.util.List;

import apk.tool.patcher.R;
import apk.tool.patcher.filesystem.FilePickHelper;
import apk.tool.patcher.util.Preferences;
import ru.svolf.melissa.fragment.dialog.SweetWaitDialog;
import ru.svolf.rxmanager.adapter.AppListAdapter;
import ru.svolf.rxmanager.async.AppListLoader;
import ru.svolf.rxmanager.data.AppListData;
import ru.svolf.rxmanager.data.AppSource;
import ru.svolf.rxmanager.util.ApkFilePicker;
import ru.svolf.rxmanager.util.RealPathUtils;

import static android.app.Activity.RESULT_OK;

/**
 * List of all applications
 */
public class AppListFragment extends ListFragment implements SearchView.OnQueryTextListener, SearchView.OnCloseListener, View.OnClickListener,
        LoaderManager.LoaderCallbacks<List<AppListData>> {
    public static final String FRAGMENT_TAG = "AppListFragment";
    private static final String TAG = "AppListFragment";
    private static final int REQUEST_STORAGE_PERMISSIONS = 13245;

    private AppListAdapter listAdapter;
    private SearchView searchView;
    private Button buttonFilter;

    private boolean isListShown;
    private View listView;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView() called with: inflater = [" + inflater + "], container = [" + container + "], savedInstanceState = [" + savedInstanceState + "]");
        View view = inflater.inflate(R.layout.fragment_app_list, container, false);
        listView = view.findViewById(R.id.list_view_list);
        progressBar = view.findViewById(R.id.list_view_progress_bar);
        searchView = view.findViewById(R.id.search_find);
        buttonFilter = view.findViewById(R.id.button_addition);
        view.findViewById(R.id.btn_analyze_not_installed).setOnClickListener(this);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated() called with: view = [" + view + "], savedInstanceState = [" + savedInstanceState + "]");
        super.onViewCreated(view, savedInstanceState);
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (searchView.isIconified()) {
                    searchView.setIconified(false);
                } else {
                    Log.e(TAG, "onClick: already expanded");
                }
            }
        });
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint(getString(R.string.search_q));

        final PopupMenu popup = new PopupMenu(getContext(), buttonFilter);
        popup.inflate(R.menu.main);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return onOptionsItemSelected(item);
            }
        });
        //popup.getMenu().getItem(Preferences.getFilterId()).setChecked(true);
        buttonFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popup.show();
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated() called with: savedInstanceState = [" + savedInstanceState + "]");
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);

        if (savedInstanceState == null) {
            listAdapter = new AppListAdapter(getActivity());
            setListAdapter(listAdapter);
            setListShown(false);
            LoaderManager.getInstance(this).initLoader(AppListLoader.ID, null, this);
        }
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String currentFilter = !TextUtils.isEmpty(newText) ? newText : null;
        listAdapter.filterOnAppName(currentFilter);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Log.d(TAG, "onQueryTextSubmit() called with: query = [" + query + "]");
        return true;
    }

    @Override
    public boolean onClose() {
        Log.d(TAG, "onClose() called");
        if (!TextUtils.isEmpty(searchView.getQuery())) {
            searchView.setQuery(null, true);
        }
        return true;
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        Log.d(TAG, "onListItemClick() called with: listView = [" + listView + "], view = [" + view + "], position = [" + position + "], id = [" + id + "]");
        AppListData appBasicData = (AppListData) view.getTag();
        AppsDetailsFragment detailsFragment = AppsDetailsFragment.newInstance(appBasicData.getPackageName(), null);

        getFragmentManager()
                .beginTransaction()
                .add(R.id.content_frame, detailsFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public Loader<List<AppListData>> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "onCreateLoader() called with: id = [" + id + "], args = [" + args + "]");
        return new AppListLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<List<AppListData>> loader, List<AppListData> data) {
        Log.d(TAG, "onLoadFinished() called with: loader = [" + loader + "], data = [" + data + "]");
        listAdapter.clear();
        listAdapter.addAll(data);

        if (isResumed()) {
            setListShown(true);
        } else {
            setListShownNoAnimation(true);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<AppListData>> loader) {
        Log.d(TAG, "onLoaderReset() called with: loader = [" + loader + "]");
        listAdapter.clear();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected() called with: item = [" + item + "]");
        switch (item.getItemId()) {
            case R.id.menu_show_all_apps:
                Preferences.setFilterId(0);
                listAdapter.filterOnAppSource(null);
                break;
            case R.id.menu_show_google_play_apps:
                Preferences.setFilterId(1);
                listAdapter.filterOnAppSource(AppSource.GOOGLE_PLAY);
                break;
            case R.id.menu_show_amazon_store_apps:
                Preferences.setFilterId(2);
                listAdapter.filterOnAppSource(AppSource.AMAZON_STORE);
                break;
            case R.id.menu_show_system_pre_installed_apps:
                Preferences.setFilterId(3);
                listAdapter.filterOnAppSource(AppSource.SYSTEM_PREINSTALED);
                break;
            case R.id.menu_show_unknown_source_apps:
                Preferences.setFilterId(4);
                listAdapter.filterOnAppSource(AppSource.UNKNOWN);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick() called with: v = [" + v + "]");
        switch (v.getId()) {
            case R.id.btn_analyze_not_installed:
                startFilePicker(true);
        }
    }

    /**
     * Currently it is called only after APK file is selected from storage
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        Log.d(TAG, "onActivityResult() called with: requestCode = [" + requestCode + "], resultCode = [" + resultCode + "], data = [" + data + "]");
        super.onActivityResult(requestCode, resultCode, data);
        // handle picked apk file uri and do work
        if (requestCode == ApkFilePicker.REQUEST_PICK_APK && resultCode == RESULT_OK) {
            new ExtractionTask().execute(data);
        }
    }

    /**
     * This is called only when storage permission is granted as we use permission granting only when
     * reading APK file
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult() called with: requestCode = [" + requestCode + "], permissions = [" + permissions + "], grantResults = [" + grantResults + "]");
        if (requestCode == REQUEST_STORAGE_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startFilePicker(false);
            } else {
                Snackbar.make(getActivity().findViewById(android.R.id.content), "Permission denied", Snackbar.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        listAdapter.notifyDataSetChanged();
    }

    /**
     * Start file picker activity in order to select APK file from storage.
     *
     * @param withPermissionCheck if true, permissions are requested
     */
    private void startFilePicker(boolean withPermissionCheck) {
        Log.d(TAG, "startFilePicker() called with: withPermissionCheck = [" + withPermissionCheck + "]");
        if (withPermissionCheck) {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSIONS);
            } else {
                startFilePicker(false);
            }
        } else {
            try {
                startActivityForResult(FilePickHelper.pickFile(true), ApkFilePicker.REQUEST_PICK_APK);
            } catch (ActivityNotFoundException exception){
                Snackbar.make(getActivity().findViewById(android.R.id.content), R.string.no_app_to_open, Snackbar.LENGTH_LONG).show();
            }
        }
    }

    public void setListShown(boolean shown) {
        Log.d(TAG, "setListShown() called with: shown = [" + shown + "]");
        setListShown(shown, true);
    }

    public void setListShownNoAnimation(boolean shown) {
        Log.d(TAG, "setListShownNoAnimation() called with: shown = [" + shown + "]");
        setListShown(shown, false);
    }

    public void setListShown(boolean shown, boolean animate) {
        Log.d(TAG, "setListShown() called with: shown = [" + shown + "], animate = [" + animate + "]");
        if (isListShown == shown) {
            return;
        }
        isListShown = shown;
        if (shown) {
            if (animate) {
                progressBar.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out));
                listView.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in));
            }
            progressBar.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        } else {
            if (animate) {
                progressBar.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in));
                listView.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out));
            }
            progressBar.setVisibility(View.VISIBLE);
            listView.setVisibility(View.INVISIBLE);
        }
    }

    class ExtractionTask extends AsyncTask<Intent, File, File> {
        private SweetWaitDialog waitDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            waitDialog = new SweetWaitDialog(getContext());
            waitDialog.setTitle(R.string.message_wait);
            waitDialog.show();
        }

        @Override
        protected File doInBackground(final Intent... intents) {
            final File archive = new File(getContext().getExternalCacheDir(), "app.apk");
            if (!archive.exists()){
                try {
                    archive.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            RealPathUtils.streamToFile(FilePickHelper.onActivityResult(getContext(), intents[0])
                            .get(0).getFileStream(), archive);
            return archive;
        }

        @Override
        protected void onPostExecute(File file) {
            super.onPostExecute(file);
            AppsDetailsFragment detailsFragment = AppsDetailsFragment.newInstance(null, file.getPath());
            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.content_frame, detailsFragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .addToBackStack(null)
                    .commit();
            if (waitDialog.isShowing()){
                waitDialog.dismiss();
            }
        }
    }
}