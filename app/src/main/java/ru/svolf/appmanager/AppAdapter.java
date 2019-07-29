package ru.svolf.appmanager;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import apk.tool.patcher.App;
import apk.tool.patcher.R;

public class AppAdapter extends RecyclerView.Adapter<AppAdapter.AppViewHolder> implements Filterable {
    // AppAdapter variables
    private List<AppInfo> appList;
    private List<AppInfo> appListSearch;
    private Context context;

    public AppAdapter(List<AppInfo> appList, Context context) {
        this.appList = appList;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return appList.size();
    }

    public void clear() {
        appList.clear();
        notifyDataSetChanged();
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull AppViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
    }

    @Override
    public void onBindViewHolder(@NonNull final AppViewHolder appViewHolder, int i) {
        final AppInfo appInfo = appList.get(i);


        try {
            appViewHolder.vName.setText(getName(appInfo.getPackageName()));
            appViewHolder.vApk.setText(getVersion(appInfo.getPackageName()));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        appViewHolder.vIcon.setImageDrawable(getIcon(appInfo));

        setButtonEvents(appViewHolder, appInfo);
    }

    private void setButtonEvents(AppViewHolder appViewHolder, final AppInfo appInfo) {
        final ImageView appIcon = appViewHolder.vIcon;
        final ConstraintLayout card = appViewHolder.vCard;

    }

    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                final FilterResults oReturn = new FilterResults();
                final List<AppInfo> results = new ArrayList<>();
                if (appListSearch == null) {
                    appListSearch = appList;
                }
                if (charSequence != null) {
                    if (appListSearch != null && appListSearch.size() > 0) {
                        for (final AppInfo appInfo : appListSearch) {
                            try {
                                if (getName(appInfo.getPackageName()).toLowerCase().contains(charSequence.toString())) {
                                    results.add(appInfo);
                                }
                            } catch (PackageManager.NameNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    oReturn.values = results;
                    oReturn.count = results.size();
                }
                return oReturn;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                if (filterResults.count > 0) {
                    AppManagerFragment.setResultsMessage(false);
                } else {
                    AppManagerFragment.setResultsMessage(true);
                }
                appList = (ArrayList<AppInfo>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public AppViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View appAdapterView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.app_layout, viewGroup, false);
        return new AppViewHolder(appAdapterView);
    }

    public static class AppViewHolder extends RecyclerView.ViewHolder {
        protected TextView vName;
        protected TextView vApk;
        protected ImageView vIcon;
        protected ConstraintLayout vCard;

        public AppViewHolder(View v) {
            super(v);
            vCard = v.findViewById(R.id.card);
            vName = vCard.findViewById(R.id.txtName);
            vApk = vCard.findViewById(R.id.txtApk);
            vIcon = vCard.findViewById(R.id.imgIcon);
        }
    }

    private String getName(String pkgName) throws PackageManager.NameNotFoundException {
        return App.get()
                .getPackageManager()
                .getPackageInfo(pkgName, PackageManager.GET_META_DATA)
                .applicationInfo.loadLabel(App.get().getPackageManager()).toString();
    }

    private String getVersion(String pkgName) throws PackageManager.NameNotFoundException {
        return App.get()
                .getPackageManager()
                .getPackageInfo(pkgName, PackageManager.GET_META_DATA).versionName;
    }

    private Drawable getIcon(AppInfo appInfo) {
        if (UtilsApp.getIconFromCache(context, appInfo) != context.getDrawable(R.mipmap.ic_launcher)) {
            Toast.makeText(context, "ICON NULL", Toast.LENGTH_SHORT).show();
            try {
                if (UtilsApp.saveIconToCache(context, appInfo)) {
                    Toast.makeText(context, "ICON SAVED", Toast.LENGTH_SHORT).show();
                    return UtilsApp.getIconFromCache(context, appInfo);
                }
            } catch (Exception e) {
                Toast.makeText(context, "SUKAAAAA\n\n"+ e, Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
        Toast.makeText(context, "PIZDA RULYI", Toast.LENGTH_SHORT).show();
        return context.getDrawable(R.mipmap.ic_launcher);
    }

}
