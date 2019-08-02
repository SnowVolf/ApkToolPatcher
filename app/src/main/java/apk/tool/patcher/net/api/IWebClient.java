package apk.tool.patcher.net.api;


import java.util.regex.Pattern;

/**
 * Created by radiationx on 26.03.17.
 */

public interface IWebClient {
    Pattern countsPattern = Pattern.compile("<a href=\"(?:https?)?\\/\\/4pda\\.ru\\/forum\\/index\\.php\\?act=mentions[^>]*?><i[^>]*?>(\\d+)<\\/i>[\\s\\S]*?act=fav[^>]*?><i[^>]*?>(\\d+)<\\/i>[\\s\\S]*?act=qms[^>]*?data-count=\"(\\d+)\">");
    Pattern errorPattern = Pattern.compile("^[\\s\\S]*?wr va-m text\">([\\s\\S]*?)</div></div></div></div><div class=\"footer\">");

    NetworkResponse get(String url) throws Exception;

    NetworkResponse request(NetworkRequest request) throws Exception;

    NetworkResponse request(NetworkRequest request, ProgressListener progressListener) throws Exception;

    String getAuthKey();

    void clearCookies();

    interface ProgressListener {
        void onProgress(int percent);
    }
}
