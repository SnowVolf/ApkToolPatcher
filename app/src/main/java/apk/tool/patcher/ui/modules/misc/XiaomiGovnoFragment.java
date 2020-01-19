package apk.tool.patcher.ui.modules.misc;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(XiaomiGovnoViewModel.class);
        // TODO: Use the ViewModel
    }

}
