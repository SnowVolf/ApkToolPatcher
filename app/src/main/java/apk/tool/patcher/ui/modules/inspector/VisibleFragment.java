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

import apk.tool.patcher.R;
import apk.tool.patcher.ui.modules.base.DataFragment;
import apk.tool.patcher.ui.modules.base.adapters.InterestAdapter;
import ru.svolf.melissa.model.InterestSmaliItem;


public class VisibleFragment extends DataFragment {
    private static final String VISIBLE_PLACES = "visible_places";

    // TODO: Rename and change types of parameters
    private ArrayList<InterestSmaliItem> mNormalItems;

    public VisibleFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static VisibleFragment newInstance(ArrayList<InterestSmaliItem> list) {
        VisibleFragment fragment = new VisibleFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(VISIBLE_PLACES, list);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mNormalItems = getArguments().getParcelableArrayList(VISIBLE_PLACES);
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
        InterestAdapter adapter = new InterestAdapter(mNormalItems);
        mList.setLayoutManager(new LinearLayoutManager(getContext()));
        mList.setAdapter(adapter);
        observe(mNormalItems.isEmpty());
    }
}
