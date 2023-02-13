package apk.tool.patcher.ui.odex;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.gmail.heagoo.apkeditor.util.OdexPatcher;
import com.gmail.heagoo.common.ApkInfoParser;

import apk.tool.patcher.R;
import apk.tool.patcher.ui.odex.filechooser.FileSelectDialog;
import apk.tool.patcher.ui.odex.filechooser.ProcessingDialog;


public class OdexPatchFragment extends Fragment implements View.OnClickListener, FileSelectDialog.IFileSelection {
    public static final String FRAGMENT_TAG = "odex_patch_fragment";
    private EditText apkPathEt;
    private String apkPath;
    private View rootView;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_odex_patch, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        initView(view);
    }

    private void initView(View view) {
        this.apkPathEt = view.findViewById(R.id.et_apkpath);

        ImageButton selectBtn = view.findViewById(R.id.btn_select_apkpath);
        selectBtn.setOnClickListener(this);
        Button applyBtn = view.findViewById(R.id.btn_apply_patch);
        applyBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_select_apkpath) {
            FileSelectDialog dlg = new FileSelectDialog(getContext(), this, ".apk", "", null);
            dlg.show();
        } else if (id == R.id.btn_apply_patch) {
            this.apkPath = apkPathEt.getText().toString();
            ProcessingDialog dlg = new ProcessingDialog(getActivity(), new PatchProcessor(), -1);
            dlg.show();
        }
    }

    @Override
    public void fileSelectedInDialog(String filePath, String extraStr, boolean openFile) {
        apkPathEt.setText(filePath);
    }

    @Override
    public boolean isInterestedFile(String filename, String extraStr) {
        return filename.endsWith(".apk");
    }

    @Override
    public String getConfirmMessage(String filePath, String extraStr) {
        return null;
    }

    class PatchProcessor implements ProcessingDialog.ProcessingInterface {
        private String errMessage;
        private String odexPath;

        @Override
        public void process() throws Exception {
            ApkInfoParser parser = new com.gmail.heagoo.common.ApkInfoParser();
            ApkInfoParser.AppInfo info = ApkInfoParser.parse(getContext(), apkPath);
            if (info == null) {
                return;
            }

            String packageName = info.packageName;
            OdexPatcher patcher = new OdexPatcher(packageName);
            patcher.applyPatch(getActivity(), apkPath);

            odexPath = patcher.targetOdex;
            if (patcher.errMessage != null) {
                this.errMessage = patcher.errMessage;
                throw new Exception(errMessage);
            }
        }

        @Override
        public void afterProcess() {
            if (errMessage == null) {
                Toast.makeText(getContext(), "Patched to " + odexPath, Toast.LENGTH_LONG).show();
            }
        }
    }


}
