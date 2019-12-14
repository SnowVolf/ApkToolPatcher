package apk.tool.patcher.entity.async;


import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import apk.tool.patcher.App;
import apk.tool.patcher.R;
import apk.tool.patcher.util.PathF;

@SuppressWarnings("WeakerAccess")
public enum GetIcon {
    INSTANCE;

    private static final int MAX_CACHE_SIZE = 512;
    private boolean allowThumbnails = true;
    private HashSet assocAll = new HashSet();
    private HashMap<String, Drawable> cache = new HashMap<>();
    private boolean disableBackground;
    List imageExt = Arrays.asList("jpg", "jpeg", "png", "bmp", "gif");
    private MimeTypeMap mime = MimeTypeMap.getSingleton();
    private PackageManager packMan = App.get().getPackageManager();
    private long recreateMainActivityTime;

    public enum FileType {
        NORMAL,
        APK,
        IMAGE
    }

    class RetrieveFileIcon extends AsyncTask<Void, Void, Drawable> {
        ImageView imageView;
        String origName;
        FileType type;

        RetrieveFileIcon(String origName, ImageView imageView, FileType type) {
            this.origName = origName;
            this.imageView = imageView;
            this.type = type;
        }

        @Override
        protected Drawable doInBackground(Void[] params) {
            if (GetIcon.this.disableBackground) {
                return null;
            }
            @SuppressWarnings("WrongThread") String tag = (String) this.imageView.getTag();
            if (!tag.equals(this.origName)) {
                return null;
            }
            if (this.type == FileType.APK) {
                return GetIcon.this.getApkIcon(this.origName);
            }
            return this.type == FileType.IMAGE ? GetIcon.this.getImageIcon(this.origName) : null;
        }

        protected void onPostExecute(Drawable drawable) {
            if (drawable != null) {
                if (GetIcon.this.cache.size() > GetIcon.MAX_CACHE_SIZE) {
                    GetIcon.this.cache.clear();
                }
                if (!GetIcon.this.disableBackground) {
                    GetIcon.this.cache.put(this.origName, drawable);
                }
                if (this.imageView.getTag().equals(this.origName)) {
                    this.imageView.setImageDrawable(drawable);
                }
            }
        }

    }

    public static GetIcon getInstance() {
        return INSTANCE;
    }

    private Drawable getApkIcon(String str) {
        Drawable drawable = null;
        PackageInfo packageArchiveInfo = this.packMan.getPackageArchiveInfo(str, PackageManager.GET_META_DATA);
        if (packageArchiveInfo != null) {
            ApplicationInfo applicationInfo = packageArchiveInfo.applicationInfo;
            applicationInfo.sourceDir = str;
            applicationInfo.publicSourceDir = str;
            try {
                drawable = applicationInfo.loadIcon(this.packMan);
            } catch (Exception ignored) {
            }
        }
        return drawable;
    }

    private Drawable getImageIcon(String origName) {
        int ratio = App.dpToPx(48);
        Options options = new Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(origName, options);
        options.inSampleSize = 1;
        while (options.outHeight >= 192 && options.outWidth >= 192) {
            options.outHeight /= 2;
            options.outWidth /= 2;
            options.inSampleSize *= 2;
        }
        options.inJustDecodeBounds = false;
        Bitmap decodeFile = BitmapFactory.decodeFile(origName, options);
        if (decodeFile == null) {
            return null;
        }
        try {
            return new BitmapDrawable(App.get().getResources(), ThumbnailUtils.extractThumbnail(decodeFile, ratio, ratio));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public void backgroundLoadIcon(String filePath, ImageView imageView, FileType fileType) {
        getIcon(true, filePath, filePath, imageView);
        new RetrieveFileIcon(filePath, imageView, fileType).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
    }

    public void setDisableBackground(boolean disableBackground) {
        this.disableBackground = disableBackground;
        if (System.currentTimeMillis() - this.recreateMainActivityTime < 3000) {
            this.disableBackground = false;
        }
    }

    public void onActivityRecreate() {
        this.recreateMainActivityTime = System.currentTimeMillis();
    }

    public void setConfig() {
        if (!this.allowThumbnails) {
            this.cache.clear();
        }
        this.allowThumbnails = true;
    }

    public void resolve(String filePath, ImageView holderView) {
        holderView.setTag(filePath);
        String fileExt = PathF.getExt(filePath);
        FileType fileType = FileType.NORMAL;
        if (fileExt.equals("apk")) {
            fileType = FileType.APK;
        } else if (this.imageExt.contains(fileExt)) {
            fileType = FileType.IMAGE;
        }

        backgroundLoadIcon(filePath, holderView, fileType);
    }

    public void getIcon(boolean curDirNull, String fileName, String filePath, ImageView imageView) {
        String fileExt = PathF.getExt(fileName).toLowerCase();
        if (fileExt.length() == 0) {
            imageView.setImageResource(R.drawable.file);
            return;
        }
        FileType fileType = FileType.NORMAL;
        if (fileExt.equals("apk")) {
            fileType = FileType.APK;
        } else if (this.imageExt.contains(fileExt)) {
            fileType = FileType.IMAGE;
        }
        Drawable drawable;
        if (curDirNull || !this.allowThumbnails || fileType == FileType.NORMAL) {
            drawable = this.cache.get(fileExt);
            if (drawable != null) {
                imageView.setImageDrawable(drawable);
                return;
            } else if (this.cache.containsKey(fileExt)) {
                imageView.setImageResource(R.drawable.file);
                return;
            } else {
                String mimeTypeFromExtension = this.mime.getMimeTypeFromExtension(PathF.getExt(fileName).toLowerCase());
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(new File(fileName)), mimeTypeFromExtension);
                List queryIntentActivities = this.packMan.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                int iconId = -1;
                for (int i = 0; i < queryIntentActivities.size(); i++) {
                    ResolveInfo resolveInfo = (ResolveInfo) queryIntentActivities.get(i);
                    if ((iconId == -1 || (resolveInfo.activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) && !this.assocAll.contains(resolveInfo.activityInfo.applicationInfo.packageName)) {
                        iconId = i;
                    }
                }
                if (iconId != -1) {
                    drawable = ((ResolveInfo) queryIntentActivities.get(iconId)).loadIcon(this.packMan);
                } else {
                    drawable = null;
                }
                if (this.cache.size() > MAX_CACHE_SIZE) {
                    this.cache.clear();
                }
                if (!this.disableBackground) {
                    this.cache.put(fileExt, drawable);
                }
                if (drawable == null) {
                    imageView.setImageResource(R.drawable.file);
                    return;
                } else {
                    imageView.setImageDrawable(drawable);
                    return;
                }
            }
        }
        drawable = this.cache.get(filePath);
        if (drawable != null) {
            imageView.setImageDrawable(drawable);
        } else {
            backgroundLoadIcon(filePath, imageView, fileType);
        }
    }
}