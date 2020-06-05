package apk.tool.patcher.filesystem;


import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Locale;

import apk.tool.patcher.App;
import apk.tool.patcher.R;
import apk.tool.patcher.ui.modules.main.MainActivity;
import apk.tool.patcher.util.Cs;
import apk.tool.patcher.util.PathF;
import apk.tool.patcher.util.SystemF;
import ru.svolf.melissa.compat.DocumentsContractCompat;
import ru.svolf.melissa.compat.ParcelFileDescriptorCompat;

@SuppressWarnings("WeakerAccess")
public class ExFile extends File {
    static final String KEY_EXT_CARD_URI = "card_uri";
    private static String lastExtCardPath = null;
    private static String lastNonExtCardPath = null;
    private static long lastReadTime = 0;
    private static final long serialVersionUID = 1;

    public ExFile(String pathname) {
        super(pathname);
    }

    public ExFile(File parent, String child) {
        super(parent, child);
    }

    public static boolean requireElevation(String dir) {
        return requireElevation(new String[]{dir});
    }

    public static boolean requireElevation(String[] dirs) {
        if (loadCardUri() != null) {
            return false;
        }
        for (String isWritable : dirs) {
            if (!isWritable(isWritable)) {
                return true;
            }
        }
        return false;
    }

    public static void elevate(AppCompatActivity activity, int resultInt) {
        try {
            Intent intent = new Intent(activity, ExFileInfo.class);
            intent.putExtra(Cs.EXTRA_RESULT_INT, resultInt);
            activity.startActivityForResult(intent, Cs.REQ_CODE_EXFILEINFO);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(activity, App.bindString(R.string.no_app_to_open, "ACTION_OPEN_DOCUMENT_TREE"), Toast.LENGTH_SHORT).show();
        }
    }

    private static Uri loadCardUri() {
        Uri uri = null;
        long currentTimeMillis = System.currentTimeMillis();
        if (App.get().extCardUri != null && currentTimeMillis - lastReadTime < Cs.MAX_TIME_MILLIS) {
            return App.get().extCardUri;
        }
        lastReadTime = currentTimeMillis;
        String cardUri = SystemF.getExtCardSettingsString(KEY_EXT_CARD_URI);
        if (cardUri == null) {
            return null;
        }
        Uri parse = Uri.parse(cardUri);
        if (validateUri(parse)) {
            uri = parse;
        }
        App.get().extCardUri = uri;
        return uri;
    }

    static boolean validateUri(Uri uri) {
        String treeDocumentId;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            treeDocumentId = DocumentsContract.getTreeDocumentId(uri);
        } else {
            treeDocumentId = DocumentsContractCompat.getTreeDocumentId(uri);
        }
        String extCardSettingsName = SystemF.getExtCardSettingsName();
        if (extCardSettingsName == null) {
            return false;
        }
        String docIdToPath = docIdToPath(treeDocumentId);
        if (docIdToPath == null) {
            return false;
        }
        docIdToPath = PathF.addEndSlash(docIdToPath);
        if (docIdToPath.length() >= extCardSettingsName.length()) {
            return false;
        }
        if (treeDocumentId.indexOf(58) != treeDocumentId.length() - 1) {
            treeDocumentId = treeDocumentId + PathF.SPATHSEPARATOR;
        }
        try {
            Uri uri1;
            String documentId = treeDocumentId + extCardSettingsName.substring(docIdToPath.length());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                uri1 = DocumentsContract.buildDocumentUriUsingTree(uri, documentId);
            } else {
                uri1 = DocumentsContractCompat.buildDocumentUriUsingTree(uri, documentId);
            }
            ParcelFileDescriptor fileDescriptor = App.get().getContentResolver().openFileDescriptor(uri1, "rw");
            if (fileDescriptor != null) fileDescriptor.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static void storeCardUri(Uri uri, int flags) {
        App.get().extCardUri = uri;
        SystemF.addExtCardSettingsString(KEY_EXT_CARD_URI, uri == null ? null : uri.toString());
        if (uri != null) {
            int flagss = flags & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            //noinspection WrongConstant
            App.get().getContentResolver().takePersistableUriPermission(uri, flagss);
        }
    }

    private static String docIdToPath(String str) {
        String[] split = str.split(":");
        String str2 = split.length < 2 ? "" : split[1];
        String path = ExternalCard.getPath(false);
        if (path == null) {
            return null;
        }
        if (str2.length() == 0) {
            return path;
        }
        return PathF.addEndSlash(path) + str2;
    }

    private static Uri pathToUri(String str) {
        Uri loadCardUri = loadCardUri();
        if (loadCardUri != null) {
            String treeDocumentId;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                treeDocumentId = DocumentsContract.getTreeDocumentId(loadCardUri);
            } else {
                treeDocumentId = DocumentsContractCompat.getTreeDocumentId(loadCardUri);
            }
            String docIdToPath = docIdToPath(treeDocumentId);
            if (docIdToPath == null) {
                return null;
            }
            if (docIdToPath.equalsIgnoreCase(str)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    return DocumentsContract.buildDocumentUriUsingTree(loadCardUri, treeDocumentId);
                } else {
                    return DocumentsContractCompat.buildDocumentUriUsingTree(loadCardUri, treeDocumentId);
                }
            }
            docIdToPath = PathF.addEndSlash(docIdToPath);
            if (str.toLowerCase().startsWith(docIdToPath.toLowerCase())) {
                if (treeDocumentId.indexOf(58) != treeDocumentId.length() - 1) {
                    treeDocumentId = treeDocumentId + PathF.SPATHSEPARATOR;
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    return DocumentsContract.buildDocumentUriUsingTree(loadCardUri, treeDocumentId + str.substring(docIdToPath.length()));
                } else {
                    return DocumentsContractCompat.buildDocumentUriUsingTree(loadCardUri, treeDocumentId + str.substring(docIdToPath.length()));
                }
            }
            storeCardUri(null, 0);
            return null;
        }
        return null;
    }

    public static OutputStream getOutputStream(File file) throws FileNotFoundException {
        String absolutePath = file.getAbsolutePath();
        String removeNameFromPath = PathF.removeNameFromPath(absolutePath);
        if (!isWritable(absolutePath)) {
            Uri pathToUri = pathToUri(removeNameFromPath);
            if (pathToUri != null) {
                OutputStream openOutputStream;
                if (file.exists()) {
                    //noinspection ResultOfMethodCallIgnored
                    new ExFile(absolutePath).delete();
                }
                ContentResolver contentResolver = App.get().getContentResolver();
                String pointToName = PathF.pointToName(absolutePath);
                String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(PathF.getExt(pointToName).toLowerCase(Locale.US));
                if (mimeType == null) {
                    mimeType = "application/octet-stream";
                }
                try {
                    Uri createDocument = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        createDocument = DocumentsContract.createDocument(contentResolver, pathToUri, mimeType, pointToName);
                    }
                    openOutputStream = createDocument != null ? contentResolver.openOutputStream(createDocument) : null;
                } catch (Exception e) {
                    openOutputStream = null;
                }
                if (openOutputStream == null) {
                    return new FileOutputStream(file);
                }
                return openOutputStream;
            }
        }
        return new FileOutputStream(file);
    }

    private static boolean isWritable(String str) {
        String removeNameFromPath = PathF.removeNameFromPath(str);
        if (lastExtCardPath != null && lastExtCardPath.equals(removeNameFromPath)) {
            return false;
        }
        if (lastNonExtCardPath != null && lastNonExtCardPath.equals(removeNameFromPath)) {
            return true;
        }
        if (ExternalCard.isPathWritableBeforeElevation(str)) {
            lastNonExtCardPath = removeNameFromPath;
            return true;
        }
        lastExtCardPath = removeNameFromPath;
        return false;
    }

    public static int createExFile(String str) {
        int result = -1;
        ExFile exFile = new ExFile(str);
        String absolutePath = exFile.getAbsolutePath();
        String removeNameFromPath = PathF.removeNameFromPath(absolutePath);
        if (!isWritable(absolutePath)) {
            Uri pathToUri = pathToUri(removeNameFromPath);
            if (pathToUri != null) {
                if (exFile.exists()) {
                    //noinspection ResultOfMethodCallIgnored
                    exFile.delete();
                }
                ContentResolver contentResolver = App.get().getContentResolver();
                String pointToName = PathF.pointToName(str);
                String mimeTypeFromExtension = MimeTypeMap.getSingleton().getMimeTypeFromExtension(PathF.getExt(pointToName).toLowerCase(Locale.US));
                if (mimeTypeFromExtension == null) {
                    mimeTypeFromExtension = "application/octet-stream";
                }
                try {
                    Uri createDocument = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        createDocument = DocumentsContract.createDocument(contentResolver, pathToUri, mimeTypeFromExtension, pointToName);
                    } else {

                    }
                    if (createDocument != null) {
                        ParcelFileDescriptor parcelFileDescriptor = contentResolver.openFileDescriptor(createDocument, "rw");
                        if (parcelFileDescriptor != null) {
                            result = ParcelFileDescriptorCompat.detachFdSupport(parcelFileDescriptor);
                        }
                    }
                } catch (Exception ignored) {
                }
            }
        }
        return result;
    }

    public static int openExFile(String str) {
        int result = -1;
        String absolutePath = new File(str).getAbsolutePath();
        if (!isWritable(absolutePath)) {
            Uri pathToUri = pathToUri(absolutePath);
            if (pathToUri != null) {
                try {
                    ParcelFileDescriptor parcelFileDescriptor = App.get().getContentResolver().openFileDescriptor(pathToUri, "rw");
                    if (parcelFileDescriptor != null) {
                        result = ParcelFileDescriptorCompat.detachFdSupport(parcelFileDescriptor);
                    }
                } catch (Exception ignored) {
                }
            }
        }
        return result;
    }

    public File[] listFiles() {
        File[] listFiles = super.listFiles();
        if (listFiles == null) {
            return null;
        }
        File[] fileArr = new ExFile[listFiles.length];
        for (int i = 0; i < listFiles.length; i++) {
            fileArr[i] = new ExFile(listFiles[i].getAbsolutePath());
        }
        return fileArr;
    }

    public boolean delete() {
        String absolutePath = getAbsolutePath();
        if (!isWritable(absolutePath)) {
            Uri pathToUri = pathToUri(absolutePath);
            if (pathToUri != null) {
                try {
                    DocumentsContract.deleteDocument(App.get().getContentResolver(), pathToUri);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                return !exists();
            }
        }
        return super.delete();
    }

    public boolean mkdir() {
        if (exists()) {
            return true;
        }
        String absolutePath = getAbsolutePath();
        String removeNameFromPath = PathF.removeNameFromPath(absolutePath);
        if (!isWritable(absolutePath)) {
            Uri pathToUri = pathToUri(removeNameFromPath);
            if (pathToUri != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    try {
                        DocumentsContract.createDocument(App.get().getContentResolver(), pathToUri, "vnd.android.document/directory", PathF.pointToName(absolutePath));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                return exists();
            }
        }
        return super.mkdir();
    }

    public boolean renameTo(File file) {
        String absolutePath = getAbsolutePath();
        if (!(isWritable(absolutePath) || isWritable(file.getAbsolutePath()))) {
            Uri pathToUri = pathToUri(absolutePath);
            if (pathToUri != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    try {
                        DocumentsContract.renameDocument(App.get().getContentResolver(), pathToUri, file.getName());
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                return file.exists();
            }
        }
        return super.renameTo(file);
    }

    private static void openDocTree(AppCompatActivity activity, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
            activity.startActivityForResult(intent, requestCode);
        }
    }

    public static boolean onActivityResult(MainActivity mainActivity, int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Cs.REQ_CODE_OPENDOCTREE_MKDIR:
            case Cs.REQ_CODE_OPENDOCTREE_RENAME:
                if (resultCode != AppCompatActivity.RESULT_OK || data == null) {
                    return true;
                }
                Uri uri = data.getData();
                storeCardUri(uri, data.getFlags());
                if (!(uri == null || validateUri(uri))) {
                    storeCardUri(null, data.getFlags());
                    uri = null;
                }
                if (uri == null) {
                    return true;
                }
//                if (requestCode == Cs.REQ_CODE_OPENDOCTREE_MKDIR) {
//                    NewFolder.askNewFolder(mainActivity, mainActivity.listViewer.curDir);
//                    return true;
//                }
//                CmdRename.askRename(mainActivity, mainActivity.listViewer);
                return true;
            case Cs.REQ_CODE_EXFILEINFO:
                if (resultCode != AppCompatActivity.RESULT_OK || data == null) {
                    return true;
                }
                openDocTree(mainActivity, data.getIntExtra(Cs.EXTRA_RESULT_INT, 0));
                return true;
            default:
                return false;
        }
    }

    public static boolean onActivityResult(MainActivity activity, Runnable runnable, int requestCode, int resultCode, Intent data) {
        Uri uri = null;
        switch (requestCode) {
            case Cs.REQ_CODE_OPENDOCTREE_COMMAND:
                int bFlag = 0;
                if (resultCode == AppCompatActivity.RESULT_OK && data != null) {
                    Uri uri1 = data.getData();
                    storeCardUri(uri1, data.getFlags());
                    if (uri1 == null || validateUri(uri1)) {
                        uri = uri1;
                    } else {
                        storeCardUri(null, data.getFlags());
                    }
                    if (uri != null) {
                        runnable.run();
                        bFlag = 1;
                    }
                }
                if (bFlag != 0) {
                    return true;
                }
                //backgroundCommand.;
                return true;
            case Cs.REQ_CODE_EXFILEINFO:
                if (resultCode != AppCompatActivity.RESULT_OK || data == null) {
                    //backgroundCommand.finish();
                    return true;
                }
                openDocTree(activity, data.getIntExtra(Cs.EXTRA_RESULT_INT, 0));
                return true;
            default:
                return false;
        }
    }
}
