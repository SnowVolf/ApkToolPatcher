package apk.tool.patcher.ui.modules.decompiler;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import apk.tool.patcher.R;
import apk.tool.patcher.util.SysUtils;
import ru.atomofiron.apknator.Adapters.SimpleAdapter;
import ru.atomofiron.apknator.Managers.ToolsManager;

public class ToolsFragment extends Fragment {

    ListView filesListView;
    SharedPreferences sp;
    Context co;
    Activity ac;
    ToolsManager.ToolSet toolSet;
    private SysUtils.ActionListener actionListener;

    public ToolsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        co = getContext();
        ac = getActivity();
        sp = SysUtils.SP(co);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tools, container, false);

        filesListView = view.findViewById(R.id.aliases_list);
        filesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String name = ((TextView) view.findViewById(R.id.file_name)).getText().toString();
                SharedPreferences.Editor editor = sp.edit();
                if (name.startsWith(SysUtils.TOOL_APKTOOL))
                    editor.putString(SysUtils.TOOL_APKTOOL, name);
                else if (name.startsWith(SysUtils.TOOL_AAPT))
                    editor.putString(SysUtils.TOOL_AAPT, name);
                else if (name.startsWith(SysUtils.TOOL_SIGNAPK))
                    editor.putString(SysUtils.TOOL_SIGNAPK, name);
                else if (name.startsWith(SysUtils.TOOL_SMALI))
                    editor.putString(SysUtils.TOOL_SMALI, name);
                else if (name.startsWith(SysUtils.TOOL_BAKSMALI))
                    editor.putString(SysUtils.TOOL_BAKSMALI, name);
                editor.apply();
                inflateListView();
                actionListener.onAction(SysUtils.FR_VERSIONS_CHANGE, null, 0, false);
            }
        });
        toolSet = ToolsManager.fullReview(co);
        inflateListView();
        return view;
    }

    private void inflateListView() {
        List<Map<String, Object>> listItems = new ArrayList<>();
        for (String toolName : SysUtils.TOOLS_ARR) {
            ArrayList<String> versions = toolSet.getVersions(toolName);
            for (String ver : versions) {
                Map<String, Object> listItem = new HashMap<>();
                listItem.put(SysUtils.FILENAME, ver);
                listItem.put(SysUtils.OUTPUT, sp.getString(toolName, "").equals(ver));
                listItem.put(SysUtils.ICON, ver.endsWith(".jar") ?
                        co.getResources().getDrawable(R.drawable.jar) :
                        co.getResources().getDrawable(R.drawable.exec));
                listItems.add(listItem);
            }
        }
        SimpleAdapter simpleAdapter = new SimpleAdapter(getActivity(), listItems, R.layout.item_list,
                new String[]{SysUtils.FILENAME, SysUtils.ICON, SysUtils.OUTPUT},
                new int[]{R.id.file_name, R.id.icon, R.id.file_modify});
        filesListView.setAdapter(simpleAdapter);
    }

    public void setActionListener(SysUtils.ActionListener listener) {
        actionListener = listener;
    }
}
