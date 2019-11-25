package ru.svolf.melissa.widget.crumb;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import apk.tool.patcher.R;

public class CrumbAdapter extends RecyclerView.Adapter<CrumbAdapter.ViewHolder> {
    private ArrayList<PathItem> paths;
    private OnItemLongClickListener itemLongClickListener;

    public CrumbAdapter(ArrayList<PathItem> paths){
        this.paths = paths;
    }

    public void setItemLongClickListener(OnItemLongClickListener itemLongClickListener){
        this.itemLongClickListener = itemLongClickListener;
    }

    public PathItem getItem(int position){
        return paths.get(position);
    }

    @NonNull
    @Override
    public CrumbAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.flexfilepicker_breadcrumb, parent, false);
        return new CrumbAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PathItem item = getItem(position);
        if (item != null) {
            TextView textView = holder.pathName;
            textView.setText(item.getFolderName());
            Drawable chevron = ContextCompat.getDrawable(holder.pathName.getContext(), R.drawable.ic_chevron_right);

            if (!item.isLast()) {
                textView.setTextColor(textView.getCurrentTextColor() & 0x99FAFAFA);
                textView.setTypeface(Typeface.DEFAULT);
                textView.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, chevron, null);
            } else {
                textView.setTextColor(textView.getCurrentTextColor() | 0xFF000000);
                textView.setTypeface(Typeface.DEFAULT_BOLD);
                textView.setCompoundDrawables(null, null, null, null);
            }
        }
    }

    @Override
    public int getItemCount() {
        return paths.size();
    }

    public interface OnItemLongClickListener {
        void onItemClick(PathItem pathItem, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        TextView pathName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            pathName = (TextView) itemView.findViewById(R.id.breadcrumb_part);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {
            if (itemLongClickListener != null){
                itemLongClickListener.onItemClick(getItem(getLayoutPosition()), getLayoutPosition());
                return true;
            } else return false;
        }
    }
}
