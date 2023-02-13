package apk.tool.patcher.ui.inspector;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import apk.tool.patcher.R;
import apk.tool.patcher.ui.base.DataFragment;
import apk.tool.patcher.ui.base.adapters.InterestAdapter;
import ru.svolf.melissa.model.InterestSmaliItem;


public class HiddenFragment extends DataFragment {
    private static final String HIDDEN_PLACES = "hidden_places";

    // TODO: Rename and change types of parameters
    private ArrayList<InterestSmaliItem> mSortedItems;

    public HiddenFragment() {
        // Required empty public constructor
    }

    public static HiddenFragment newInstance(ArrayList<InterestSmaliItem> list) {
        HiddenFragment fragment = new HiddenFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(HIDDEN_PLACES, list);
        fragment.setArguments(args);//Артем пидор
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mSortedItems = getArguments().getParcelableArrayList(HIDDEN_PLACES);
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
        RecyclerView list = view.findViewById(R.id.list_places);
        InterestAdapter adapter = new InterestAdapter(mSortedItems);
        list.setLayoutManager(new LinearLayoutManager(getContext()));
        list.setAdapter(adapter);
        observe(mSortedItems.isEmpty());
    }
}
