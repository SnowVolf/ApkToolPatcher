package ru.svolf.rxmanager.adapter;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import apk.tool.patcher.R;
import apk.tool.patcher.util.TextUtil;
import ru.svolf.rxmanager.data.AppInfoItem;

public class ExtendedAppAdapter extends RecyclerView.Adapter<ExtendedAppAdapter.ViewHolder> {
    private List<AppInfoItem> items;

    public ExtendedAppAdapter(List<AppInfoItem> items) {
        this.items = items;
    }

    public AppInfoItem getItem(int position) {
        return items.get(position);
    }

    @NonNull
    @Override
    public ExtendedAppAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_app_simple, parent, false);
        return new ExtendedAppAdapter.ViewHolder(v);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final AppInfoItem item = getItem(position);

        assert item != null;
        if (position % 2 != 0) {
            holder.code.setText(item.getContent());
        } else {
            holder.code.setText(item.getContent());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                holder.code.setTextAppearance(R.style.TextAppearance_AppCompat_Body2);
            } else {
                holder.code.setTextAppearance(holder.itemView.getContext(), R.style.TextAppearance_AppCompat_Body2);
            }
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView code;

        public ViewHolder(View v) {
            super(v);
            code = v.findViewById(R.id.content);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            TextUtil.copyToClipboard(code.getText().toString());
            Snackbar.make(code, R.string.label_copied, Snackbar.LENGTH_SHORT).show();
        }
    }
}
