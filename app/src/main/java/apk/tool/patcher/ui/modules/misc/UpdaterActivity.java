package apk.tool.patcher.ui.modules.misc;

import android.os.Bundle;

import apk.tool.patcher.R;
import ru.svolf.melissa.swipeback.SwipeBackActivity;
import ru.svolf.melissa.swipeback.SwipeBackLayout;

public class UpdaterActivity extends SwipeBackActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updater);
        setEdgeLevel(SwipeBackLayout.EdgeLevel.MED);
    }
}
