package ru.atomofiron.apknator.Managers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.widget.EditText;
import android.widget.LinearLayout;

import apk.tool.patcher.util.SysUtils;
import ru.atomofiron.apknator.Utils.Cmd;

public class KeyStoreManager {
    private Context co;
    private SharedPreferences sp;
    private AlertDialog passDialog;
    private AlertDialog aliasPassDialog;
    private EditText passView;
    private EditText aliasEditText;
    private EditText passEditText;

    private String path;
    private String action;

    public KeyStoreManager(Context context) {
        co = context;
        sp = SysUtils.SP(co);

        passView = new EditText(co);
        passView.setHint("store password");
        aliasEditText = new EditText(co);
        aliasEditText.setHint("alias");
        passEditText = new EditText(co);
        passEditText.setHint("store password");
        LinearLayout layout = new LinearLayout(co);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(aliasEditText);
        layout.addView(passEditText);

        passDialog = new AlertDialog.Builder(co)
                .setView(passView)
                .setCancelable(true)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        action1();
                    }
                }).create();
        aliasPassDialog = new AlertDialog.Builder(co)
                .setView(layout)
                .setCancelable(true)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        action2();
                    }
                }).create();
    }

    public void list(String path) {
        this.path = path;
        passDialog.show();
    }

    public void exportPK8(String path) {
        this.path = path;
        action = "%1$s/bin.sh keytool -keystore %2$s -exportcert -alias %3$s -storepass %4$s -file %5$s";
        aliasPassDialog.show();
    }

    public void exportX509(String path) {
        this.path = path;
        action = "%1$s/bin.sh keytool -keystore %2$s -exportcert -alias %3$s -storepass %4$s -file %5$s -rfc";
        aliasPassDialog.show();
    }

    public void importPK8(String path) {
        this.path = path;
        action = "%1$s/bin.sh keytool -keystore %2$s -importcert -alias %3$s -storepass %4$s -file %5$s -noprompt";
        aliasPassDialog.show();
    }

    public void importX509(String path) {
        this.path = path;
        action = "%1$s/bin.sh keytool -keystore %2$s -importcert -alias %3$s -storepass %4$s -file %5$s -noprompt";
        aliasPassDialog.show();
    }

    public void delete(String path) {
        this.path = path;
        action = "%1$s/bin.sh keytool -keystore %2$s -delete -alias %3$s -storepass %4$s";
        aliasPassDialog.show();
    }

    private void action1() {
        if (passView.getText().length() == 0)
            SysUtils.Toast(co, "Empty");
        else
            showMessage(Cmd.Exec(sp.getBoolean(SysUtils.PREFS_USE_ROOT, false), String.format("%1$s/bin.sh keytool -keystore %2$s -list -storepass %3$s",
                    SysUtils.getScriptsPath(co), path, passView.getText())).getResult());
    }

    private void action2() {
        if (aliasEditText.getText().length() == 0 || passEditText.getText().length() == 0)
            SysUtils.Toast(co, "Empty");
        else
            showMessage(Cmd.Exec(sp.getBoolean(SysUtils.PREFS_USE_ROOT, false), String.format(action, SysUtils.getScriptsPath(co), path,
                    aliasEditText.getText(), passEditText.getText(), getPath(path))).getResult());
    }

    private void showMessage(String str) {
        if (str != null && str.length() > 1)
            new AlertDialog.Builder(co)
                    .setMessage(str)
                    .setCancelable(true)
                    .setPositiveButton("OK", null)
                    .show();
    }

    private String getPath(String name) {
        return path.substring(0, path.lastIndexOf('/')) + "/" + name;
    }

}
