package apk.tool.patcher.ui.modules.odex.filechooser;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.gmail.heagoo.common.PathUtil;
import com.gmail.heagoo.common.SDCard;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import apk.tool.patcher.R;

//import android.*;

public class FileSelectDialog extends Dialog implements OnItemClickListener,
        View.OnClickListener {
    private static final String LAST_DIR = "lastDirectory";

    private ResListAdapter fileListAdapter;

    // private ApkInfoActivity activity;
    // In most cases, the extra string is the replaced file path
    private String extraStr;

    // To select a folder or not
    private boolean selectFolder;

    // Should show confirmation dialog or not
    private boolean showConfirmDlg;

    // tag to save last directory
    private String tag;

    // Sub title for current path
    private TextView titleTv;
    private TextView pathTv;

    private CheckBox editCheckBox;

    private IFileSelection callback;

    private Context ctx;

    // extraString should be the replaced file name if used to replace a file
    public FileSelectDialog(Context ctx, IFileSelection callback,
                            String fileSuffix, String extraString, String strTitle) {
        this(ctx, callback, fileSuffix, extraString, strTitle, false, false, false, null);
    }

    // public FileReplaceDialog(ApkInfoActivity activity, String
    // replacedFilePath,
    // String fileSuffix) {
    // this(activity, replacedFilePath, fileSuffix, false);
    // }

    public FileSelectDialog(Context ctx, IFileSelection callback,
                            String fileSuffix, String extraString, String strTitle,
                            boolean selectFolder, boolean showConfirmDlg, boolean showEditOption,
                            String tag) {
        this(ctx, callback,
                fileSuffix, extraString, strTitle,
                selectFolder, showConfirmDlg, showEditOption,
                tag, null);
    }

    // When selectFolder = true, means to select a folder
    // tag is used to differentiate remembered directory
    @SuppressLint("InflateParams")
    public FileSelectDialog(Context ctx, IFileSelection callback,
                            String fileSuffix, String extraString, String strTitle,
                            boolean selectFolder, boolean showConfirmDlg, boolean showEditOption,
                            String tag, String defaultDir) {
        super(ctx);

        super.requestWindowFeature(Window.FEATURE_NO_TITLE);

        this.ctx = ctx;
        this.callback = callback;
        this.extraStr = extraString;
        this.selectFolder = selectFolder;
        this.showConfirmDlg = showConfirmDlg;
        this.tag = tag;

        View view = LayoutInflater.from(ctx).inflate(R.layout.dialog_fileselect, null, false);

        // Not show confirm button when select file
        Button confirmBtn = view.findViewById(R.id.confirm);
        if (selectFolder) {
            confirmBtn.setVisibility(View.VISIBLE);
            confirmBtn.setOnClickListener(this);
        } else {
            confirmBtn.setVisibility(View.GONE);
        }

        // File List view
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
        String key = LAST_DIR;
        if (tag != null) {
            key = tag + "_" + LAST_DIR;
        }
        String lastDir = sp.getString(key, "");
        if (!new File(lastDir).exists()) {
            if (defaultDir == null) {
                lastDir = SDCard.getRootDirectory();
            } else {
                lastDir = defaultDir;
            }
        }

        // Title & sub title
        titleTv = view.findViewById(R.id.tv_title);
        pathTv = view.findViewById(R.id.tv_subtitle);
        if (strTitle == null) {
            if (fileSuffix != null) {
                strTitle = "Select file to replace" + " (" + fileSuffix + ")";
            } else {
                strTitle = "Select file to replace";
            }
        }
        titleTv.setText(strTitle);
        pathTv.setText(lastDir);

        // File list view
        ListView fileList = view.findViewById(R.id.file_list);
        this.fileListAdapter = new ResListAdapter(ctx, null, lastDir, "/",
                new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String filename) {
                        File f = new File(dir, filename);
                        return (f.isDirectory() || isInterestedFile(filename));
                    }
                });
        fileList.setAdapter(fileListAdapter);
        fileList.setOnItemClickListener(this);

        // Checkbox (Edit it before replace)
        this.editCheckBox = view.findViewById(R.id.cb_edit_before_replace);
        if (showEditOption) {
            editCheckBox.setText(
                    String.format("edit %s before replace",
                            PathUtil.getNameFromPath(extraString)));
            editCheckBox.setChecked(getHistoryEditOption());
            editCheckBox.setVisibility(View.VISIBLE);
        } else {
            editCheckBox.setVisibility(View.GONE);
        }

        // Close button
        Button closeBtn = view.findViewById(R.id.close);
        closeBtn.setOnClickListener(this);

        setContentView(view);
    }

    // To decide whether to show the file in the dialog
    private boolean isInterestedFile(String filename) {
        return callback.isInterestedFile(filename, extraStr);
    }

    protected void close() {
        this.dismiss();
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        List<FileRecord> fileList = new ArrayList<>();
        String oldDir = fileListAdapter.getData(fileList);
        FileRecord rec = fileList.get(position);
        if (rec == null) {
            return;
        }

        if (rec.isDir) {
            String targetPath;
            if (rec.fileName.equals("..")) {
                int pos = oldDir.lastIndexOf('/');
                targetPath = oldDir.substring(0, pos);
            } else {
                targetPath = oldDir + "/" + rec.fileName;
            }
            fileListAdapter.openDirectory(targetPath);

            String curPath = fileListAdapter.getData(null);
            pathTv.setText(curPath);

        } else if (!this.selectFolder && isInterestedFile(rec.fileName)) {
            String selectedPath = oldDir + "/" + rec.fileName;
            boolean editSelected = isEditSelected();
            callback.fileSelectedInDialog(selectedPath, extraStr, editSelected);
            // if (!isManifest) {
            //
            // } else {
            // activity.replaceFile(replacedFilePath, selectedPath);
            // activity.setManifestModified();
            // }
            saveLastDirectory(oldDir);
            saveEditOption(editSelected);
            close();
        }
    }

    // Save the directory as the default directory next time
    private void saveLastDirectory(String lastDir) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
        Editor editor = sp.edit();
        String key = LAST_DIR;
        if (tag != null) {
            key = tag + "_" + LAST_DIR;
        }
        editor.putString(key, lastDir);
        editor.apply();
    }

    private boolean getHistoryEditOption() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
        String key = "editBeforeReplace";
        if (tag != null) {
            key = tag + "_" + key;
        }
        return sp.getBoolean(key, false);
    }

    private void saveEditOption(boolean editChecked) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
        Editor editor = sp.edit();
        String key = "editBeforeReplace";
        if (tag != null) {
            key = tag + "_" + key;
        }
        editor.putBoolean(key, editChecked);
        editor.apply();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.close) {
            close();
        } else if (id == R.id.confirm) {
            final String curDir = fileListAdapter.getData(null);

            if (showConfirmDlg) {
                new AlertDialog.Builder(this.ctx)
                        .setTitle("Confirm replace")
                        .setMessage(callback.getConfirmMessage(curDir, extraStr))
                        .setPositiveButton(android.R.string.yes,
                                new OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        callback.fileSelectedInDialog(
                                                curDir, extraStr, isEditSelected());
                                        saveLastDirectory(curDir);
                                        close();
                                    }
                                })
                        .setNegativeButton(android.R.string.cancel, null)
                        .show();
            } else {
                callback.fileSelectedInDialog(curDir, extraStr, isEditSelected());
                saveLastDirectory(curDir);
                close();
            }
        }
    }

    // If the option "Edit the file before replacing" selected
    private boolean isEditSelected() {
        return editCheckBox.isChecked();
    }

    public interface IFileSelection {
        void fileSelectedInDialog(String filePath, String extraStr, boolean openFile);

        // Only show interested file
        boolean isInterestedFile(String filename, String extraStr);

        // For folder replacement, will call it to show the message
        String getConfirmMessage(String filePath, String extraStr);
    }
}
