package apk.tool.patcher.ui.modules.misc;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import apk.tool.patcher.R;

public class EasterEggFragment extends Fragment {

    private EasterEggViewModel mViewModel;

    public static EasterEggFragment newInstance() {
        return new EasterEggFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.easter_egg_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(EasterEggViewModel.class);
        // TODO: Use the ViewModel
    }

}
