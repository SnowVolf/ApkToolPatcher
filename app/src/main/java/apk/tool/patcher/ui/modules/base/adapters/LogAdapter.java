package apk.tool.patcher.ui.modules.base.adapters;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import apk.tool.patcher.R;
import apk.tool.patcher.util.TextUtil;
import ru.svolf.melissa.compat.Compat;
import ru.svolf.melissa.model.LogItem;

public class LogAdapter extends RecyclerView.Adapter<LogAdapter.ViewHolder> {
    List<LogItem> items;
    private LogAdapter.OnItemClickListener itemClickListener;

    public LogAdapter(List<LogItem> items) {
        this.items = items;
    }

    public void setItemClickListener(LogAdapter.OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public LogItem getItem(int position) {
        return items.get(position);
    }

    @NonNull
    @Override
    public LogAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_logger, parent, false);
        return new LogAdapter.ViewHolder(v);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onBindViewHolder(@NonNull LogAdapter.ViewHolder holder, int position) {
        LogItem item = getItem(position);
        assert item != null;

        String color;
        switch (item.getTag()) {
            case "i":
                color = "#4caf50";
                break;
            case "e":
                color = "#d32f2f";
                break;
            case "w":
                color = "#ff9800";
                break;
            default:
                color = "#2979ff";
                break;
        }
        if (item.getTag().equalsIgnoreCase("e")) {
            holder.text.setText(Compat.htmlCompat(item.getMessage()));
        } else {
            holder.text.setText(item.getMessage());
        }
        ViewCompat.setBackgroundTintList(holder.icon, ColorStateList.valueOf(Color.parseColor(color)));
        holder.icon.setText(item.getTag().toUpperCase());
    }

    public interface OnItemClickListener {
        void onItemClick(LogItem menuItem, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView text;
        public TextView icon;

        public ViewHolder(View v) {
            super(v);
            text = v.findViewById(R.id.message);
            icon = v.findViewById(R.id.log_tag);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextUtil.copyToClipboard(text.getText().toString());
                    Toast.makeText(v.getContext(), R.string.label_copied, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
