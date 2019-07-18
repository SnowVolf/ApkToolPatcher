package apk.tool.patcher.ui.modules.inspector;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Locale;

import apk.tool.patcher.R;
import apk.tool.patcher.api.Project;
import apk.tool.patcher.ui.modules.base.DataFragment;
import apk.tool.patcher.ui.modules.base.adapters.FlexibleAdapter;
import ru.svolf.melissa.model.FlexibleItem;


public class AnalyticsFragment extends DataFragment {
    /**
     * Ключ для извлечения проекта
     */
    private static final String PROJECT = "project";
    /**
     * Лист с айтемами
     */
    private ArrayList<FlexibleItem> mItems = new ArrayList<>();

    /**
     * Метод для создания инстанса фрагмента с уже нужными данными
     *
     * @param project входной проект
     * @return новый экземпляр {@link AnalyticsFragment}
     */
    public static AnalyticsFragment newInstance(Project project) {
        AnalyticsFragment fragment = new AnalyticsFragment();
        Bundle args = new Bundle();
        args.putParcelable(PROJECT, project);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Project mProject = getArguments().getParcelable(PROJECT);
            if (mProject != null) {
                // Иначе при перелистывании вкладок будет удваиваться список
                if (mItems.size() > 0) {
                    mItems.clear();
                }
                mItems.add(new FlexibleItem(getString(R.string.inspector_src_path), mProject.getPath()));
                mItems.add(new FlexibleItem(getString(R.string.inspector_src_name), mProject.getName()));
                mItems.add(new FlexibleItem(getString(R.string.inspector_src_type), mProject.forApkTool() ? "ApkTool project" : "ApkEditor project"));
                mItems.add(new FlexibleItem(getString(R.string.inspector_src_dex_count),
                        mProject.isMultiDexed() ? String.format(Locale.ENGLISH, getString(R.string.inspector_multidexed),
                                mProject.volumes) : getString(R.string.inspector_one_dex)));
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        baseInflateFragment(inflater, R.layout.fragment_inspector);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        RecyclerView mList = view.findViewById(R.id.list_places);
        mList.setLayoutManager(new LinearLayoutManager(getContext()));
        FlexibleAdapter adapter = new FlexibleAdapter(mItems, R.layout.item_analytics);
        mList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        observe(mItems.isEmpty());
    }

}
