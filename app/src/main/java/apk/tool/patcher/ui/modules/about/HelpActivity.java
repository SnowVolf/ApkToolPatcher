package apk.tool.patcher.ui.modules.about;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import apk.tool.patcher.App;
import apk.tool.patcher.R;
import ru.svolf.melissa.widget.AutoResizeTextView;
import apk.tool.patcher.util.TextUtil;
import ru.svolf.melissa.swipeback.SwipeBackActivity;
import ru.svolf.melissa.swipeback.SwipeBackLayout;

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

        issue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(HelpActivity.this)
                        .setTitle(R.string.caption_issues)
                        .setMessage(R.string.help_warning)
                        .setPositiveButton(R.string.check_issue, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                TextUtil.goLink(HelpActivity.this, "https://github.com/SnowVolf/ApkToolPatcherPublic/issues");
                            }
                        })
                        .setNegativeButton(R.string.create_issue, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                TextUtil.goLink(HelpActivity.this, "https://github.com/SnowVolf/ApkToolPatcherPublic/issues/new/choose");
                            }
                        })
                        .show();

            }
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
