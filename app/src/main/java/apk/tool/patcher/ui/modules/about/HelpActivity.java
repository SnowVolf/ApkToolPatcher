package apk.tool.patcher.ui.modules.about;

//import android.*;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

import apk.tool.patcher.R;
import apk.tool.patcher.ui.modules.base.adapters.AboutAdapter;
import ru.svolf.melissa.AboutItems;
import ru.svolf.melissa.model.FlexibleItem;
import ru.svolf.melissa.swipeback.SwipeBackActivity;
import ru.svolf.melissa.swipeback.SwipeBackLayout;

public class HelpActivity extends SwipeBackActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        setEdgeLevel(SwipeBackLayout.EdgeLevel.MED);

        RecyclerView contentView = (RecyclerView) findViewById(R.id.help_list);

        ArrayList<FlexibleItem> items = new AboutItems().getCreatedMenuItems();

        contentView.setLayoutManager(new LinearLayoutManager(this));
        contentView.setAdapter(new AboutAdapter(items));

    }
}
