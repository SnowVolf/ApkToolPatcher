package apk.tool.patcher.ui.modules.decompiler;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import apk.tool.patcher.R;
import apk.tool.patcher.util.SysUtils;
import ru.atomofiron.apknator.Adapters.PreviewAdapter;
import ru.atomofiron.apknator.Utils.Cmd;
import ru.atomofiron.apknator.Utils.CommandFactory;
import ru.atomofiron.apknator.Utils.Node;

public class PreviewFragment extends Fragment implements AdapterView.OnItemClickListener {

    private final ArrayList<Node> nodes = new ArrayList<>();
    private Context co;
    private SharedPreferences sp;
    private View selfView = null;
    private String path = null;
    private boolean isNew = true;
    private ListView listView;
    private PreviewAdapter adapter;
    private CommandFactory commandFactory;
    private String[] classes;
    private String[] methods;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        co = getActivity();
        sp = SysUtils.SP(co);
        commandFactory = new CommandFactory(co);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        SysUtils.Log("onCreateView()");
        if (selfView != null && !isNew)
            return selfView;
        else
            selfView = inflater.inflate(R.layout.fragment_preview, container, false);
        isNew = false;

        SysUtils.Log("new PreviewAdapter()");
        adapter = new PreviewAdapter(co);
        listView = selfView.findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        new DumpAsyncTask().execute();

        return selfView;
    }

    public void setPathFile(String path) {
        isNew = this.path == null || !this.path.equals(path);
        this.path = path;
    }


    private ArrayList<Node> extractClasses() {
        SysUtils.Log("extractClasses()");
        String bakslami = sp.getString(SysUtils.TOOL_BAKSMALI, "");
        if (!checkTool(bakslami, SysUtils.TOOL_BAKSMALI))
            return null;

        SysUtils.Log("bakslami: " + bakslami);
        classes = Cmd.Exec(sp.getBoolean(SysUtils.PREFS_USE_ROOT, false), commandFactory.getExtractClassesCommand(path, bakslami)).getResultData().split("\n");

        return Node.parse(classes, "");
    }

    private boolean checkTool(String tool, String name) {
        SysUtils.Log("tool = " + tool);
        if (tool.isEmpty()) {
            SysUtils.Toast(co, co.getString(R.string.no_tool, name));
            return false;
        }
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Node node = nodes.get(i);
        if (!node.isPackage)
            return;

        if (!node.extended) {
            ArrayList<Node> list = Node.parse(classes, node.path);
            if (list.size() != 0) {
                node.extended = true;
                nodes.addAll(i + 1, list);
                adapter.update(nodes);
            }
        } else {
            int n = nodes.size();
            node.extended = false;
            for (int j = i + 1; j < n; j++)
                if (nodes.get(j).path.startsWith(node.path)) {
                    nodes.remove(j);
                    j--;
                    n--;
                }
            adapter.update(nodes);
        }
    }

    private class DumpAsyncTask extends AsyncTask<String, String, String> implements DialogInterface.OnClickListener {
        private ProgressDialog progressDialog;

        @Override
        protected void onProgressUpdate(String... progress) {
        }

        @Override
        protected void onPostExecute(String str) {
            SysUtils.Log("onPostExecute(): " + str);
            progressDialog.dismiss();
            adapter.update(nodes);

            super.onPostExecute(str);
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(co);
            progressDialog.setIndeterminate(true);
            progressDialog.setTitle(co.getString(R.string.parsing));
            progressDialog.setCancelable(false);
            progressDialog.setButton(DialogInterface.BUTTON_NEUTRAL, co.getString(R.string.dostop), this);
            progressDialog.show();

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... parameter) {
            nodes.clear();
            nodes.addAll(extractClasses());
            return "TEST";
        }

        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            cancel(true);
        }
    }

}
