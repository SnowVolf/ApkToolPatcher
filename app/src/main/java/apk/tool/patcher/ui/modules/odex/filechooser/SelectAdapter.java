package apk.tool.patcher.ui.modules.odex.filechooser;

//import android.*;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import apk.tool.patcher.R;
import apk.tool.patcher.entity.async.GetIcon;
import apk.tool.patcher.util.Preferences;
import apk.tool.patcher.util.SysUtils;

public class SelectAdapter extends RecyclerView.Adapter<SelectAdapter.ViewHolder> {
    public static ClickListener clickListener;
    private static File[] files;
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yy  HH:mm", Locale.ENGLISH);

    public SelectAdapter(File[] f, ClickListener l) {
        files = f;
        clickListener = l;
    }

    public void onFilesUpdate(File[] f) {
        files = f;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SelectAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup p1, int p2) {
        View view;
        if (Preferences.getGridSize() < 3) {
            view = LayoutInflater.from(p1.getContext()).inflate(R.layout.recycler_select_item, p1, false);
        } else {
            view = LayoutInflater.from(p1.getContext()).inflate(R.layout.recycler_select_item_grid, p1, false);
        }
        // TODO: Implement this method
        return new ViewHolder(view);
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull SelectAdapter.ViewHolder holder) {
        holder.textViewName.setSelected(false);
        super.onViewDetachedFromWindow(holder);
    }

    @Override
    public void onViewAttachedToWindow(@NonNull SelectAdapter.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        SysUtils.marqueeAfterDelay(2000, holder.textViewName);
    }

    @Override
    public boolean onFailedToRecycleView(@NonNull SelectAdapter.ViewHolder holder) {
        holder.textViewName.setSelected(false);
        return super.onFailedToRecycleView(holder);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder p1, int p2) {
        File file = files[p2];
        p1.textViewName.setText(file.getName());
        p1.textViewData.setText(simpleDateFormat.format(new Date(file.lastModified())));
        p1.imageView.setBackgroundResource(R.drawable.file_background);
        if (file.isDirectory()) {
            p1.imageView.setImageResource(file.canRead() ? R.drawable.folder : R.drawable.folder);
        } else {
            p1.imageView.setTag(file.getPath());
            GetIcon.getInstance().resolve(file.getPath(), p1.imageView);
        }
    }

    @Override
    public int getItemCount() {
        return files.length;
    }

    public interface ClickListener {
        void onItemClick(int position);

        //void onItemLongClick(int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewName;
        private TextView textViewData;
        private ImageView imageView;

        public ViewHolder(View view) {
            super(view);

            textViewName = view.findViewById(R.id.recycler_select_item_NameTextView);
            textViewData = view.findViewById(R.id.recycler_select_item_DataTextView);
            imageView = view.findViewById(R.id.recycler_select_item_ImageView);

            view.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View p1) {
                    clickListener.onItemClick(getLayoutPosition());
                }
            });

//            view.setOnLongClickListener(new View.OnLongClickListener() {
//
//                @Override
//                public boolean onLongClick(View p1) {
//                    clickListener.onItemLongClick(getLayoutPosition());
//                    return true;
//                }
//            });
        }
    }
}
