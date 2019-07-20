package apk.tool.patcher.ui.modules.base;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.CallSuper;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import apk.tool.patcher.R;
import apk.tool.patcher.ui.widget.ContentController;
import apk.tool.patcher.ui.widget.FunnyContent;
import ru.svolf.melissa.swipeback.SwipeBackFragment;

/**
 * Фрагмент, оснащенный системой контроля за состоянием данных
 * если данных нет, отображается специальная вьюха, извещающая об этом
 */
public class DataFragment extends SwipeBackFragment {
    protected ContentController contentController;
    protected ViewGroup fragmentContent;
    protected ViewGroup additionalContent;
    protected ProgressBar contentProgress;
    protected View view;


    @CallSuper
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_data, container, false);
        fragmentContent = findViewById(R.id.fragment_content);
        additionalContent = findViewById(R.id.additional_content);
        contentProgress = additionalContent.findViewById(R.id.content_progress);
        contentController = new ContentController(contentProgress, additionalContent, fragmentContent);
        return view;
    }

    protected void baseInflateFragment(LayoutInflater inflater, @LayoutRes int res) {
        inflater.inflate(res, fragmentContent, true);
    }

    public final <T extends View> T findViewById(@IdRes int id) {
        return view.findViewById(id);
    }

    protected void startRefreshing() {
        contentController.startRefreshing();
    }

    protected void stopRefreshing() {
        contentController.stopRefreshing();
    }

    public void setRefreshing(boolean refreshing) {
        if (refreshing)
            startRefreshing();
        else
            stopRefreshing();
    }

    protected void observe(boolean emptyData) {
        if (emptyData) {
            if (!contentController.contains(ContentController.TAG_NO_DATA)) {
                contentController.addContent(getEmptyView(), ContentController.TAG_NO_DATA);
            }
            contentController.showContent(ContentController.TAG_NO_DATA);
        } else {
            contentController.hideContent(ContentController.TAG_NO_DATA);
        }
    }

    protected FunnyContent getEmptyView() {
        return new FunnyContent(getContext())
                .setImage(R.drawable.ic_cancel)
                .setTitle(R.string.no_data)
                .setDesc(R.string.no_data_desc);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (contentController != null) {
            contentController.destroy();
        }
    }
}