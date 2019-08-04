package apk.tool.patcher.ui.widget;


import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.core.content.res.ResourcesCompat;

import apk.tool.patcher.R;

public class FontTextView extends androidx.appcompat.widget.AppCompatTextView {

    public FontTextView(Context context) {
        super(context);
        setGoogleSans(context);
    }

    public FontTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setGoogleSans(context);
    }

    public FontTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setGoogleSans(context);
    }

    private void setGoogleSans(Context ctx) {
        setFreezesText(true);
        Typeface typeface;
        int style = getTypeface().getStyle();

        switch (style) {
            case Typeface.BOLD:
                typeface = ResourcesCompat.getFont(ctx, R.font.googlesans_bold);
                break;
            case Typeface.ITALIC:
                typeface = ResourcesCompat.getFont(ctx, R.font.googlesans_italic);
                break;
            case Typeface.BOLD_ITALIC:
                typeface = ResourcesCompat.getFont(ctx, R.font.googlesans_bolditalic);
                break;
            case Typeface.NORMAL:
            default:
                typeface = ResourcesCompat.getFont(ctx, R.font.googlesans_regular);
                break;
        }
        setTypeface(typeface);
    }
}