package apk.tool.patcher.ui.modules.apps;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import apk.tool.patcher.R;
import apk.tool.patcher.util.TextUtil;

public class SimpleAppAdapter extends RecyclerView.Adapter<SimpleAppAdapter.ViewHolder> {
    private List<AppInfoItem> items;

    public SimpleAppAdapter(List<AppInfoItem> items) {
        this.items = items;
    }

    public AppInfoItem getItem(int position) {
        return items.get(position);
    }

    @NonNull
    @Override
    public SimpleAppAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_app_simple, parent, false);
        return new SimpleAppAdapter.ViewHolder(v);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onBindViewHolder(@NonNull final SimpleAppAdapter.ViewHolder holder, int position) {
        final AppInfoItem item = getItem(position);

        assert item != null;
        holder.code.setText(item.getContent());
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
            Toast.makeText(code.getContext(), R.string.label_copied, Toast.LENGTH_SHORT).show();
        }
    }
}
