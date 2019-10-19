package apk.tool.patcher.ui.widget;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
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
            if (isEnabled()){
                TextView textView = (TextView) view;
                int index = (int) textView.getTag();
                if (pm.currentPos != index){
                    beforePathChangedListener.onClick(view);
                    textView.setTextColor(textView.getCurrentTextColor() | 0xFF000000);
                    TextView tv = pm.currentText();
                    tv.setTextColor(tv.getCurrentTextColor() & 0x60FFFFFF);
                    pm.currentPos = index;
                    ensureItemVisible();
                    afterPathChangedListener.onClick(view);
                }
            }
        }
    };



    File currentPath = pm.getCurrentFile();

    private void setPath(File file) {

    }

    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3) {

    }

    private class PathItem implements Comparable<PathItem> {
        public String path;
        // FIXME Inflate widget
        TextView textView;
        ImageView arrowView;
        File pathFile;
        public String name;

        public PathItem(String name, File pathFile, PathView parent) {
            this.pathFile = pathFile;
            this.name = name;
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

        public ImageView getArrowView() {
            return arrowView;
        }

        public void setArrowView(ImageView arrowView) {
            this.arrowView = arrowView;
        }

        @Override
        public int compareTo(PathItem other) {
            return -path.compareTo(other.path);
        }
    }


    private void ensureItemVisible(){
        ensureItemVisible(true);
    }

    private void ensureItemVisible(boolean animation) {
        int index = pm.currentPos;


    }

    private final void refreshViews() {
        removeAllViews();
        boolean addArrow = false;
        int size = pm.size();
        for (int i = 0; i < size; i++) {
            TextView textView = this.pm.get(i).textView;
            if (addArrow) {
                if (this.arrow.getColorFilter() == null) {
                    this.arrow.setColorFilter(new PorterDuffColorFilter(textView.getCurrentTextColor() & 1627389951, PorterDuff.Mode.SRC_IN));
                }
                ImageView view = this.pm.get(i).getArrowView();
                if (view == null) {
                    view = new ImageView(getContext());
                    view.setImageDrawable(this.arrow);
                    this.pm.get(i).setArrowView(view);
                }
                addView(view, -2, -1);
            } else {
                addArrow = true;
            }
            if (i == pm.currentPos) {
                textView.setTextColor(textView.getCurrentTextColor() | ((int) 4278190080L));
            } else {
                textView.setTextColor(1627389951 & textView.getCurrentTextColor());
            }
            addView(textView, -2, -2);
        }
    }

    private class PathManager {
        private TreeSet<PathItem> rootPath = new TreeSet<>();
        private ArrayList<PathItem> items = new ArrayList<>();
        int currentPos = -1;
        private PathView parent;

        public PathManager(PathView parent, File pathFile) {
            this.parent = parent;
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
                    File file = pathItem.pathFile;
                    if (!path.isEmpty()){
                        for (String s : path.split("/")) {
                            file = new File(file, s);
                            items.add(new PathItem(s, file, parent));
                        }
                    }
                    currentPos = items.size() - 1;
                    break;
                }
            }
            if (!success) throw new RuntimeException("Failed to construct a breadcrumbs tree");
        }

        private void setPath(File pathFile) {
            String path = pathFile.getPath();
            boolean success = false;
            for (PathItem pathItem : rootPath) {
                if (pathItem.isMyChild(path)){
                    success = true;
                    if (pathItem.path.length() == path.length()){
                        path = "";
                    } else if (pathItem.path.length() == 1){
                        path = path.substring(1);
                    } else {
                        path =  path.substring(pathItem.path.length() + 1);
                    }
                }
                if (items.get(0) == pathItem){
                    currentPos = 0;
                    if (!path.isEmpty()){
                        for (String s : path.split("/")){
                            push(s);
                        }
                    }
                } else {
                    items.clear();
                    items.add(pathItem);
                    File file = pathItem.pathFile;
                    if (!path.isEmpty()){
                        for (String s : path.split("/")) {
                            file = new File(file, s);
                            items.add(new PathItem(s, file, parent));
                        }
                    }
                    currentPos = items.size() - 1;
                    break;
                }
            }
            if (!success) throw new RuntimeException("Failed to set path into a breadcrumbs tree");
        }

        private void push(String name) {
            int nextPos = ++currentPos;
            if (nextPos < items.size()){
                if (items.get(nextPos).name.equals(name)){
                    return;
                }
                do {
                    items.remove(items.size() - 1);
                } while (nextPos < items.size());
            }
            File file = new File(items.get(nextPos - 1).pathFile, name);
            items.add(new PathItem(name, file, parent));
        }

        public boolean canPop() {
            return currentPos > 0;
        }

        public void pop() {
            if (currentPos <= 0) throw new IllegalStateException("Cannot pop an empty array!");
            currentPos--;
        }

        public File getCurrentFile(){
            return items.get(currentPos).pathFile;
        }

        public String currentPath() {
            return items.get(currentPos).path;
        }

        public TextView currentText() {
            return items.get(currentPos).textView;
        }

        public PathItem getCurrent(){
            return items.get(currentPos);
        }

        public int size(){
            return items.size();
        }

        public PathItem get(int index){
            return items.get(index);
        }

    }
}
