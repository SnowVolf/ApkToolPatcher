package apk.tool.patcher.ui.modules.settings;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import apk.tool.patcher.R;
import apk.tool.patcher.ui.modules.about.AboutFragment;
import apk.tool.patcher.ui.modules.base.adapters.ViewPagerAdapter;
import apk.tool.patcher.ui.modules.misc.OnTabSwipeListener;
import apk.tool.patcher.ui.widget.BigTabsLayout;
import apk.tool.patcher.util.Cs;

public class SettingsHostFragment extends Fragment implements BigTabsLayout.OnCurrentTabClickedListener {
    public static final String FRAGMENT_TAG = "settings_parent_fragment";
    private OnTabSwipeListener tabSwipeListener;
    private int mTabIndex;

    private View rootView;
    private BigTabsLayout mTabs;



    public static SettingsHostFragment newInstance(int startupTab) {
        SettingsHostFragment fragment = new SettingsHostFragment();
        Bundle args = new Bundle();
        args.putInt(Cs.ARG_PREF_TAB, startupTab);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        tabSwipeListener = (OnTabSwipeListener) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (getArguments() != null) {
            mTabIndex = getArguments().getInt(Cs.ARG_PREF_TAB);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_patcher_settings, container, false);
        return rootView;
    }

    @SuppressLint("HandlerLeak")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        long start = System.currentTimeMillis();
        mTabs = view.findViewById(R.id.tab_layout);
        ViewPager mPager = view.findViewById(R.id.tab_pager);
        setViewPager(mPager);

        mTabs.setTabClickedListener(this).setupWithPager(mPager);
        long end = System.currentTimeMillis();
        // Скроллим нужный таб  в зависимости от интента
        Handler wait = new Handler();
        wait.postDelayed(new Runnable() {
            @Override
            public void run() {
                mTabs.setupViews(mTabIndex);
            }
        }, end - start + 100);
    }

    @Override
    public void onDestroyView() {
        rootView = null;
        super.onDestroyView();
    }

    private void setViewPager(ViewPager pager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new SettingsFragment(), getString(R.string.settings_tab_main));
        //adapter.addFragment(new DecompilerSettingsFragment(), getString(R.string.settings_tab_decompiler));
        adapter.addFragment(new AboutFragment(), getString(R.string.settings_tab_other));

        pager.setAdapter(adapter);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
                tabSwipeListener.onTabSwipe(i);
            }

            @Override
            public void onPageSelected(int i) {

            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });

    }

    @Override
    public void onCurrentTabClicked() {

    }
}