package jp.sblo.pandora.aGrep;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import org.mozilla.universalchardet.UniversalDetector;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import apk.tool.patcher.R;
import apk.tool.patcher.ui.modules.base.BaseActivity;
import apk.tool.patcher.ui.modules.main.MainActivity;


@SuppressLint("DefaultLocale")
public class Search extends BaseActivity implements GrepView.Callback {
    private Toolbar mToolbar;
    private GrepView mGrepView;
    private GrepView.GrepAdapter mAdapter;
    private ArrayList<GrepView.Data> mData;
    private GrepTask mTask;
    private String mQuery;
    private Pattern mPattern;

    private Prefs mPrefs;

    static public String escapeMetaChar(String pattern) {
        final String metachar = ".^${}[]*+?|()\\";

        StringBuilder newpat = new StringBuilder();

        int len = pattern.length();

        for (int i = 0; i < len; i++) {
            char c = pattern.charAt(i);
            if (metachar.indexOf(c) >= 0) {
                newpat.append('\\');
            }
            newpat.append(c);
        }
        return newpat.toString();
    }

    static public String convertOrPattern(String pattern) {
        if (pattern.contains(" ")) {
            return "(" + pattern.replace(" ", "|") + ")";
        } else {
            return pattern;
        }
    }

    public static SpannableString highlightKeyword(CharSequence text, Pattern p, int fgcolor, int bgcolor) {
        SpannableString ss = new SpannableString(text);

        int start = 0;
        int end;
        Matcher m = p.matcher(text);
        while (m.find(start)) {
            start = m.start();
            end = m.end();

            BackgroundColorSpan bgspan = new BackgroundColorSpan(bgcolor);
            ss.setSpan(bgspan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            ForegroundColorSpan fgspan = new ForegroundColorSpan(fgcolor);
            ss.setSpan(fgspan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            start = end;
        }
        return ss;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPrefs = Prefs.loadPrefes(this);

        setContentView(R.layout.activity_search_result);

        if (mPrefs.mDirList.size() == 0) {
            Toast.makeText(getApplicationContext(), R.string.label_no_target_dir, Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mGrepView = (GrepView) findViewById(R.id.DicView01);
        mData = new ArrayList<GrepView.Data>();
        mAdapter = new GrepView.GrepAdapter(getApplicationContext(), R.layout.list_row, R.id.DicView01, mData);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View p1) {
                finish();
            }
        });
        mGrepView.setAdapter(mAdapter);
        mGrepView.setCallback(this);

        Intent it = getIntent();

        if (it != null &&
                Intent.ACTION_SEARCH.equals(it.getAction())) {
            Bundle extras = it.getExtras();
            mQuery = extras.getString(SearchManager.QUERY);

            if (mQuery != null && mQuery.length() > 0) {

                mPrefs.addRecent(this, mQuery);

                String patternText = mQuery;
                if (!mPrefs.mRegularExrpression) {
                    patternText = escapeMetaChar(patternText);
                    patternText = convertOrPattern(patternText);
                }

                if (mPrefs.mIgnoreCase) {
//                        mPatternText = text.toLowerCase();
                    mPattern = Pattern.compile(patternText, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.MULTILINE);
                } else {
//                        mPatternText = text;
                    mPattern = Pattern.compile(patternText);
                }

                if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                    mData.removeAll(mData);
                    mAdapter.setFormat(mPattern, mPrefs.mHighlightFg, mPrefs.mHighlightBg, mPrefs.mFontSize);
                    mTask = new GrepTask();
                    mTask.execute(mQuery);

                }
            } else {
                finish();
            }
        }
    }

    @Override
    public void onGrepItemClicked(int position) {
        GrepView.Data data = (GrepView.Data) mGrepView.getAdapter().getItem(position);

        Intent it = new Intent(this, TextViewer.class);

        it.putExtra(TextViewer.EXTRA_PATH, data.mFile.getAbsolutePath());
        it.putExtra(TextViewer.EXTRA_QUERY, mQuery);
        it.putExtra(TextViewer.EXTRA_LINE, data.mLinenumber);

        startActivity(it);
    }

    @Override
    public boolean onGrepItemLongClicked(int position) {
        return false;
    }

    public boolean onMenuItem(int featureId, MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    class GrepTask extends AsyncTask<String, GrepView.Data, Boolean> {
        private ProgressDialog mProgressDialog;
        private int mFileCount = 0;
        private int mFoundcount = 0;
        private boolean mCancelled;

        @Override
        protected void onPreExecute() {
            mCancelled = false;
            mProgressDialog = new ProgressDialog(Search.this);
            mProgressDialog.setTitle(R.string.grep_spinner);
            mProgressDialog.setMessage(mQuery);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setCancelable(true);
            mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

                @Override
                public void onCancel(DialogInterface dialog) {
                    mCancelled = true;
                    cancel(false);
                }
            });
            mProgressDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            return grepRoot(params[0]);
        }


        @Override
        protected void onPostExecute(Boolean result) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
            synchronized (mData) {
                Collections.sort(mData, new GrepView.Data());
                mAdapter.notifyDataSetChanged();
            }
            mGrepView.setSelection(0);
            Toast.makeText(getApplicationContext(), result ? R.string.grep_finished : R.string.grep_canceled, Toast.LENGTH_LONG).show();
            mData = null;
            mAdapter = null;
            mTask = null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            onPostExecute(false);
        }

        @Override
        protected void onProgressUpdate(GrepView.Data... progress) {
            if (isCancelled()) {
                return;
            }
            mProgressDialog.setMessage(Search.this.getString(R.string.progress, mQuery, mFileCount));
            if (progress != null) {
                synchronized (mData) {
                    for (GrepView.Data data : progress) {
                        mData.add(data);
                    }
                    mAdapter.notifyDataSetChanged();
                    mGrepView.setSelection(mData.size() - 1);
                }
            }
        }


        boolean grepRoot(String text) {
            for (CheckedString dir : mPrefs.mDirList) {
                if (dir.checked && !grepDirectory(new File(dir.string))) {


                    return false;
                }
            }
            return true;
        }

        boolean grepDirectory(File dir) {
            if (isCancelled()) {
                return false;
            }
            if (dir == null) {
                return false;
            }

            File[] flist = dir.listFiles();

            if (flist != null) {
                for (File f : flist) {
                    boolean res = false;
                    if (f.isDirectory()) {
                        res = grepDirectory(f);
                    } else {
                        res = grepFile(f);
                    }
                    if (!res) {
                        return false;
                    }
                }
            }
            return true;
        }


        boolean grepFile(File file) {
            if (isCancelled()) {
                return false;
            }
            if (file == null) {
                return false;
            }

            boolean extok = false;
            for (CheckedString ext : mPrefs.mExtList) {
                if (ext.checked) {
                    if (ext.string.equals("*")) {
                        if (file.getName().indexOf('.') == -1) {
                            extok = true;
                            break;
                        }
                    } else if (file.getName().toLowerCase().endsWith("." + ext.string.toLowerCase())) {
                        extok = true;
                        break;
                    }
                }
            }
            if (!extok) {
                return true;
            }

            InputStream is;
            try {
                is = new BufferedInputStream(new FileInputStream(file), 65536);
                is.mark(65536);

                //  文字コードの判定
                String encode = null;
                try {
                    UniversalDetector detector = new UniversalDetector();
                    try {
                        int nread;
                        byte[] buff = new byte[4096];
                        if ((nread = is.read(buff)) > 0) {
                            detector.handleData(buff, 0, nread);
                        }
                        detector.dataEnd();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        is.close();
                        return true;
                    } catch (IOException e) {
                        e.printStackTrace();
                        is.close();
                        return true;
                    }
                    encode = detector.getCharset();
                    detector.reset();
                    detector.destroy();
                } catch (UniversalDetector.DetectorException e) {
                }
                is.reset();

                BufferedReader br = null;
                try {
                    if (encode != null) {
                        br = new BufferedReader(new InputStreamReader(is, encode), 8192);

                    } else {
                        br = new BufferedReader(new InputStreamReader(is), 8192);
                    }

                    String text;
                    int line = 0;
                    boolean found = false;
                    Pattern pattern = mPattern;
                    Matcher m = null;
                    ArrayList<GrepView.Data> data = null;
                    mFileCount++;
                    while ((text = br.readLine()) != null) {
                        line++;
                        if (m == null) {
                            m = pattern.matcher(text);
                        } else {
                            m.reset(text);
                        }
                        if (m.find()) {
                            found = true;

                            synchronized (mData) {
                                mFoundcount++;
                                if (data == null) {
                                    data = new ArrayList<GrepView.Data>();
                                }
                                data.add(new GrepView.Data(file, line, text));

                                if (mFoundcount < 10) {
                                    publishProgress(data.toArray(new GrepView.Data[0]));
                                    data = null;
                                }
                            }
                            if (mCancelled) {
                                break;
                            }
                        }
                    }
                    br.close();
                    is.close();
                    if (data != null) {
                        publishProgress(data.toArray(new GrepView.Data[0]));
                        data = null;
                    }
                    if (!found) {
                        if (mFileCount % 10 == 0) {
                            publishProgress((GrepView.Data[]) null);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return true;
        }
    }
}
