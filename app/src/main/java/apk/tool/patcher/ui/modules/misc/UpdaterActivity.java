package apk.tool.patcher.ui.modules.misc;

import android.os.Bundle;
import android.widget.Button;

import apk.tool.patcher.R;
import ru.svolf.melissa.fragment.dialog.SweetWaitDialog;
import ru.svolf.melissa.swipeback.SwipeBackActivity;
import ru.svolf.melissa.swipeback.SwipeBackLayout;

public class UpdaterActivity extends SwipeBackActivity {
    private SweetWaitDialog waitDialog = null;
    private Button refresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updater);
        setEdgeLevel(SwipeBackLayout.EdgeLevel.MED);
    }

//    @SuppressLint("CheckResult")
//    private void refreshInfo() {
//        Observable.fromCallable(() -> {
//            NetworkResponse response = Client.get().get(mValue);
//            String body;
//            body = response.getBody();
//            return body;
//        })
//                .onErrorReturn(throwable -> "pizda rulyi")
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(this::checkSource);
//    }
//
//    private void checkSource(String jsonSource) {
//        setRefreshing(false);
//        if (jsonSource.length() == 0) {
//            return;
//        }
//        try {
//            final JSONObject jsonBody = new JSONObject(jsonSource);
//            final JSONObject updateObject = jsonBody.getJSONObject("update");
//            checkUpdate(updateObject);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void setRefreshing(boolean refresh) {
//        if (refresh && waitDialog == null) {
//            waitDialog = new SweetWaitDialog(this);
//            waitDialog.show();
//        } else if (waitDialog != null){
//                waitDialog.dismiss();
//        }
//    }

}
