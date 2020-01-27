package apk.tool.patcher.entity.worker;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import java.util.ArrayList;

import apk.tool.patcher.App;

public class WorkerCaller {
    public void call(){
        OneTimeWorkRequest myWorkRequest = new OneTimeWorkRequest.Builder(TesetVorker.class).setInputData(new Data.Builder().putString("suka", "blyad").build()).build();
        ArrayList<WorkRequest> requests = new ArrayList<>();
        requests.add(myWorkRequest);
        requests.add(myWorkRequest);
        WorkManager.getInstance(App.get()).enqueue(requests);
    }
}
