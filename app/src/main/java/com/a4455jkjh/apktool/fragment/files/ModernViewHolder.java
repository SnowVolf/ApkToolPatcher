package com.a4455jkjh.apktool.fragment.files;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import apk.tool.patcher.R;

public class ModernViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    TextView name;
    ImageView icon;

    public ModernViewHolder(@NonNull View itemView) {
        super(itemView);

        name = itemView.findViewById(R.id.name);
        icon = itemView.findViewById(R.id.icon);

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

    }
}