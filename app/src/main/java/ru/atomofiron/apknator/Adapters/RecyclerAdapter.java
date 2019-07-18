package ru.atomofiron.apknator.Adapters;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import apk.tool.patcher.R;
import apk.tool.patcher.ui.modules.decompiler.ApkToolActivity;
import apk.tool.patcher.util.SysUtils;
import ru.atomofiron.apknator.Managers.FileManager;
import ru.atomofiron.apknator.Managers.FileManager.Filek;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.StyleableHolder> implements View.OnClickListener, View.OnLongClickListener {

    public int animResId = -1;
    public boolean lineEffect = true;
    private ApkToolActivity mainActivity;
    private OnItemClickListener listener;
    private RecyclerView recyclerView;
    //Slider:
    private float[] defEnd;
    private boolean clickable = true;
    private boolean scrolling = false;
    private SliderListener sliderListener = null;
    private ArrayList<Filek> filekList = new ArrayList<>();
    //private ArrayList<String> subtitleList = new ArrayList<>();
    //private ArrayList<Boolean> isDirList = new ArrayList<>();
    private boolean listen = false;
    private float limit;
    private Map<Integer, Drawable> apkIcons = new HashMap<>();
    private int curAnimLine = -1;
    private boolean touched = false; // for line anim after scrolling
    private boolean onLongClicked = false;

    public RecyclerAdapter(ApkToolActivity activity, RecyclerView recyclerView) {
        mainActivity = activity;
        this.recyclerView = recyclerView;
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            float lastY = -1;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getPointerCount() > 1)
                    return true;

                if (sliderListener.sliding) {
                    if (lastY == -1)
                        lastY = event.getY();

                    event.setLocation(event.getX(), lastY);
                    sliderListener.onTouch(sliderListener.view, event);
                } else
                    scrolling = true;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    scrolling = false;
                    lastY = -1;
                }
                return false;
            }
        });
        sliderListener = new SliderListener();
        limit = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48, mainActivity.getResources().getDisplayMetrics());
    }

    public void setData(File[] files, boolean seriously) {
        SysUtils.Log("setData(): " + seriously);
        //for (File file : files)
        //	filekList.add(new Filek(file));
        FileManager.Filek.getFileks(files, filekList);
        discovery();
        if (seriously)
            curAnimLine = -1;
        touched = false;
        notifyDataSetChanged();
        if (seriously)
            recyclerView.scrollToPosition(0);
    }

    @NonNull
    @Override
    public StyleableHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_new, parent, false);
        v.setOnTouchListener(sliderListener);
        v.setOnClickListener(null);
        v.setOnLongClickListener(this);
        v.findViewById(R.id.delete).setOnClickListener(this);
        v.findViewById(R.id.rename).setOnClickListener(this);
        v.findViewById(R.id.operation_1).setOnClickListener(this);
        v.findViewById(R.id.operation_2).setOnClickListener(this);
        v.findViewById(R.id.operation_3).setOnClickListener(this);
        if (!lineEffect)
            v.setBackgroundResource(R.drawable.bg_file_line);

        return new StyleableHolder((RelativeLayout) v);
    }

    @Override
    public void onBindViewHolder(final StyleableHolder holder, int position) {

        position = holder.getAdapterPosition();
        final Filek filek = filekList.get(position);
        String name = filek.getName();
        holder.title.setText(name);
        holder.subtitle.setText(filek.getDataTime());
        if (filek.isDirectory())
            holder.subtitle.setText(filek.getDataTime());
        else
            holder.subtitle.setText(filek.getDataTime().concat("   ").concat(filek.size()));
        if (filek.isDirectory()) {
            holder.icon.setImageDrawable(mainActivity.getResources().getDrawable(
                    filek.canRead() ? R.drawable.folder : R.drawable.folder_open));
            if (name.endsWith("_src") || name.endsWith("_dex") || name.endsWith("_odex")) {
                holder.ibOperation_1.setImageDrawable(mainActivity.getResources().getDrawable(R.drawable.ic_play));
                holder.operationCount = 1;
            } else {
                holder.ibOperation_1.setImageDrawable(mainActivity.getResources().getDrawable(R.drawable.home));
                holder.ibOperation_2.setImageDrawable(mainActivity.getResources().getDrawable(R.drawable.ic_tools));
                holder.operationCount = 2;
            }
        } else if (name.endsWith(".dex")) {
            holder.ibOperation_1.setImageDrawable(getDrawable(R.drawable.ic_play));
            holder.operationCount = 1;
            holder.icon.setImageDrawable(getDrawable(R.drawable.dex));
        } else if (name.endsWith(".odex")) {
            holder.ibOperation_1.setImageDrawable(getDrawable(R.drawable.ic_play));
            holder.operationCount = 1;
            holder.icon.setImageDrawable(getDrawable(R.drawable.odex));
        } else if (name.endsWith(".oat")) {
            holder.ibOperation_1.setImageDrawable(getDrawable(R.drawable.ic_play));
            holder.operationCount = 1;
            holder.icon.setImageDrawable(getDrawable(R.drawable.odex));
        } else if (name.endsWith(".smali")) {
            holder.operationCount = 1;
            holder.ibOperation_1.setImageDrawable(getDrawable(R.drawable.ic_lucky));
            holder.icon.setImageDrawable(getDrawable(R.drawable.smali));
        } else if (name.endsWith(".jar")) {
            holder.ibOperation_1.setImageDrawable(getDrawable(R.drawable.ic_play));
            holder.ibOperation_2.setImageDrawable(getDrawable(R.drawable.ic_config));
            holder.operationCount = 3;
            holder.icon.setImageDrawable(getDrawable(R.drawable.jar));
        } else if (name.endsWith(".apk")) {
            holder.ibOperation_1.setImageDrawable(getDrawable(R.drawable.ic_play));
            holder.ibOperation_2.setImageDrawable(getDrawable(R.drawable.ic_config));
            holder.operationCount = 3;
            Drawable icon = apkIcons.get(position);
            if (icon == null) icon = getIcon(filek.getAbsolutePath());
            holder.icon.setImageDrawable(icon);
        } else if (name.endsWith(".class")) {
            holder.ibOperation_1.setImageDrawable(getDrawable(R.drawable.ic_play));
            holder.operationCount = 1;
            holder.icon.setImageDrawable(getDrawable(R.drawable.clss));
        } else if (name.endsWith(".java")) {
            holder.ibOperation_1.setImageDrawable(getDrawable(R.drawable.ic_play));
            holder.operationCount = 1;
            holder.icon.setImageDrawable(getDrawable(R.drawable.java));
        } else if (name.endsWith(".jks") || name.endsWith(".keystore")) {
            holder.ibOperation_1.setImageDrawable(getDrawable(R.drawable.ic_config));
            holder.operationCount = 1;
            holder.icon.setImageDrawable(getDrawable(R.drawable.ic_key));
        } else {
            holder.icon.setImageDrawable(getDrawable(R.drawable.file));
            holder.operationCount = 0;
        }

        if (animResId > 0 && position > curAnimLine) {
            curAnimLine = position;
            if (touched)
                holder.ll.startAnimation(AnimationUtils.loadAnimation(mainActivity, animResId));
        }
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull StyleableHolder holder) {
        holder.title.setSelected(false);
        super.onViewDetachedFromWindow(holder);
    }

    @Override
    public void onViewAttachedToWindow(@NonNull StyleableHolder holder) {
        super.onViewAttachedToWindow(holder);
        SysUtils.marqueeAfterDelay(2000, holder.title);
    }

    @Override
    public boolean onFailedToRecycleView(@NonNull StyleableHolder holder) {
        holder.title.setSelected(false);
        return super.onFailedToRecycleView(holder);
    }


    private Drawable getDrawable(int resId) {
        return mainActivity.getResources().getDrawable(resId);
    }

    private void discovery() {
        apkIcons.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < filekList.size(); i++) {
                    Filek filek = filekList.get(i);
                    if (!filek.isFile() || !filek.getName().endsWith(".apk"))
                        continue;

                    apkIcons.put(i, getIcon(filek.getAbsolutePath()));
                }
            }
        }).start();
    }

    private Drawable getIcon(String path) {
        PackageManager pm = mainActivity.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES);
        if (info != null) {
            ApplicationInfo appInfo = info.applicationInfo;
            appInfo.sourceDir = path;
            appInfo.publicSourceDir = path;
            try {
                return appInfo.loadIcon(pm);
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
                return getDrawable(R.drawable.file);
            }
        } else
            return getDrawable(R.drawable.file);
    }

    @Override
    public int getItemCount() {
        return filekList.size();
    }

    @Override
    public boolean onLongClick(View v) {
        if (!sliderListener.sliding) {
            listener.onItemLongClick(v, recyclerView.getChildAdapterPosition(v));
            onLongClicked = true;
        }
        return false;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
        listen = listener != null;
    }

    @Override
    public void onClick(View v) {
        SysUtils.Log("onClick()");
        if (listen)
            listener.onItemButtonClick(v.getId(), recyclerView.getChildViewHolder((View) v.getParent()).getAdapterPosition());

        sliderListener.closeSlider();
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position);

        void onItemLongClick(View v, int position);

        void onItemButtonClick(int operation, int position);
    }

    static class StyleableHolder extends RecyclerView.ViewHolder {
        public ImageView icon;
        public TextView title;
        public TextView subtitle;
        LinearLayout ll;
        ImageButton ibOperation_1;
        ImageButton ibOperation_2;
        // public ImageButton ibOperation_3; //  позже понадобится
        int operationCount = -1;

        StyleableHolder(RelativeLayout v) {
            super(v);
            ll = (LinearLayout) v.findViewById(R.id.item_ll);
            icon = (ImageView) ll.findViewById(R.id.icon);
            title = (TextView) ll.findViewById(R.id.title);
            subtitle = (TextView) ll.findViewById(R.id.subtitle);
            ibOperation_1 = (ImageButton) v.findViewById(R.id.operation_1);
            ibOperation_2 = (ImageButton) v.findViewById(R.id.operation_2);
            //ibOperation_3 = (ImageButton)v.findViewById(R.id.operation_3);
        }
    }

    private class SliderListener implements View.OnTouchListener {

        View view;
        LinearLayout itemLL = null;
        ImageButton ibEmpty = null;
        ImageButton ibDelete = null;
        ImageButton ibRename = null;
        ImageButton ibOperation_1 = null;
        ImageButton ibOperation_2 = null;
        ImageButton ibOperation_3 = null;

        float lastTouchX = -1;
        float destForClose;
        float ibDecompileDef;
        float ibApkfileDef;
        float ibArchiveDef;
        boolean savedDef = false;
        float lengthX = 0;
        int state = 0; // closed (anim)? openedLeft openedRight
        int position = -1;
        int lastPosition = -1;
        boolean sliding = false;
        int operationCount = -1;

        //View highlight;
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (scrolling || event.getPointerCount() > 1 || v == null)
                return false;

            float eventX = event.getX();
            if (!sliding) switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    view = v;
                    touched = true;
                    clickable = true;
                    onLongClicked = false;
                    position = recyclerView.getChildAdapterPosition(v);

                    if (!savedDef) {
                        ibDecompileDef = view.findViewById(R.id.operation_1).getX();
                        ibApkfileDef = view.findViewById(R.id.operation_2).getX();
                        ibArchiveDef = view.findViewById(R.id.operation_3).getX();
                        savedDef = true;
                        defEnd = new float[]{-limit * 2, -limit, 0, ibDecompileDef, ibApkfileDef, ibArchiveDef};
                    }

                    if (lastPosition == -1)
                        lastPosition = position;
                    if (lastPosition != position && state != 0 && itemLL != null)
                        animSlider(itemLL.getX(), defEnd, 0);

                    ibDelete = (ImageButton) view.findViewById(R.id.delete);
                    ibEmpty = (ImageButton) view.findViewById(R.id.empty);
                    ibRename = (ImageButton) view.findViewById(R.id.rename);
                    itemLL = (LinearLayout) view.findViewById(R.id.item_ll);
                    ibOperation_1 = (ImageButton) view.findViewById(R.id.operation_1);
                    ibOperation_2 = (ImageButton) view.findViewById(R.id.operation_2);
                    ibOperation_3 = (ImageButton) view.findViewById(R.id.operation_3);

                    lastPosition = position;
                    operationCount = ((StyleableHolder) recyclerView.getChildViewHolder(view)).operationCount;
                    lastTouchX = eventX;
                    lengthX = 0;
                    break;
                case MotionEvent.ACTION_MOVE:
                    lengthX += eventX - lastTouchX;
                    if ((lengthX >= 64 || lengthX <= -64)) {
                        sliding = true;
                        clickable = false;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (!onLongClicked)
                        listener.onItemClick(view, position);
                    break;
            }
            if (sliding) switch (event.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    float dif = eventX - lastTouchX;
                    float itemLLX = itemLL.getX();

                    if (itemLLX + dif > limit * 2 && dif > 0)
                        dif = limit * 2 - itemLLX;
                    else if (itemLLX + dif < -limit * operationCount && dif < 0)
                        dif = -limit * operationCount - itemLLX;

                    ibEmpty.setX(ibEmpty.getX() + dif);
                    ibDelete.setX(ibDelete.getX() + dif);
                    ibRename.setX(ibRename.getX() + dif);
                    itemLL.setX(itemLLX + dif);
                    ibOperation_1.setX(ibOperation_1.getX() + dif);
                    ibOperation_2.setX(ibOperation_2.getX() + dif);
                    ibOperation_3.setX(ibOperation_3.getX() + dif);
                    break;
                case MotionEvent.ACTION_UP:
                    float[] end = defEnd;
                    float cur = itemLL.getX();
                    float dest = cur;
                    int newState = 0;
                    sliding = false;
                    if (!(state == 2 && cur < limit * 1.5 && cur > 0 ||
                            state == 3 && cur > -limit * (((float) operationCount) - 0.5) && cur < 0)) {
                        if (cur >= limit / 2) {
                            destForClose = limit * 2;
                            newState = 2;
                            dest = cur - limit * 2;
                            end = new float[]{0, limit, limit * 2, ibDecompileDef + limit * 2, ibApkfileDef + limit * 2, ibArchiveDef + limit * 2};
                        } else if (cur <= -limit / 2) {
                            destForClose = -limit * operationCount;
                            newState = 3;
                            dest = cur + limit * operationCount;
                            end = new float[]{-limit * (2 + operationCount), -limit * (1 + operationCount), -limit * operationCount, ibDecompileDef - limit * operationCount, ibApkfileDef - limit * operationCount, ibArchiveDef - limit * operationCount};
                        }
                    }
                    animSlider(dest, end, newState);
                    break;
            }
            lastTouchX = eventX;
            return false;
        }

        private void closeSlider() {
            animSlider(destForClose, defEnd, 0);
        }

        private void animSlider(float dest, final float[] finEnd, final int newState) {
            SysUtils.Log("animSlider()");
            setSlider(finEnd);
            state = newState;
            Animation anim = new TranslateAnimation(dest, 0, itemLL.getY(), itemLL.getY());
            anim.setDuration(200);
            ibEmpty.startAnimation(anim);
            ibDelete.startAnimation(anim);
            ibRename.startAnimation(anim);
            itemLL.startAnimation(anim);
            ibOperation_1.startAnimation(anim);
            ibOperation_2.startAnimation(anim);
            ibOperation_3.startAnimation(anim);
        }

        private void setSlider(float[] end) {
            SysUtils.Log("setSlider()");
            ibEmpty.setX(end[0]);
            ibDelete.setX(end[0]);
            ibRename.setX(end[1]);
            itemLL.setX(end[2]);
            ibOperation_1.setX(end[3]);
            ibOperation_2.setX(end[4]);
            ibOperation_3.setX(end[5]);
        }
    }

}
