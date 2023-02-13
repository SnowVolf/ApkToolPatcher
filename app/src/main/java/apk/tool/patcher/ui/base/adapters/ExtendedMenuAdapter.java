package apk.tool.patcher.ui.base.adapters;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import apk.tool.patcher.App;
import apk.tool.patcher.R;
import ru.svolf.melissa.model.ExtendedMenuItem;

public class ExtendedMenuAdapter extends RecyclerView.Adapter<ExtendedMenuAdapter.ViewHolder> {
    List<ExtendedMenuItem> items;
    private ExtendedMenuAdapter.OnItemClickListener itemClickListener;

    public ExtendedMenuAdapter(List<ExtendedMenuItem> items) {
        this.items = items;
    }

    public void setItemClickListener(ExtendedMenuAdapter.OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public ExtendedMenuItem getItem(int position) {
        return items.get(position);
    }

    @NonNull
    @Override
    public ExtendedMenuAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exp, parent, false);
        return new ExtendedMenuAdapter.ViewHolder(v);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ExtendedMenuAdapter.ViewHolder holder, int position) {
        ExtendedMenuItem item = getItem(position);
        assert item != null;

        holder.text.setText(item.getTitle());
        holder.icon.setImageResource(item.getIcon());

        //Тинт круга иконки парсим из параметра айтема
        ViewCompat.setBackgroundTintList(holder.icon, ColorStateList.valueOf(item.getTintColor()));
        // Тинт иконки делаем в цвет бэкграунда
        ImageViewCompat.setImageTintList(holder.icon,
                ColorStateList.valueOf(App.getColorFromAttr(holder.icon.getContext(),
                        android.R.attr.colorBackground)));
    }

    public interface OnItemClickListener {
        void onItemClick(ExtendedMenuItem menuItem, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView text;
        public ImageView icon;

        public ViewHolder(View v) {
            super(v);
            text = v.findViewById(R.id.list_item_content);
            icon = v.findViewById(R.id.list_item_icon);

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
