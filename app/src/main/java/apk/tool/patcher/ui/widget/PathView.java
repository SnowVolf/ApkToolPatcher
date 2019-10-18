package apk.tool.patcher.ui.widget;

import android.content.Context;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.OverScroller;
import android.widget.TextView;

import androidx.core.view.GestureDetectorCompat;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import java.io.File;
import java.util.ArrayList;
import java.util.TreeSet;

import apk.tool.patcher.R;

public class PathView extends ViewGroup {
    private int maxScrollX = 0;
    private PathManager pm = new PathManager(this, new File("/"));
    Integer itemVisibleOffset = 20;

    // Arrow
    private VectorDrawableCompat arrow = VectorDrawableCompat.create(getContext().getResources(), R.drawable.ic_check, getContext().getTheme());

    private Float touchSlopSquare;
    private Integer scaledOverflingDistance;

    private OnClickListener afterPathChangedListener;
    private OnClickListener beforePathChangedListener;

    public PathView(Context context) {
        super(context);
        init();
    }

    public PathView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PathView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        ViewConfiguration vc = ViewConfiguration.get(getContext());
        int touchSlop = vc.getScaledTouchSlop();
        touchSlopSquare = (float) (touchSlop * touchSlop);
        scaledOverflingDistance =  vc.getScaledOverflingDistance();
        setMotionEventSplittingEnabled(false);
        if (isInEditMode()){
            File file = new File("/system/bin");
            setPath(file);
        }
    }

    private OverScroller mScroller = new OverScroller(getContext());

    private GestureDetectorCompat gestureDetector = new GestureDetectorCompat(getContext(), new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            int offX = (int) distanceX;
            if (offX == 0) {
                return true;
            }

            int x = getScrollX() + offX;
            if (x < 0)
                x = 0;
            else if (x > maxScrollX)
                x = maxScrollX;

            scrollTo(x, 0);
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (maxScrollX > 0) {
                mScroller.fling(getScrollX(), 0, (int) -velocityX, 0, 0, maxScrollX,
                        0, 0, scaledOverflingDistance, 0);
                postInvalidate();
            }
            return true;
        }
    });

    private OnClickListener itemClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
        }
    };

    private void setPath(File file) {

    }

    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3) {

    }

    private class PathItem implements Comparable<PathItem> {
        public String path;
        // FIXME Inflate widget
        TextView textView;
        ImageView arrowView = null;

        public PathItem(String name, File pathFile, PathView parent) {
            path = pathFile.getPath();
            textView.setText(name);
            textView.setOnClickListener(parent.itemClickListener);
        }

        public boolean isMyChild(String path){
            String root = this.path;
            if (path.startsWith(root)){
                if (root.length() == 1 || path.length() == root.length())
                    return true;
                return path.split("/")[path.length()].equals("/");
            }
            return false;
        }

        @Override
        public int compareTo(PathItem other) {
            return -path.compareTo(other.path);
        }
    }

    private class PathManager {
        private TreeSet<PathItem> rootPath = new TreeSet<>();
        private ArrayList<PathItem> items = new ArrayList<>();
        public int currentPos = -1;

        public PathManager(PathView parent, File pathFile) {
            Context context = parent.getContext();
            if (!parent.isInEditMode()) {
                rootPath.add(new PathItem("Internal Storage",
                        Environment.getExternalStorageDirectory(), parent));
            }
            rootPath.add(new PathItem("Root",
                    new File("/"), parent));

            String path = pathFile.getPath();
            boolean success = false;

            for (PathItem pathItem : rootPath){
                if (pathItem.isMyChild(path)){
                    success = true;
                    if (pathItem.path.length() == path.length()){
                        path = "";
                    } else if (pathItem.path.length() == 1){
                        path = path.substring(1);
                    } else {
                        path =  path.substring(pathItem.path.length() + 1);
                    }

                    items.add(pathItem);
                    //File file = pathItem.
                }
            }
        }
    }
}
