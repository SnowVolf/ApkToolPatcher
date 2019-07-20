package apk.tool.patcher.ui.modules.apps;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;

import apk.tool.patcher.R;
import ru.svolf.melissa.model.AppItem;

public class SystemAppsFragment extends Fragment {
    public static final String FRAGMENT_TAG = "sys_apps_fragment";
    private View rootView;
    private Context mContext;
    private ListView listView = null;
    private ArrayList<AppItem> mAppItems = new ArrayList<>();

    public static SystemAppsFragment newInstance(ArrayList<AppItem> list) {
        SystemAppsFragment fragment = new SystemAppsFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(FRAGMENT_TAG, list);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        setRetainInstance(true);

        if (getArguments() != null) {
            mAppItems = getArguments().getParcelableArrayList(FRAGMENT_TAG);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_app_listing, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        listView = view.findViewById(R.id.list);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupList(mAppItems);
    }

    private void setupList(ArrayList<AppItem> AllPackages) {
        final ArrayAdapter<AppItem> aa = new ArrayAdapter<AppItem>(mContext, R.layout.package_list_item, AllPackages) {
            @NonNull
            @SuppressLint("InflateParams")
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.package_list_item, null);
                }

                AppItem pkg = getItem(position);

                ViewHolder holder = new ViewHolder();

                holder.packageLabel = convertView.findViewById(R.id.pkg_name);
                holder.packageName = convertView.findViewById(R.id.pkg_id);
                holder.packageVersion = convertView.findViewById(R.id.pkg_version);
                holder.packageFilePath = convertView.findViewById(R.id.pkg_dir);
                holder.packageIcon = convertView.findViewById(R.id.pkg_img);
                holder.position = position;

                convertView.setTag(holder);

                holder.packageLabel.setText(pkg.getPackageLabel());
                holder.packageName.setText(pkg.getPackageName());
                holder.packageVersion.setText(String.format("version %s", pkg.getPackageVersion()));
                holder.packageFilePath.setText(pkg.getPackageFilePath());

                holder.packageIcon.setImageDrawable(pkg.getPackageIcon());

                return convertView;
            }
        };
        listView.setAdapter(aa);
        listView.setTextFilterEnabled(true);
        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Create the fragment only when the activity is created for the first time.
                // ie. not after orientation changes
                Fragment fragment = getActivity().getSupportFragmentManager().findFragmentByTag(AppsDetailsFragment.FRAGMENT_TAG);
                if (fragment == null) {
                    fragment = AppsDetailsFragment.newInstance(aa.getItem(position).getPackageName());
                }
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.content_frame, fragment, SystemAppsFragment.FRAGMENT_TAG)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .addToBackStack(null)
                        .commit();

            }
        });
    }

    @Override
    public void onDestroyView() {
        rootView = null;
        super.onDestroyView();
    }

    private static class ViewHolder {
        TextView packageLabel;
        TextView packageName;
        TextView packageVersion;
        TextView packageFilePath;
        ImageView packageIcon;
        int position;
    }
}
