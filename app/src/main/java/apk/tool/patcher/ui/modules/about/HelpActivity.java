package apk.tool.patcher.ui.modules.about;

import android.os.Bundle;
import android.util.TypedValue;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import androidx.annotation.Nullable;

import apk.tool.patcher.App;
import apk.tool.patcher.R;
import apk.tool.patcher.util.TextUtil;
import ru.svolf.melissa.fragment.dialog.SweetContentDialog;
import ru.svolf.melissa.swipeback.SwipeBackActivity;
import ru.svolf.melissa.swipeback.SwipeBackLayout;
import ru.svolf.melissa.widget.AutoResizeTextView;

public class HelpActivity extends SwipeBackActivity {
    private WebView webView;
    private AutoResizeTextView caption;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        webView = findViewById(R.id.help);
        caption = findViewById(R.id.caption);
        calculateTextSize();
        Button issue = findViewById(R.id.button_addition);
        setEdgeLevel(SwipeBackLayout.EdgeLevel.MIN);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient());
        webView.setBackgroundColor(App.getColorFromAttr(this, android.R.attr.windowBackground));
        webView.loadUrl("https://github.com/SnowVolf/ApkToolPatcherPublic/wiki");

        issue.setOnClickListener(view -> {
            SweetContentDialog builder = new SweetContentDialog(HelpActivity.this);
            builder.setTitle(R.string.caption_issues);
            builder.setMessage(R.string.help_warning);
            builder.setPositive(R.drawable.ic_check, getString(R.string.check_issue),
                    (view1) -> TextUtil.goLink(HelpActivity.this, "https://github.com/SnowVolf/ApkToolPatcherPublic/issues"));
            builder.setNegative(R.drawable.ic_help_outline, getString(R.string.create_issue),
                    (view1) -> TextUtil.goLink(HelpActivity.this, "https://github.com/SnowVolf/ApkToolPatcherPublic/issues/new/choose"));
            builder.show();
        });

    }

    private void calculateTextSize() {
        caption.setMinTextSize(24f);
        caption.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25f);
        caption.resizeText();
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) webView.goBack();
        else super.onBackPressed();
    }
}
