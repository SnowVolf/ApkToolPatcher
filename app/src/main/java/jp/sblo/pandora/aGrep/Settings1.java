package jp.sblo.pandora.aGrep;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import apk.tool.patcher.R;

public class Settings1 extends Activity {

    final static int REQUEST_CODE_ADDDIC = 0x1001;

    private Prefs mPrefs;
    private LinearLayout mDirListView;
    private LinearLayout mExtListView;
    private View.OnLongClickListener mDirListener;
    private View.OnLongClickListener mExtListener;
    private CompoundButton.OnCheckedChangeListener mCheckListener;
    private ArrayAdapter<String> mRecentAdapter;
    private Context mContext;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;

        mPrefs = Prefs.loadPrefes(this);

        setContentView(R.layout.activity_main_patcher);

        //mDirListView = (LinearLayout)findViewById(R.id.listdir);
        mExtListView = (LinearLayout) findViewById(R.id.listext);

        mDirListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                final CheckedString strItem = (CheckedString) view.getTag();
                // Show Dialog
                new AlertDialog.Builder(mContext)
                        .setTitle(R.string.label_remove_item_title)
                        .setMessage(getString(R.string.label_remove_item, strItem))
                        .setPositiveButton(R.string.label_OK, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                mPrefs.mDirList.remove(strItem);
                                refreshDirList();
                                mPrefs.savePrefs(mContext);
                            }
                        })
                        .setNegativeButton(R.string.label_CANCEL, null)
                        .setCancelable(true)
                        .show();
                return true;
            }
        };

        mExtListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                final String strText = (String) ((TextView) view).getText();
                final CheckedString strItem = (CheckedString) view.getTag();
                // Show Dialog
                new AlertDialog.Builder(mContext)
                        .setTitle(R.string.label_remove_item_title)
                        .setMessage(getString(R.string.label_remove_item, strText))
                        .setPositiveButton(R.string.label_OK, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                mPrefs.mExtList.remove(strItem);
                                refreshExtList();
                                mPrefs.savePrefs(mContext);
                            }
                        })
                        .setNegativeButton(R.string.label_CANCEL, null)
                        .setCancelable(true)
                        .show();
                return true;
            }
        };

        mCheckListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                final CheckedString strItem = (CheckedString) buttonView.getTag();
                strItem.checked = isChecked;
                mPrefs.savePrefs(mContext);
            }
        };

        refreshDirList();
        refreshExtList();

        Button btnAddDir = (Button) findViewById(R.id.adddir);
        Button btnAddExt = (Button) findViewById(R.id.addext);

        btnAddDir.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // ファイル選択画面呼び出し
                Intent intent = new Intent(mContext, FileSelectorActivity.class);
                startActivityForResult(intent, REQUEST_CODE_ADDDIC);
            }
        });

        btnAddExt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // Create EditText
                final EditText edtInput = new EditText(mContext);
                edtInput.setSingleLine();
                // Show Dialog
                new AlertDialog.Builder(mContext)
                        .setTitle(R.string.label_addext)
                        .setView(edtInput)
                        .setPositiveButton(R.string.label_OK, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
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
                            }
                        })
                        .setNeutralButton(R.string.label_no_extension, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
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
                            }
                        })
                        .setNegativeButton(R.string.label_CANCEL, null)
                        .setCancelable(true)
                        .show();
            }
        });


        final CheckBox chkRe = (CheckBox) findViewById(R.id.checkre);
        final CheckBox chkIc = (CheckBox) findViewById(R.id.checkignorecase);

        chkRe.setChecked(mPrefs.mRegularExrpression);
        chkIc.setChecked(mPrefs.mIgnoreCase);

        chkRe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPrefs.mRegularExrpression = chkRe.isChecked();
                mPrefs.savePrefs(mContext);
            }
        });
        chkIc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPrefs.mIgnoreCase = chkIc.isChecked();
                mPrefs.savePrefs(mContext);
            }
        });

        final AutoCompleteTextView edittext = (AutoCompleteTextView) findViewById(R.id.EditText01);
        edittext.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                    String text = edittext.getEditableText().toString();
                    Intent it = new Intent(mContext, Search.class);
                    it.setAction(Intent.ACTION_SEARCH);
                    it.putExtra(SearchManager.QUERY, text);
                    startActivity(it);
                    return true;
                }
                return false;
            }
        });
        mRecentAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_dropdown_item_1line, new ArrayList<String>());
        edittext.setAdapter(mRecentAdapter);

//        ImageButton clrBtn = (ImageButton) findViewById(R.id.ButtonClear);
//        clrBtn.setOnClickListener(new OnClickListener() {
//            public void onClick(View view)
//            {
//                edittext.setText("");
//                edittext.requestFocus();
//            }
//        });
//
//        ImageButton searchBtn = (ImageButton) findViewById(R.id.ButtonSearch);
//        searchBtn.setOnClickListener(new OnClickListener() {
//            public void onClick(View view)
//            {
//                String text = edittext.getText().toString();
//                Intent it = new Intent(mContext,Search.class);
//                it.setAction(Intent.ACTION_SEARCH);
//                it.putExtra(SearchManager.QUERY,text );
//                startActivity( it );
//            }
//        });
//
//        ImageButton historyBtn = (ImageButton) findViewById(R.id.ButtonHistory);
//        historyBtn.setOnClickListener(new OnClickListener() {
//            public void onClick(View view)
//            {
//                edittext.showDropDown();
//            }
//        });
//
//    }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        // ディレクトリ選択画面からの応答
        if (requestCode == REQUEST_CODE_ADDDIC && resultCode == RESULT_OK && data != null) {
            final String dirname = data.getExtras().getString(FileSelectorActivity.INTENT_FILEPATH);
            if (dirname != null && dirname.length() > 0) {
                // 二重チェック
                for (CheckedString t : mPrefs.mDirList) {
                    if (t.string.equalsIgnoreCase(dirname)) {
                        return;
                    }
                }
                mPrefs.mDirList.add(new CheckedString(dirname));
                refreshDirList();
                mPrefs.savePrefs(mContext);
            }
        }

    }

    void setListItem(LinearLayout view,
                     ArrayList<CheckedString> list,
                     View.OnLongClickListener logclicklistener,
                     CompoundButton.OnCheckedChangeListener checkedChangeListener) {
        view.removeAllViews();
        Collections.sort(list, new Comparator<CheckedString>() {
            @Override
            public int compare(CheckedString object1, CheckedString object2) {
                return object1.string.compareToIgnoreCase(object2.string);
            }
        });
        for (CheckedString s : list) {
            CheckBox v = (CheckBox) View.inflate(this, R.layout.list_dir, null);
            if (s.equals("*")) {
                v.setText(R.string.label_no_extension);
            } else {
                v.setText(s.string);
            }
            v.setChecked(s.checked);
            v.setTag(s);
            v.setOnLongClickListener(logclicklistener);
            v.setOnCheckedChangeListener(checkedChangeListener);
            view.addView(v);
        }
    }

    private void refreshDirList() {
        setListItem(mDirListView, mPrefs.mDirList, mDirListener, mCheckListener);
    }

    private void refreshExtList() {
        setListItem(mExtListView, mPrefs.mExtList, mExtListener, mCheckListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_patcher, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_option) {
            Intent intent = new Intent(this, OptionActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        final List<String> recent = mPrefs.getRecent(mContext);
        mRecentAdapter.clear();
        mRecentAdapter.addAll(recent);
        mRecentAdapter.notifyDataSetChanged();
    }
}
