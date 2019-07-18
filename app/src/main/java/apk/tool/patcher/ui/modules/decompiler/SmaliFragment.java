package apk.tool.patcher.ui.modules.decompiler;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import apk.tool.patcher.R;
import apk.tool.patcher.util.SysUtils;
import ru.atomofiron.apknator.Adapters.SmaliAdapter;

public class SmaliFragment extends Fragment {


    final ArrayList<String> smaliStrings = new ArrayList<>();
    View thisView = null;
    ListView listSmali;
    TextView pathView;
    ProgressBar progBar;
    EditText editOriginal;
    EditText editReplace;
    View savePatchView;
    EditText etPackage;

    Context co;
    Activity ac;
    Listner listener;
    SmaliAdapter baseAdapter;
    MenuItem menuPrev;
    MenuItem menuNext;

    String filePath;
    String packageName = "";
    boolean isEditOriginal = true;
    ArrayList<String> opcomands = new ArrayList<>();
    ArrayList<String> bytes = new ArrayList<>();
    Map<String, String> com2bytes = new HashMap<>();
    String searchString;
    int searchPos = 1;
    ArrayList<Integer> positions = new ArrayList<>();
    private boolean saved = true;
    private boolean patchSaved = true;

    public SmaliFragment() {
    }

    public static SmaliFragment newInstance(String filePath) {
        SmaliFragment fragment = new SmaliFragment();
        Bundle args = new Bundle();
        args.putString(SysUtils.PATH, filePath);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        co = getContext();
        ac = getActivity();

        filePath = getArguments().getString(SysUtils.PATH, "");
    }

    public void setMenuArrs(MenuItem menuPrev, MenuItem menuNext) {
        this.menuPrev = menuPrev;
        this.menuNext = menuNext;
    }

    private void updateMenuArrsVisible() {
        boolean value = !positions.isEmpty();
        menuPrev.setEnabled(value);
        menuNext.setEnabled(value);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (filePath.isEmpty()) {
            SysUtils.Toast(co, getString(R.string.error));
            return new View(co);
        }
        updateMenuArrsVisible();
        if (thisView == null) {
            View view = inflater.inflate(R.layout.fragment_smali, container, false);
            init(view);
            return view;
        } else
            return thisView;
    }

    void init(View view) {
        editReplace = view.findViewById(R.id.edit_replace);

        pathView = view.findViewById(R.id.tvpath);
        listSmali = view.findViewById(R.id.list_smali);
        progBar = view.findViewById(R.id.progrss_bar);
        editOriginal = view.findViewById(R.id.edit_original);
        editReplace = view.findViewById(R.id.edit_replace);
        listener = new Listner();
        editOriginal.setOnTouchListener(listener);
        editReplace.setOnTouchListener(listener);
        listSmali.setOnItemClickListener(listener);
        listSmali.setOnItemLongClickListener(listener);


        savePatchView = View.inflate(co, R.layout.layout_save, null);
        etPackage = savePatchView.findViewById(R.id.package_name);
        savePatchView.findViewById(R.id.clear).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                etPackage.setText("");
            }
        });

        pathView.setText(filePath);
        final String path = filePath;
        new Thread(new Runnable() {
            @Override
            public void run() {
                StringBuilder text = new StringBuilder();
                try {
                    InputStream fis = co.getAssets().open("smali.txt");
                    InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
                    BufferedReader br = new BufferedReader(isr);
                    String line;
                    boolean last_ = false;
                    // считываем опкоды с байтами
                    while ((line = br.readLine()) != null) {
                        if (last_)
                            opcomands.add(line.split(" ")[1]);

                        if (!last_ && !line.equals("_"))
                            bytes.add(line + " ");

                        last_ = line.equals("_");
                    }
                    fis.close();
                    isr.close();
                    for (int i = 0; i < opcomands.size(); i++)
                        com2bytes.put(opcomands.get(i), bytes.get(i));

                    // считываем файл, формируем реалбайты
                    final long size = new File(path).length();
                    fis = new FileInputStream(path);
                    isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
                    br = new BufferedReader(isr);
                    int line_n = 0;
                    while ((line = br.readLine()) != null) {
                        if (line_n == 0) {
                            String[] s = line.split("L");
                            if (s.length > 1)
                                packageName = SysUtils.getParentPath(s[1]).replace('/', '.');
                        }
                        line_n++;
                        smaliStrings.add(line);
                        text.append(line).append("\n");
                        if (line_n % 100 == 0) {
                            final int load = (int) Math.ceil((text.length() * 100) / size);
                            ac.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (load < 98)
                                        progBar.setProgress(load);
                                    else
                                        progBar.setIndeterminate(true);
                                }
                            });
                        }
                    }
                    fis.close();
                    isr.close();

                } catch (Exception e) {
                    SysUtils.Log(e.toString());
                }
                final ArrayList<String> exitText = smaliStrings;
                ac.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        baseAdapter = new SmaliAdapter(co, R.layout.item_opcode, exitText, listSmali, positions);
                        listSmali.setAdapter(baseAdapter);
                        progBar.setVisibility(View.GONE);
                    }
                });
            }
        }).start();

    }

    @Override
    public void onStop() {
        super.onStop();
        thisView = getView();
    }

    public void actionSearch() {
        final EditText editText = new EditText(ac);
        new android.app.AlertDialog.Builder(ac)
                .setView(editText)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.find, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Editable text = editText.getText();
                        if (text.length() > 0)
                            search(text.toString());
                        else {
                            positions.clear();
                            baseAdapter.updatePositions();
                            updateMenuArrsVisible();
                        }
                    }
                }).create().show();
    }

    void search(String string) {
        positions.clear();
        if (string != null)
            searchString = string;
        if (searchString == null)
            return;

        for (int i = 0; i < smaliStrings.size(); i++)
            if (smaliStrings.get(i).contains(searchString))
                positions.add(i);
        if (positions.size() > 0) {
            searchPos = 0;
            //scroll();
            SysUtils.Toast(co, getString(R.string.found, positions.size()));
        } else
            SysUtils.Toast(co, getString(R.string.not_found));
        baseAdapter.updatePositions();
        updateMenuArrsVisible();
    }

    int getOffset(int line_n) {
        int offset = 0;
        for (int i = 0; i < line_n; i++)
            offset += smaliStrings.get(i).length() + 1;

        return offset;
    }

    void saveFile() {
        StringBuilder sb = new StringBuilder();
        for (String line : smaliStrings)
            sb.append(line).append("\n");

        sb.delete(sb.length() - 1, sb.length());
        writeFile(filePath, sb.toString());
    }

    public void savePatch() {
        String path = SysUtils.SP(ac)
                .getString(SysUtils.PREFS_PATCH_PATH, ac.getExternalFilesDir(null).getAbsolutePath());
        if (!new File(path).canWrite()) {
            SysUtils.Toast(co, getString(R.string.error_dir));
            return;
        }
        etPackage.setText(packageName);
        new AlertDialog.Builder(co)
                .setTitle(R.string.save_patch)
                .setView(savePatchView)
                .setCancelable(false)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = ((EditText) savePatchView.findViewById(R.id.name)).getText().toString();
                        String package_name = etPackage.getText().toString();
                        String begin = ((EditText) savePatchView.findViewById(R.id.begin)).getText().toString();
                        String end = ((EditText) savePatchView.findViewById(R.id.end)).getText().toString();
                        if (package_name.isEmpty()) {
                            SysUtils.Toast(co, getString(R.string.need_package_name));
                            return;
                        }
                        String output = getString(R.string.patch_template, begin, editOriginal.getText(), editReplace.getText(), end);
                        String path = SysUtils.SP(ac)
                                .getString(SysUtils.PREFS_PATCH_PATH, ac.getExternalFilesDir(null).getAbsolutePath());
                        SysUtils.Log("path for patch = " + path);
                        writeFile(path + "/" + name + "_" + package_name + ".txt", output);
                        patchSaved = true;
                    }
                }).create().show();
    }

    void writeFile(String path, String data) {
        writeFile(new File(path), data);
    }

    void writeFile(File dest, String data) {
        try {
            if (dest.exists() && !dest.delete())
                SysUtils.Toast(co, getString(R.string.cant_delete_file));
            else if (!dest.createNewFile())
                SysUtils.Toast(co, getString(R.string.cant_make_a_file));
            else {
                BufferedWriter bw = new BufferedWriter(new FileWriter(dest, false));
                bw.append(data);
                bw.close();
                saved = true;
                SysUtils.Toast(co, getString(R.string.save_success));
            }
        } catch (Exception e) {
            SysUtils.Toast(co, e.toString());
        }
    }

    public void menuAction(int action) {
        switch (action) {
            case SysUtils.MENU_CLEAR:
                editOriginal.setText("");
                editReplace.setText("");
                break;
            case SysUtils.MENU_SEARCH:
                actionSearch();
                break;
            case SysUtils.MENU_PREV:
                baseAdapter.scrollFor(false);
                break;
            case SysUtils.MENU_NEXT:
                baseAdapter.scrollFor(true);
                break;
            case SysUtils.MENU_SAVE:
                if (!saved) saveFile();
                break;
            case SysUtils.MENU_SAVE_PATCH:
                if (patchSaved || (editReplace.length() == 0 && editOriginal.length() == 0))
                    return;
                if (editReplace.length() == editOriginal.length())
                    savePatch();
                else
                    SysUtils.Toast(co, getString(R.string.patch_need));
                break;
            case SysUtils.MENU_CLOSE:
                if (!saved || !patchSaved)
                    new android.support.v7.app.AlertDialog.Builder(getActivity())
                            .setTitle(R.string.save_before)
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    ((ApkToolActivity) getActivity()).closeSmali();
                                }
                            })
                            .setNegativeButton(R.string.back, null)
                            .create().show();
                else
                    ((ApkToolActivity) getActivity()).closeSmali();
                break;
        }
    }

    String cutSpacesOff(String string) {
        while (string.startsWith(" ")) string = string.substring(1);
        return string;
    }

    public ListView getListView() {
        return listSmali;
    }

    class iTextWatcher implements TextWatcher {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            SysUtils.Log("beforeTextChanged()");
        }

        public void onTextChanged(final CharSequence s, int start, int deleted, int inserted) {
            SysUtils.Log("editOriginal: " + start + " " + deleted + " " + inserted + " " + s);
            saved = false;
            int length = editReplace.getText().length();
            int enemyStart = start;

            if (length < start) {
                enemyStart = length;
                deleted = 0; // очень продуманная логика:
            }

            if (length < enemyStart + deleted)
                deleted = length - start;

            editReplace.getText().replace(enemyStart, enemyStart + deleted, s.toString(), start, start + inserted);
        }

        public void afterTextChanged(Editable s) {
            SysUtils.Log("afterTextChanged()");
        }
    }

    class Listner implements View.OnTouchListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String line = smaliStrings.get(position);
            line = cutSpacesOff(line);
            SysUtils.Log("line = ".concat(line));

            if (line.startsWith(".method")) {
                /*if (position < 0) return;
                line = cutSpacesOff(cutSpacesOff(smaliStrings.get(position)).substring(7));
				if (line.indexOf("(") < 1) return;
				line = line.split("\\(")[0];
				String[] parts = line.split(" ");
				line = parts[parts.length-1];
				if (line == null || line.isEmpty()) return;*/
                StringBuilder sb = new StringBuilder();
                position++;
                while (!(line = cutSpacesOff(smaliStrings.get(position))).startsWith(".end")) {
                    position++;
                    line = getCommand(line);
                    if (line.isEmpty() || !opcomands.contains(line))
                        continue;
                    sb.append(com2bytes.get(line));
                    if (smaliStrings.size() == position)
                        break;
                }
                patchSaved = false;
                if (isEditOriginal)
                    editOriginal.setText(sb.toString());
                else
                    editReplace.setText(sb.toString());
                return;
            }

            line = getCommand(line);
            if (line.isEmpty() || !opcomands.contains(line))
                return;
            String code = null;
            for (int i = 0; i < opcomands.size(); i++)
                if (opcomands.get(i).equals(line)) {
                    code = bytes.get(i);
                    break;
                }
            if (code == null)
                return;
            patchSaved = false;
            if (isEditOriginal)
                editOriginal.append(code);
            else
                editReplace.append(code);
        }

        String getCommand(String line) {
            line = cutSpacesOff(line);
            if (line.isEmpty())
                return "";
            else
                return line.split(" ")[0];
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    isEditOriginal = v.getId() == R.id.edit_original;
                /*editOriginal.setTextIsSelectable(true);
				editOriginal.setInputType(0);*/
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (isEditOriginal)
                        editReplace.setScrollX(editOriginal.getScrollX());
                    else
                        editOriginal.setScrollX(editReplace.getScrollX());
                    break;
            }
            return false;
        }

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
            final EditText et = new EditText(ac);
            final String orig = smaliStrings.get(position);
            et.setText(orig);
            new AlertDialog.Builder(ac)
                    .setView(et)
                    .setNeutralButton(R.string.cancel, null)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String string = et.getText().toString();
                            if (orig.equals(string))
                                return;
                            smaliStrings.remove(position);
                            if (!string.isEmpty()) {
                                String[] split = string.split("\\n");
                                for (int i = 0; i < split.length; i++)
                                    smaliStrings.add(position + i, split[i]);
                            }
                            baseAdapter.notifyDataSetChanged();
                            saved = false;
                        }
                    }).create().show();
            return false;
        }
    }

}
