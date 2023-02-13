package apk.tool.patcher.ui.misc;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import apk.tool.patcher.R;
import ru.svolf.melissa.swipeback.SwipeBackFragment;

public class XiaomiGovnoFragment extends SwipeBackFragment {

    public static final String FRAGMENT_TAG = "XiaomiGovnische";
    private XiaomiGovnoViewModel mViewModel;

    public static XiaomiGovnoFragment newInstance() {
        return new XiaomiGovnoFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return attachToSwipeBack(inflater.inflate(R.layout.fragment_pepe, container, false));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button btnExit = view.findViewById(R.id.exit);
        btnExit.setOnClickListener(v -> getActivity().onBackPressed());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // TODO: Use the ViewModel}
    }

}
