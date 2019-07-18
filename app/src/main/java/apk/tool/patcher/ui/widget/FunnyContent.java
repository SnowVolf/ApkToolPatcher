package apk.tool.patcher.ui.widget;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v7.content.res.AppCompatResources;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import apk.tool.patcher.R;

/**
 * Created by radiationx on 06.10.17.
 */

public class FunnyContent extends RelativeLayout {
    private ImageView image;
    private TextView title, desc;

    public FunnyContent(Context context) {
        super(context);
        inflate(context, R.layout.funny_content, this);
        image = findViewById(R.id.funny_image);
        title = findViewById(R.id.funny_title);
        desc = findViewById(R.id.funny_desc);
    }

    public FunnyContent setImage(@DrawableRes int resId) {
        image.setImageDrawable(AppCompatResources.getDrawable(getContext(), resId));
        return this;
    }

    /*public FunnyContent setTitle(String text) {
        title.setText(text);
        title.setVisibility(VISIBLE);
        return this;
    }*/

    /*public FunnyContent setDesc(String text) {
        desc.setText(text);
        desc.setVisibility(VISIBLE);
        return this;
    }*/

    public FunnyContent setTitle(@StringRes int resId) {
        title.setText(resId);
        title.setVisibility(VISIBLE);
        return this;
    }

    public FunnyContent setDesc(@StringRes int resId) {
        desc.setText(resId);
        desc.setVisibility(VISIBLE);
        return this;
    }
}
