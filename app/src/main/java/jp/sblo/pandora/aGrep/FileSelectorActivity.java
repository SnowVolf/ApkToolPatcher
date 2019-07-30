package jp.sblo.pandora.aGrep;

import android.app.ActionBar;
import android.app.ListActivity;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import apk.tool.patcher.R;


public class FileSelectorActivity extends ListActivity {
    final public static String INTENT_EXTENSION = "ext";
    final public static String INTENT_FILENAME = "filename";
    final public static String INTENT_FILEPATH = "filepath";

    private String m_strDirPath;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_selector);

        // キャンセルの戻り値の指定
        setResult(RESULT_CANCELED);

//		Bundle extras = getIntent().getExtras();
//		// 拡張子の取得
//		if (extras != null) {
//			extension = (String[]) extras.get(INTENT_EXTENSION);
//		}
        ActionBar actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            Toast.makeText(FileSelectorActivity.this, R.string.label_unable_access, Toast.LENGTH_SHORT).show();
            finish();
        } else {
            // sdcardをカレントにしてリスト構築
            m_strDirPath = Environment.getExternalStorageDirectory().getAbsolutePath();
            fillList();
            Toast.makeText(FileSelectorActivity.this, R.string.label_long_press, Toast.LENGTH_LONG).show();

            ListView lv = getListView();
            lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

                @Override
                public boolean onItemLongClick(AdapterView<?> adapter, View view, int position, long id) {
                    // リストアダプタから現在選択中のファイル名／ディレクトリ名を取得
                    String strItem = (String) getListAdapter().getItem(position);

                    if (strItem.equals("..")) {
                        // ディレクトリを1階層上がる場合
                        if (m_strDirPath.lastIndexOf("/") <= 0) {
                            // ルートから1階層目の場合
                            m_strDirPath = m_strDirPath.substring(0, m_strDirPath
                                    .lastIndexOf("/") + 1);
                        } else {
                            // ルートから2階層目以上の場合
                            m_strDirPath = m_strDirPath.substring(0, m_strDirPath
                                    .lastIndexOf("/"));
                        }
                        fillList();
                    } else {
                        // 応答に設定
                        setResult(RESULT_OK, getIntent().putExtra(INTENT_FILENAME, strItem)
                                .putExtra(INTENT_FILEPATH, m_strDirPath + "/" + strItem));
                        // アクティビティの終了
                        finish();
                    }
                    return true;
                }

            });

        }

    }


    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        // リストアダプタから現在選択中のファイル名／ディレクトリ名を取得
        String strItem = (String) getListAdapter().getItem(position);

        if (strItem.equals("..")) {
            // ディレクトリを1階層上がる場合
            if (m_strDirPath.lastIndexOf("/") <= 0) {
                // ルートから1階層目の場合
                m_strDirPath = m_strDirPath.substring(0, m_strDirPath
                        .lastIndexOf("/") + 1);
            } else {
                // ルートから2階層目以上の場合
                m_strDirPath = m_strDirPath.substring(0, m_strDirPath
                        .lastIndexOf("/"));
            }
            fillList();
        } else if (strItem.substring(strItem.length() - 1).equals("/")) {
            // ディレクトリに入る場合
            if (m_strDirPath.equals("/")) {
                // ルートの場合
                m_strDirPath += strItem;
            } else {
                // ルートから1階層目以上の場合
                m_strDirPath = m_strDirPath + "/" + strItem;
            }
            m_strDirPath = m_strDirPath.substring(0, m_strDirPath.length() - 1);
            fillList();
        } else {
//			// ファイルの場合は応答に設定
//			setResult(RESULT_OK, getIntent().putExtra(INTENT_FILENAME, strItem)
//					.putExtra(INTENT_FILEPATH, m_strDirPath + "/" + strItem));
//			// アクティビティの終了
//			finish();
        }
    }


    // ファイルリスト構築
    private void fillList() {
        File[] files = new File(m_strDirPath).listFiles();
        if (files == null) {
            Toast.makeText(FileSelectorActivity.this, R.string.label_unable_access, Toast.LENGTH_SHORT).show();
            return;
        }

        // ディレクトリ→大文字小文字無視で名前順になるようにソート
        Arrays.sort(files, new Comparator<File>() {
            @Override
            public int compare(File object1, File object2) {
                final boolean isdir1 = object1.isDirectory();
                final boolean isdir2 = object2.isDirectory();

                if (isdir1 ^ isdir2) {
                    if (isdir1) {
                        return -1;
                    } else {
                        return 1;
                    }
                }
                return object1.getName().compareToIgnoreCase(object2.getName());
            }
        });

        // カレントディレクトリ名をTextViewに設定
        TextView txtDirName = findViewById(R.id.txtDirName);
        txtDirName.setText(m_strDirPath);

        ArrayList<String> items = new ArrayList<String>();

        // ルートじゃない場合は、階層を上がれるように".."をArrayListの先頭に設定
        if (!m_strDirPath.equals("/")) {
            items.add("..");
        }

        // ディレクトリのみArrayListに追加する
        for (File file : files) {
            if (file.isDirectory()) {
                items.add(file.getName() + "/");
            }
        }

        // ArrayListをListActivityに設定する
        ArrayAdapter<String> fileList = new ArrayAdapter<String>(this,
                R.layout.item_file_row, items);
        setListAdapter(fileList);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

}
