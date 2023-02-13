package apk.tool.patcher.ui.settings;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import apk.tool.patcher.App;
import apk.tool.patcher.R;
import apk.tool.patcher.util.Cs;
import jp.sblo.pandora.aGrep.CheckedString;
import jp.sblo.pandora.aGrep.Prefs;
import jp.sblo.pandora.aGrep.Search;
import ru.svolf.melissa.swipeback.SwipeBackFragment;
import ru.svolf.melissa.swipeback.SwipeBackLayout;

public class SearchSettingsFragment extends SwipeBackFragment {
    public static final String FRAGMENT_TAG = "search_settings_fragment";
    
    public LinearLayout mContainer, mNotFound;
    private ChipGroup mExtListView;
    private View rootView;
    private String mPath;
    private Prefs mPrefs;
    private Context mContext;
    private ArrayAdapter<String> mRecentAdapter;
    private CheckBox chkIgnoreCase, chkRegexp;
    private FloatingActionButton mFab;
    private View.OnLongClickListener mExtListener;
    private CompoundButton.OnCheckedChangeListener mCheckListener;
    private ImageButton clearBtn;
    private Button btnAddExt, btnHistory;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_settings_search, container, false);
        return attachToSwipeBack(rootView);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        // TODO: Implement mContext method
        super.onViewCreated(view, savedInstanceState);
        setEdgeLevel(SwipeBackLayout.EdgeLevel.MED);
        mPrefs = Prefs.loadPrefes(mContext);

        mPrefs.mHighlightBg = App.getColorFromAttr(mContext, R.attr.colorPrimary);
        mPrefs.mHighlightFg = App.getColorFromAttr(mContext, R.attr.text_color);
        mPrefs.savePrefs(mContext);

        if (getArguments() != null) {
            mPath = getArguments().getString(Cs.ARG_PATH_NAME);
        }

        mContainer = view.findViewById(R.id.search_container);
        mNotFound = view.findViewById(R.id.not_found);
        btnHistory = mContainer.findViewById(R.id.button_addition);

        mFab = view.findViewById(R.id.fab);
        chkRegexp = view.findViewById(R.id.checkre);
        chkIgnoreCase = view.findViewById(R.id.checkignorecase);
        clearBtn = view.findViewById(R.id.ButtonClear);
        btnAddExt = view.findViewById(R.id.addext);
        mExtListView = view.findViewById(R.id.listext);

        setPath();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final AutoCompleteTextView searchField = rootView.findViewById(R.id.EditText01);

        clearBtn.setOnClickListener(view -> {
            searchField.setText("");
            searchField.requestFocus();
        });

        chkRegexp.setChecked(mPrefs.mRegularExrpression);
        chkIgnoreCase.setChecked(mPrefs.mIgnoreCase);
        chkRegexp.setOnClickListener(view -> {
            mPrefs.mRegularExrpression = chkRegexp.isChecked();
            mPrefs.savePrefs(mContext);
        });
        chkIgnoreCase.setOnClickListener(view -> {
            mPrefs.mIgnoreCase = chkIgnoreCase.isChecked();
            mPrefs.savePrefs(mContext);
        });

        mFab.setOnClickListener(p1 -> {
            // TODO: Implement mContext method

            mPrefs.mDirList = new ArrayList<>();
            mPrefs.savePrefs(mContext);

            if (mPath != null && mPath.length() > 0) {
                // 二重チェック
                for (CheckedString t : mPrefs.mDirList) {
                    if (t.string.equalsIgnoreCase(mPath)) {
                        return;
                    }
                }
                mPrefs.mDirList.add(new CheckedString(mPath));

                mPrefs.savePrefs(mContext);

                String text = searchField.getText().toString();
                Intent it = new Intent(mContext, Search.class);
                it.setAction(Intent.ACTION_SEARCH);
                it.putExtra(SearchManager.QUERY, text);
                startActivity(it);
            }

        });

        mExtListener = view -> {
            final String strText = (String) ((TextView) view).getText();
            final CheckedString strItem = (CheckedString) view.getTag();
            // Show Dialog
            new AlertDialog.Builder(mContext)
                    .setTitle(R.string.label_remove_item_title)
                    .setMessage(getString(R.string.label_remove_item, strText))
                    .setPositiveButton(R.string.label_OK, (dialog, whichButton) -> {
                        mPrefs.mExtList.remove(strItem);
                        refreshExtList();
                        mPrefs.savePrefs(mContext);
                    })
                    .setNegativeButton(R.string.label_CANCEL, null)
                    .setCancelable(true)
                    .show();
            return true;
        };
//
        mCheckListener = (buttonView, isChecked) -> {
            final CheckedString strItem = (CheckedString) buttonView.getTag();
            strItem.checked = isChecked;
            mPrefs.savePrefs(mContext);
        };
//
        //refreshDirList();
        refreshExtList();

        btnAddExt.setOnClickListener(view -> {
            // Create EditText
            final EditText edtInput = new EditText(mContext);
            edtInput.setSingleLine();
            // Show Dialog
            new AlertDialog.Builder(mContext)
                    .setTitle(R.string.label_addext)
                    .setView(edtInput)
                    .setPositiveButton(R.string.label_OK, (dialog, whichButton) -> {
                        /* OKボタンをクリックした時の処理 */

                        String ext = edtInput.getText().toString();
                        if (ext.length() > 0) {
                            // 二重チェック
                            for (CheckedString t : mPrefs.mExtList) {
                                if (t.string.equalsIgnoreCase(ext)) {
                                    return;
                                }
                            }
                            mPrefs.mExtList.add(new CheckedString(ext));
                            refreshExtList();
                            mPrefs.savePrefs(mContext);
                        }
                    })
                    .setNeutralButton(R.string.label_no_extension, (dialog, whichButton) -> {
                        /* 拡張子無しボタンをクリックした時の処理 */

                        String ext = "*";
                        // 二重チェック
                        for (CheckedString t : mPrefs.mExtList) {
                            if (t.string.equalsIgnoreCase(ext)) {
                                return;
                            }
                        }
                        mPrefs.mExtList.add(new CheckedString(ext));
                        refreshExtList();
                        mPrefs.savePrefs(mContext);
                    })
                    .setNegativeButton(R.string.label_CANCEL, null)
                    .setCancelable(true)
                    .show();
        });

        searchField.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                String text = searchField.getEditableText().toString();
                Intent it = new Intent(mContext, Search.class);
                it.setAction(Intent.ACTION_SEARCH);
                it.putExtra(SearchManager.QUERY, text);
                startActivity(it);
                return true;
            }
            return false;
        });
        mRecentAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_dropdown_item_1line, new ArrayList<>());
        searchField.setAdapter(mRecentAdapter);

        btnHistory.setOnClickListener(item -> searchField.showDropDown());
    }

    @Override
    public void onResume() {
        super.onResume();

        final List<String> recent = mPrefs.getRecent(mContext);
        mRecentAdapter.clear();
        mRecentAdapter.addAll(recent);
        mRecentAdapter.notifyDataSetChanged();
    }

    @SuppressLint("RestrictedApi")
    private void setPath() {
        File test = new File(mPath);
        if (!test.exists()) {
            mContainer.setVisibility(View.GONE);
            mFab.setVisibility(View.GONE);
            mNotFound.setVisibility(View.VISIBLE);
        }
    }

    void setListItem(ChipGroup view,
                     ArrayList<CheckedString> list,
                     View.OnLongClickListener listener,
                     CompoundButton.OnCheckedChangeListener checkedChangeListener) {
        view.removeAllViews();
        Collections.sort(list, (object1, object2) -> object1.string.compareToIgnoreCase(object2.string));
        for (CheckedString s : list) {
            Chip v = (Chip) View.inflate(mContext, R.layout.list_dir, null);
            if (s.equals("*")) {
                v.setText(R.string.label_no_extension);
            } else {
                v.setText(s.string);
            }
            v.setChecked(s.checked);
            v.setTag(s);
            v.setOnLongClickListener(listener);
            v.setOnCheckedChangeListener(checkedChangeListener);
            view.addView(v);
        }
    }

    @Override
    public void onDestroyView() {
        rootView = null;
        mPrefs.mDirList.remove(mPath);
        mPrefs.savePrefs(mContext);
        mRecentAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_dropdown_item_1line, new ArrayList<>());
        super.onDestroyView();
    }

    private void refreshExtList() {
        setListItem(mExtListView, mPrefs.mExtList, mExtListener, mCheckListener);
    }
}
