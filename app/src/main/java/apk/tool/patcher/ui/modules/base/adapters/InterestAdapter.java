package apk.tool.patcher.ui.modules.base.adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import apk.tool.patcher.R;
import apk.tool.patcher.util.SysUtils;
import apk.tool.patcher.util.TextUtil;
import ru.svolf.melissa.fragment.dialog.SweetContentDialog;
import ru.svolf.melissa.model.InterestSmaliItem;

public class InterestAdapter extends RecyclerView.Adapter<InterestAdapter.ViewHolder> {
    private List<InterestSmaliItem> items;
    private InterestAdapter.OnItemClickListener itemClickListener;

    public InterestAdapter(List<InterestSmaliItem> items) {
        this.items = items;
    }

    public void setItemClickListener(InterestAdapter.OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public InterestSmaliItem getItem(int position) {
        return items.get(position);
    }

    @NonNull
    @Override
    public InterestAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_interest_smail, parent, false);
        return new InterestAdapter.ViewHolder(v);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onBindViewHolder(final InterestAdapter.ViewHolder holder, int position) {
        final InterestSmaliItem item = getItem(position);

        assert item != null;
        holder.caption.setText(item.getSmaliName());
        holder.code.setText(item.getPieceOfCode());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final String[] arr = v.getResources().getStringArray(R.array.interest_items);
                final SweetContentDialog dialog = new SweetContentDialog(v.getContext());
                dialog.setTitle(item.getSmaliName());
                dialog.setMessage(item.getPieceOfCode());
                dialog.setPositive(R.drawable.ic_copy, arr[0], new View.OnClickListener() {
                    @Override
                    public void onClick(View v1) {
                        TextUtil.copyToClipboard(item.getSmaliPath());
                        Toast.makeText(v.getContext(), R.string.label_copied, Toast.LENGTH_SHORT).show();
                    }
                });
                dialog.setNegative(R.drawable.ic_copy, arr[1], new View.OnClickListener() {
                    @Override
                    public void onClick(View v1) {
                        TextUtil.copyToClipboard(item.getPieceOfCode());
                        Toast.makeText(v.getContext(), R.string.label_copied, Toast.LENGTH_SHORT).show();
                    }
                });
                dialog.setNeutral(R.drawable.ic_launch, arr[2], new View.OnClickListener() {
                    @Override
                    public void onClick(View v1) {
                        SysUtils.openFile(v.getContext(), item.getSmaliPath());
                    }
                });
                dialog.show();
            }
        });
    }

    public interface OnItemClickListener {
        void onItemClick(InterestSmaliItem menuItem, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView caption, code;

        public ViewHolder(View v) {
            super(v);
            caption = v.findViewById(R.id.caption);
            code = v.findViewById(R.id.content);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(getItem(getLayoutPosition()), getLayoutPosition());
            }
        }
    }
}
