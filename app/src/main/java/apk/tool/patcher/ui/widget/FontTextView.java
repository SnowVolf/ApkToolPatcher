package apk.tool.patcher.ui.widget;


import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

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
                typeface = Typeface.createFromAsset(ctx.getAssets(), "fonts/GoogleSans-Bold.ttf");
                break;
            case Typeface.ITALIC:
                typeface = Typeface.createFromAsset(ctx.getAssets(), "fonts/GoogleSans-Italic.ttf");
                break;
            case Typeface.BOLD_ITALIC:
                typeface = Typeface.createFromAsset(ctx.getAssets(), "fonts/GoogleSans-BoldItalic.ttf");
                break;
            case Typeface.NORMAL:
                typeface = Typeface.createFromAsset(ctx.getAssets(), "fonts/GoogleSans-Regular.ttf");
                break;
            default:
                typeface = Typeface.createFromAsset(ctx.getAssets(), "fonts/GoogleSans-Regular.ttf");
                break;
        }
        setTypeface(typeface);
    }
}