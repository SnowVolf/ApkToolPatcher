package ru.svolf.rxmanager;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.ListFragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import apk.tool.patcher.R;
import ru.svolf.rxmanager.adapter.AppListAdapter;
import ru.svolf.rxmanager.async.AppListLoader;
import ru.svolf.rxmanager.data.AppListData;
import ru.svolf.rxmanager.data.AppSource;
import ru.svolf.rxmanager.util.ApkFilePicker;

import static android.app.Activity.RESULT_OK;

/**
 * List of all applications
 */
public class AppListFragment extends ListFragment implements SearchView.OnQueryTextListener, SearchView.OnCloseListener, View.OnClickListener,
        LoaderManager.LoaderCallbacks<List<AppListData>> {
    public static final String FRAGMENT_TAG = "AppListFragment";
    private static final int REQUEST_STORAGE_PERMISSIONS = 13245;

    private AppListAdapter listAdapter;
    private ConstraintLayout toolbar;
    private SearchView searchView;

    private boolean isListShown;
    private View listView;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_app_list, container, false);
        listView = view.findViewById(R.id.list_view_list);
        progressBar = view.findViewById(R.id.list_view_progress_bar);
        toolbar = view.findViewById(R.id.toolbar);
        searchView = view.findViewById(R.id.search_find);
        view.findViewById(R.id.btn_analyze_not_installed).setOnClickListener(this);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchView.performClick();
            }
        });
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint(getString(R.string.search_q));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
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
        return true;
    }

    @Override
    public boolean onClose() {
        if (!TextUtils.isEmpty(searchView.getQuery())) {
            searchView.setQuery(null, true);
        }
        return true;
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        AppListData appBasicData = AppListData.class.cast(view.getTag());
        AppsDetailsFragment detailsFragment = AppsDetailsFragment.newInstance(appBasicData.getPackageName());
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, detailsFragment)
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }

    @Override
    public Loader<List<AppListData>> onCreateLoader(int id, Bundle args) {
        return new AppListLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<List<AppListData>> loader, List<AppListData> data) {
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
        listAdapter.clear();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_analyze_not_installed:
                startFilePicker(true);
                break;
            case R.id.menu_show_all_apps:
                item.setChecked(true);
                listAdapter.filterOnAppSource(null);
                break;
            case R.id.menu_show_google_play_apps:
                item.setChecked(true);
                listAdapter.filterOnAppSource(AppSource.GOOGLE_PLAY);
                break;
            case R.id.menu_show_amazon_store_apps:
                item.setChecked(true);
                listAdapter.filterOnAppSource(AppSource.AMAZON_STORE);
                break;
            case R.id.menu_show_system_pre_installed_apps:
                item.setChecked(true);
                listAdapter.filterOnAppSource(AppSource.SYSTEM_PREINSTALED);
                break;
            case R.id.menu_show_unknown_source_apps:
                item.setChecked(true);
                listAdapter.filterOnAppSource(AppSource.UNKNOWN);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_analyze_not_installed:
                startFilePicker(true);
        }
    }

    /**
     * Currently it is called only after APK file is selected from storage
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // handle picked apk file and
        if (requestCode == ApkFilePicker.REQUEST_PICK_APK && resultCode == RESULT_OK) {
            Snackbar.make(listView, "Не забудь накодить, тварь!", Snackbar.LENGTH_LONG).show();
//            AnalyzeFragment parentFragment = (AnalyzeFragment) getParentFragment();
//            parentFragment.itemClicked(null, ApkFilePicker.getPathFromIntentData(data, getContext()));
        }
    }

    /**
     * This is called only when storage permission is granted as we use permission granting only when
     * reading APK file
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (requestCode == REQUEST_STORAGE_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startFilePicker(false);
            } else {
                Snackbar.make(getActivity().findViewById(android.R.id.content), "Permission denied", Snackbar.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Start file picker activity in order to select APK file from storage.
     *
     * @param withPermissionCheck if true, permissions are requested
     */
    private void startFilePicker(boolean withPermissionCheck) {
        if (withPermissionCheck) {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSIONS);
            } else {
                startFilePicker(false);
            }
        } else {
            try {
                startActivityForResult(ApkFilePicker.getFilePickerIntent(), ApkFilePicker.REQUEST_PICK_APK);
            } catch (ActivityNotFoundException exception){
                Snackbar.make(getActivity().findViewById(android.R.id.content), R.string.no_app_to_open, Snackbar.LENGTH_LONG).show();
            }
        }
    }

    public void setListShown(boolean shown) {
        setListShown(shown, true);
    }

    public void setListShownNoAnimation(boolean shown) {
        setListShown(shown, false);
    }

    public void setListShown(boolean shown, boolean animate) {
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
}