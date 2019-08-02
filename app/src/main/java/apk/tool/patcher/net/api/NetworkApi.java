package apk.tool.patcher.net.api;

/**
 * Created by radiationx on 29.07.16.
 */
public class NetworkApi {
    public static  String CASINO = "http://l1l.pw/";
    private static NetworkApi INSTANCE = null;
    private static IWebClient webClient = null;

    public static void setWebClient(IWebClient webClient) {
        NetworkApi.webClient = webClient;
    }

    public static IWebClient getWebClient() {
        return webClient;
    }


    public static NetworkApi get() {
        if (INSTANCE == null) INSTANCE = new NetworkApi();
        return INSTANCE;
    }
}
