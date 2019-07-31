package apk.tool.patcher.ui.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;

import com.google.android.material.appbar.CollapsingToolbarLayout;

public class SweetBar extends CollapsingToolbarLayout {
    public SweetBar(Context context) {
        super(context);
        init(context);
    }

    public SweetBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SweetBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @SuppressLint("RestrictedApi")
    private void init(Context context){
//        setCollapsedTitleTextColor(App.getColorFromAttr(context, android.R.attr.textColor));
//        setExpandedTitleColor(App.getColorFromAttr(context, android.R.attr.textColor));
    }
}
