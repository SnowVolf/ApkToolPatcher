package ru.svolf.melissa.widget.crumb;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import apk.tool.patcher.App;
import apk.tool.patcher.R;

public class CrumbAdapter extends RecyclerView.Adapter<CrumbAdapter.ViewHolder> {
    private ArrayList<PathItem> paths;
    private OnItemLongClickListener itemLongClickListener;

    public CrumbAdapter(ArrayList paths){
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
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.flexfilepicker_breadcrumb, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final PathItem item = getItem(position);
        if (item != null) {
            holder.pathName.setText(item.getFolderName());
            if (!item.isLast()) {
                holder.pathName.setCompoundDrawables(null, null, AppCompatResources.getDrawable(App.get(), R.drawable.ic_chevron_right), null);
            } else {
                holder.pathName.setCompoundDrawables(null, null, null, null);
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
            pathName = (TextView) itemView;
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
