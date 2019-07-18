package ru.svolf.melissa;

import java.util.ArrayList;

import apk.tool.patcher.App;
import apk.tool.patcher.R;
import ru.svolf.melissa.model.FlexibleItem;

public class AboutItems {

    private ArrayList<FlexibleItem> createdMenuItems = new ArrayList<>();

    public AboutItems() {
        createdMenuItems.add(new FlexibleItem(App.bindString(R.string.app_name),
                String.format("%s%s%s",
                        App.bindString(R.string.patcher_help_1),
                        App.bindString(R.string.patcher_help_2),
                        App.bindString(R.string.patcher_help_3))));
        createdMenuItems.add(new FlexibleItem(App.bindString(R.string.patcher_help_4),
                App.bindString(R.string.patcher_help_5)
        ));
        createdMenuItems.add(new FlexibleItem(App.bindString(R.string.patcher_help_6),
                App.bindString(R.string.patcher_help_7)));
        createdMenuItems.add(new FlexibleItem("Info", "Help created by <a href=\"https://github.com/SnowVolf\">SnowVolf (git)</a> /" +
                " <a href=\"http://4pda.ru/forum/index.php?showuser=4324432\">Snow Volf (4pda)</a>"));
    }

    public ArrayList<FlexibleItem> getCreatedMenuItems() {
        return createdMenuItems;
    }
}

