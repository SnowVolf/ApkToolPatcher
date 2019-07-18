package apk.tool.patcher.ui.modules.odex.filechooser;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gmail.heagoo.common.PathUtil;
import com.gmail.heagoo.common.SDCard;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import apk.tool.patcher.R;

public class ResListAdapter extends BaseAdapter {

    List<FileRecord> fileList = new ArrayList<FileRecord>();
    private WeakReference<Context> ctxRef;
    private int resourceId;
    private String rootPath;
    private String curPath;
    private FilenameFilter filter;


    public ResListAdapter(Context ctx, String apkPath, String curPath,
                          String rootPath, FilenameFilter filter) {
        this.ctxRef = new WeakReference<Context>(ctx);

        this.rootPath = rootPath;
        this.curPath = curPath;
        this.filter = filter;

        resourceId = R.layout.item_file;

        initListData(curPath);
    }

    // Return directory and sub file records
    public String getData(List<FileRecord> records) {
        synchronized (fileList) {
            if (records != null) {
                records.addAll(fileList);
            }
            return curPath;
        }
    }

    private void initListData(String path) {
        synchronized (fileList) {
            listSubFiles(path);
        }
    }


    @SuppressWarnings("unchecked")
    private void listSubFiles(String path) {
        File dir = new File(path);
        boolean isRootDir = path.equals(rootPath);
        File[] subFiles = null;
        if (isRootDir && filter != null) {
            subFiles = dir.listFiles(filter);
        } else {
            subFiles = dir.listFiles();
        }
        if (subFiles != null) {
            fileList.clear();

            for (File f : subFiles) {
                FileRecord fr = new FileRecord();
                fr.fileName = f.getName();
                fr.isDir = f.isDirectory();
                fileList.add(fr);
            }

            // Seems it may throw IllegalArgumentException
            try {
                Collections.sort(fileList, new FilenameComparator());
            } catch (Exception ignored) {
            }

            // In root directory, will not show parent folder
            if (!isRootDir) {
                FileRecord fr = new FileRecord();
                fr.fileName = "..";
                fr.isDir = true;
                fileList.add(0, fr);
            }

            curPath = path;
        }
        // Special case: in the parent path of SD card (like /storage/emulated/0)
        // As on some phones, we cannot access the directory like /storage/emulated
        else if (PathUtil.isParentFolderOf(path, SDCard.getRootDirectory())) {
            fileList.clear();

            SDCard.getRootDirectory();
            FileRecord fr = new FileRecord();
            fr.fileName = PathUtil.getSubFolder(path, SDCard.getRootDirectory());
            fr.isDir = true;
            fileList.add(fr);

            // In root directory, will not show parent folder
            if (!path.equals(rootPath)) {
                fr = new FileRecord();
                fr.fileName = "..";
                fr.isDir = true;
                fr.totalSize = -1;
                fileList.add(0, fr);
            }

            curPath = path;
        }
    }

    @Override
    public int getCount() {
        return fileList.size();
    }

    @Override
    public Object getItem(int position) {
        return fileList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FileRecord rec = fileList.get(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(ctxRef.get()).inflate(resourceId,
                    null);

            viewHolder = new ViewHolder();
            viewHolder.icon = convertView
                    .findViewById(R.id.file_icon);
            viewHolder.filename = convertView
                    .findViewById(R.id.filename);
            viewHolder.desc1 = convertView
                    .findViewById(R.id.detail1);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        String detailInfo = null;
        viewHolder.filename.setText(rec.fileName);
        if (rec.fileName.equals("..")) {
            viewHolder.icon.setImageResource(R.drawable.arrow_up);
        } else if (rec.isDir) {
            viewHolder.icon.setImageResource(R.drawable.folder);
        } else {
            viewHolder.icon.setImageResource(R.drawable.file);
        }

        if (detailInfo != null) {
            viewHolder.desc1.setText(detailInfo);
            viewHolder.desc1.setVisibility(View.VISIBLE);
        } else {
            viewHolder.desc1.setVisibility(View.GONE);
        }

        return convertView;
    }

    // Get entry name by current directory and file name
    private String getEntryName(String curPath, String fileName) {
        String entryName = null;
        if (curPath.equals(rootPath)) {
            entryName = fileName;
        } else {
            int position = rootPath.endsWith("/") ? rootPath.length()
                    : (rootPath.length() + 1);
            entryName = curPath.substring(position) + "/" + fileName;
        }
        return entryName;
    }

    public void openDirectory(String targetPath) {
        // Target path is not correct
        if (rootPath.startsWith(targetPath) && !targetPath.equals(rootPath)) {
            return;
        }
        initListData(targetPath);
        this.notifyDataSetChanged();
    }

    private static class ViewHolder {
        ImageView icon;
        TextView filename;
        TextView desc1;
    }
}
