package apk.tool.patcher.filesystem;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import apk.tool.patcher.R;
import apk.tool.patcher.ui.base.BaseActivity;
import apk.tool.patcher.util.Cs;

public class ExFileInfo extends BaseActivity {
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_exfileinfo);
        TextView textView = findViewById(R.id.exfile_info);
        if (textView != null) {
            textView.setText("Test");
        }
    }

    public void btnok_clicked(View view) {
        Intent intent = new Intent();
        intent.putExtra(Cs.EXTRA_RESULT_INT, getIntent().getIntExtra(Cs.EXTRA_RESULT_INT, 0));
        setResult(RESULT_OK, intent);
        finish();
    }

    public void btncancel_clicked(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }
}
