package apk.tool.patcher.ui.modules.misc;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import apk.tool.patcher.R;

public class XiaomiGovnoFragment extends Fragment {

    public static final String FRAGMENT_TAG = "XiaomiGovnische";
    private XiaomiGovnoViewModel mViewModel;

    public static XiaomiGovnoFragment newInstance() {
        return new XiaomiGovnoFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pepe, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(XiaomiGovnoViewModel.class);
        // TODO: Use the ViewModel
    }

}
