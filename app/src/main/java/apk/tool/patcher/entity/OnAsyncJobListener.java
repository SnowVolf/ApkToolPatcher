package apk.tool.patcher.entity;

public interface OnAsyncJobListener {
    void onError(Exception e);

    void onJobFinished();
}