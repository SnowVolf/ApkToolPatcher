package ru.svolf.melissa.compat;

import android.content.ContentResolver;
import android.net.Uri;

import java.util.List;

/**
 *  on 30.08.2016.
 */
public class DocumentsContractCompat {
    private static final String TAG = "DocumentsCompat";

    private static final String PATH_TREE = "tree";
    private static final String PATH_DOCUMENT = "document";

    public static String getTreeDocumentId(Uri documentUri) {
        final List<String> paths = documentUri.getPathSegments();
        if (paths.size() >= 2 && PATH_TREE.equals(paths.get(0))) {
            return paths.get(1);
        }
        throw new IllegalArgumentException("Invalid URI: " + documentUri);
    }

    public static Uri buildDocumentUriUsingTree(Uri treeUri, String documentId) {
        return new Uri.Builder().scheme(ContentResolver.SCHEME_CONTENT)
                .authority(treeUri.getAuthority()).appendPath(PATH_TREE)
                .appendPath(getTreeDocumentId(treeUri)).appendPath(PATH_DOCUMENT)
                .appendPath(documentId).build();
    }
}
