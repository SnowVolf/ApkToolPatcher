package apk.tool.patcher.entity.async;


import android.util.Log;

import com.afollestad.async.Action;

import java.util.ArrayList;
import java.util.Arrays;

import apk.tool.patcher.entity.async.ads.AsyncAds;
import apk.tool.patcher.entity.async.ads.AsyncAdsActivities;
import apk.tool.patcher.entity.async.ads.AsyncAdsCompanion1;
import apk.tool.patcher.entity.async.ads.AsyncAdsCompanion2;
import apk.tool.patcher.entity.async.ads.AsyncAdsCompanion3;
import apk.tool.patcher.entity.async.ads.AsyncAdsFallback;
import apk.tool.patcher.entity.async.ads.AsyncAdsFolder;
import apk.tool.patcher.entity.async.ads.AsyncAdsXml;

public class AsyncRepository {
    private static final String TAG = "AsyncRepository";
    private static AsyncRepository repository;

    public AsyncRepository() {
        repository = this;
    }

    public static AsyncRepository getInstance() {
        if (repository == null) {
            repository = new AsyncRepository();
        }
        return repository;
    }

    public ArrayList<Action<Integer>> findActionsByIds(String... ids) {
        Log.d(TAG, "findActionsByIds() called with: ids = [" + Arrays.toString(ids) + "]");
        ArrayList<Action<Integer>> actions = new ArrayList<>();
        for (String id : ids) {
            switch (id) {
                case "remove-ads":
                    actions.add(new AsyncAds());
                    actions.add(new AsyncAdsCompanion1());
                    actions.add(new AsyncAdsCompanion2());
                    actions.add(new AsyncAdsCompanion3());
                    actions.add(new AsyncAdsFallback());
                    actions.add(new AsyncAdsXml());
                    actions.add(new AsyncAdsFolder());
                    break;
                case "remove-ads-activities":
                    actions.add(new AsyncAdsActivities());
                    break;
                case "signature":
                    actions.add(new AsyncSignature());
                    break;
                case "obfuscation":
                    actions.add(new AsyncObfuscator());
                    break;
                case "unicode-to-utf":
                    actions.add(new AsyncUnescaper());
                    break;
                case "no-root":
                    actions.add(new AsyncNoRoot());
                    break;
                case "play-services":
                    actions.add(new AsyncPlayServices());
                    break;
                case "remove-locales":
                    actions.add(new AsyncLocalizer());
                    break;
                case "convert-dictionary":
                    actions.add(new AsyncDictionary());
                    break;
                case "decode-res-id":
                    actions.add(new AsyncIdDecoder());
                    break;
                case "disable-update":
                    actions.add(new AsyncUpdater());
                    break;
                case "remove-analytics":
                    actions.add(new AsyncAnalytics());
                    break;
                case "signature-fallback":
                    actions.add(new AsyncSignatureFallback());
                    break;
                case "startup-toast":
                    actions.add(new AsyncToast());
                    break;
                case "native-patcher":
                    actions.add(new AsyncNativePatcher());
                    break;
            }
        }
        return actions;
    }
}
