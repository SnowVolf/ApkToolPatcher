package apk.tool.patcher.entity.async.ads;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.ArrayMap;

import com.afollestad.async.Action;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import apk.tool.patcher.util.Preferences;

public class AsyncAds extends Action<Integer> {
    private static final String TAG = "AsyncAds";
    public static String smali = ".smali";
    public static String xml = ".xml";
    public static String nol = "";

    @NonNull
    @Override
    public String id() {
        return "remove-ads";
    }

    @Nullable
    @Override
    protected Integer run(String... params) throws InterruptedException {
        Log.d(TAG, "run() called with: params = [" + Arrays.toString(params) + "]");
        //Здесь вся тяжелая работа
        try {
            ArrayMap<Pattern, String> pat = new ArrayMap<Pattern, String>();
            pat.put(Pattern.compile("invoke-.+Lcom/google/android/gms/(internal|ads).*;->addView\\([^\\)]*\\)V"), "invoke-static {}, Lapk/tool/patcher/RemoveAds;->Zero()V");
//            pat.put(Pattern.compile("invoke-.*(/adbuddiz/|/adcolony/|/addapptr/|/adjust/|/adincube/|/adknowledge/|/admarvel/|/admob/|/ads/|/adsdk/|/adserver/adview/|/aerserv/|/airpush/|/altamob/|/amazon/device/ads/|/android/ads/|/appAdForce/|/appbrain/|/appenda/|/applovin/|/appnext/|/appnexus/|/appodeal/|/appia/|/apprupt/|/apsalar/|/avocarrot/|/boxdigital/sdk/ad/|/branch/|/chartboost/|/cmcm/adsdk/|/crashlytics/|/duapps/ad|/facebook/ads/|/fabric/|/flurry/|/fyber/|/google/ads/|/google/android/gms/ads/|/google/android/gms/internal/|/greystripe/|/heyzap/|/hyprmx/|/inmobi/|/inneractive/|/instreamatic/|/integralads/|/ironsource/|/jirbo/|/jumptap/|/kochava/|/Leadbolt/|/localytics/|/loopme/|/madsdk/|/mdotm/|/mediabrix/|/millennialmedia/|/mngads/|/moat/|/mobclix/|/mobfox/sdk/|/mobvista/|/mologiq/analytics/|/moolah/|/montexi/|/mopub/|/my/target/|/nexage/|/onelouder/adlib/|/openx/|psm/advertising/|/pubmatic/|/revmob/|/shark/adsert/|/smaato/SOMA/|/smartadserver/|/startapp/|/tagmanager/|/tapjoy/|/unity3d/ads|/vdopia/|/vungle/|/xtify/android/sdk/|/yandex/mobile/ads/|/video/adsdk/|/zestadz/android/|/xinmei/adsdk/|ferp/android/ads/|ad/AdmobInterstitial|/ads/NativeAdView|ad/AdmobNative|ads/FullAdmob|NativeAdViewAdmob|InlineAd|/NativeInterstitial|ru/boxdigital/sdk/ad/).*->(hasVideoContent|addHtmlAdView|admob|animateAdView|bannerAdmobMainActivity|beginFetchAds|canBeUsed|displayDownloadImageAlert|doBannerClick|downloadAndDisplayImage|expandAd|fetchAd|forceShowInterstitial|handleShow|incrementMetric|initBanner|initializeAds|initializeAdSDK|internalLoadAd|load|loadAd|loadAdFromBid|loadAds|loadAssetIntoView|loadBanner|loadBannerAd|loadBanners|loadBlocksInfo|loadChildAds|loadCustomEvent|loadData|loadDataWithBaseURL|loadDoAfterService|loadFromServer|loadFullscreen|loadHtml|loadHtmlResponse|loadImages|loadImageView|loadIncentivizedAd|loadInterstitial|loadInterstitialAd|loadList|loadMidPoint|loadNativeAd|loadNativeAds|loadNextAd|loadNextMediatedAd|loadNonJavascript|loadRewardedVideo|loadUrl|loadVideo|loadVideoAds|loadVideoUrl|mraidVideoAddendumInterstitialShow|nativeAdLoaded|onEvent|playVideo|preload|preloadAd|preloadHtml|preloadNativeAdImage|preloadUrl|pushAdsToPool|refreshAds|requestAd|requestBannerAd|requestInterstitial|requestInterstitialAd|setNativeAppwallBanner|setupBanner|shouldShowInterstitial|show|showAd|showAds|showAdInActivity|showAdinternal|showAndRender|showAsInterstitial|showBanner|showBannerAbsolute|showBannerInPlaceholder|showContent|showCustomEventInterstitial|showFullscreen|showIncentivizedAd|showInterstitial|showInterstitialAd|showOfferWall|showMoPubBrowserForUrl|showNativeContentAdView|showNativeAppInstallAdView|showPopup|showPopupExit|showPoststitial|showPreparedVideoFallbackAd|showSplash|showVideo|showWebPage|startAdLoadUponLayout|startMetric|submitAndResetMetrics|isLoading|uploadReports)\\(.*\\)Z"), "invoke-static {}, Lapk/tool/patcher/RemoveAds;->Zero()Z");
//            pat.put(Pattern.compile("invoke-.*(/adbuddiz/|/adcolony/|/addapptr/|/adjust/|/adincube/|/adknowledge/|/admarvel/|/admob/|/ads/|/adsdk/|/adserver/adview/|/aerserv/|/airpush/|/altamob/|/amazon/device/ads/|/android/ads/|/appAdForce/|/appbrain/|/appenda/|/applovin/|/appnext/|/appnexus/|/appodeal/|/appia/|/apprupt/|/apsalar/|/avocarrot/|/boxdigital/sdk/ad/|/branch/|/chartboost/|/cmcm/adsdk/|/crashlytics/|/duapps/ad|/facebook/ads/|/fabric/|/flurry/|/fyber/|/google/ads/|/google/android/gms/ads/|/google/android/gms/internal/|/greystripe/|/heyzap/|/hyprmx/|/inmobi/|/inneractive/|/instreamatic/|/integralads/|/ironsource/|/jirbo/|/jumptap/|/kochava/|/Leadbolt/|/localytics/|/loopme/|/madsdk/|/mdotm/|/mediabrix/|/millennialmedia/|/mngads/|/moat/|/mobclix/|/mobfox/sdk/|/mobvista/|/mologiq/analytics/|/moolah/|/montexi/|/mopub/|/my/target/|/nexage/|/onelouder/adlib/|/openx/|psm/advertising/|/pubmatic/|/revmob/|/shark/adsert/|/smaato/SOMA/|/smartadserver/|/startapp/|/tagmanager/|/tapjoy/|/unity3d/ads|/vdopia/|/vungle/|/xtify/android/sdk/|/yandex/mobile/ads/|/video/adsdk/|/zestadz/android/|/xinmei/adsdk/|ferp/android/ads/|ad/AdmobInterstitial|/ads/NativeAdView|ad/AdmobNative|ads/FullAdmob|NativeAdViewAdmob|InlineAd|/NativeInterstitial|ru/boxdigital/sdk/ad/).*->(hasVideoContent|addHtmlAdView|admob|animateAdView|bannerAdmobMainActivity|beginFetchAds|canBeUsed|displayDownloadImageAlert|doBannerClick|downloadAndDisplayImage|expandAd|fetchAd|forceShowInterstitial|handleShow|incrementMetric|initBanner|initializeAds|initializeAdSDK|internalLoadAd|load|loadAd|loadAdFromBid|loadAds|loadAssetIntoView|loadBanner|loadBannerAd|loadBanners|loadBlocksInfo|loadChildAds|loadCustomEvent|loadData|loadDataWithBaseURL|loadDoAfterService|loadFromServer|loadFullscreen|loadHtml|loadHtmlResponse|loadImages|loadImageView|loadIncentivizedAd|loadInterstitial|loadInterstitialAd|loadList|loadMidPoint|loadNativeAd|loadNativeAds|loadNextAd|loadNextMediatedAd|loadNonJavascript|loadRewardedVideo|loadUrl|loadVideo|loadVideoAds|loadVideoUrl|mraidVideoAddendumInterstitialShow|nativeAdLoaded|onEvent|playVideo|preload|preloadAd|preloadHtml|preloadNativeAdImage|preloadUrl|pushAdsToPool|refreshAds|requestAd|requestBannerAd|requestInterstitial|requestInterstitialAd|setNativeAppwallBanner|setupBanner|shouldShowInterstitial|show|showAd|showAds|showAdInActivity|showAdinternal|showAndRender|showAsInterstitial|showBanner|showBannerAbsolute|showBannerInPlaceholder|showContent|showCustomEventInterstitial|showFullscreen|showIncentivizedAd|showInterstitial|showInterstitialAd|showOfferWall|showMoPubBrowserForUrl|showNativeContentAdView|showNativeAppInstallAdView|showPopup|showPopupExit|showPoststitial|showPreparedVideoFallbackAd|showSplash|showVideo|showWebPage|startAdLoadUponLayout|startMetric|submitAndResetMetrics|isLoading|uploadReports)\\(.*\\)V"), "invoke-static {}, Lapk/tool/patcher/RemoveAds;->Zero()V");
//            pat.put(Pattern.compile("\"ca-app-pub-\\d+([/~])\\d+\"|\".*doubleclick\\.net.*\"|\".*googleadservices\\.com.*\"|\".*pagead/ads.*\"|\".*googleads.*\"|\".*ad\\.doubleclick\\.net.*\"|\"http://unrcv\\.adkmob\\.com/rp/.*\"|\"https://www\\.googleapis\\.com/auth/games.*\"|\"https://sb-ssl\\.google\\.com/safebrowsing/clientreport/malware.*\"|\"https://proton\\.flurry\\.com/sdk/v1/config.*\"|\"http://data\\.flurry\\.com/aap\\.do.*\"|\"https://.*applovin\\.com.*\"|\"https://ach\\.appodeal\\.com/api/v0/android/crashes.*\"|\"https://ad\\.mail\\.ru/mobile.*\"|\"https://analytics\\.mopub\\.com/i/jot/exchange_client_event.*\"|\"https://api\\.pubnative\\.net/api/partner/v2/promotions/native/video.*\"|\"https://certificate\\.mobile\\.yandex\\.net/api/v1/pins.*\"|\"https://code\\.google\\.com/p/android/issues/detail?id=.*\"|\"https://data\\.flurry\\.com.*\"|\"https://data\\.flurry\\.com/aap\\.do.*\"|\"https://data\\.flurry\\.com/pcr\\.do.*\"|\"https://dwxjayoxbnyrr\\.cloudfront\\.net/amazon-ads\\.viewablejs.*\"|\"https://e\\.crashlytics\\.com/spi/v2/events.*\"|\"https://impact\\.applifier\\.com/mobile/campaigns.*\"|\"https://impact.staging\\.applifier\\.com/mobile/campaigns.*\"|\"https://live\\.chartboost\\.com.*\"|\"https://pagead2\\.googlesyndication\\.com/pagead/gen_204.*\"|\"https://r\\.my\\.com/mobile.*\"|\"https://rri\\.appodeal\\.com/api/stat.*\"|\"https://s3\\.amazonaws\\.com/appodeal-externallibs/android/ima3\\.js\\.*\"|\"https://settings\\.crashlytics\\.com/spi/v2/platforms/android/apps/%s/settings.*\"|\"https://www\\.mopub\\.com.*\"|\"https://startup\\.mobile\\.yandex\\.net/\"|\"https://ad\\.mail\\.ru/mobile/\"|\"https://r\\.my\\.com/mobile/\"|\"https://i\\.l\\.inmobicdn\\.net/sdk/sdk/500/android/mraid\\.js\"|\"https://data\\.flurry\\.com/aap\\.do\"|\"http://data\\.flurry\\.com/aap\\.do"), "\"fuck\"");
            removeAds(params[0], pat);
        } catch (Exception err) {
            postError(err);
            err.printStackTrace();
        }
        postEvent(String.format(Locale.ENGLISH, "%d matches replaced", progress));
        //Возврат должен быть... любой
        return progress;
    }

    //ЗАМЕНА В СМАЛИ
    private void removeAds(String directoryName, ArrayMap<Pattern, String> pat) {
        Log.d(TAG, "removeAds() called with: directoryName = [" + directoryName + "], pat = [" + pat + "]");
        File directory = new File(directoryName);
        byte[] bytes;
        BufferedInputStream buf;
        String content = nol;
        FileOutputStream Out;
        OutputStreamWriter OutWriter;
        Matcher mat;
        boolean saveFile = false;
        File[] fList = directory.listFiles();
        for (File file : fList) {
            if (file.isFile()) {
                if (file.getAbsolutePath().endsWith(smali) && !Preferences.hasExcludedPackage(file.getAbsolutePath())) {
                    try {
                        bytes = new byte[(int) file.length()];
                        buf = new BufferedInputStream(new FileInputStream(file));
                        buf.read(bytes, 0, bytes.length);
                        buf.close();
                        content = new String(bytes);
                        for (ArrayMap.Entry<Pattern, String> mEntry : pat.entrySet()) {

                            mat = mEntry.getKey().matcher(content);
                            if (mat.find()) {
                                Log.d(TAG, "removeAds: deleting ads");
                                progress++;
                                if (ETA + 20 < progress) {
                                    postProgress(this, progress);
                                    ETA = progress;
                                }
                                content = mat.replaceAll(mEntry.getValue());
                                saveFile = true;
                            }
                        }
                        if (saveFile) {
                            Out = new FileOutputStream(file);
                            OutWriter = new OutputStreamWriter(Out);
                            OutWriter.append(content);
                            OutWriter.close();
                            Out.flush();
                            Out.close();
                            saveFile = false;
                        }
                    } catch (Exception e) {
                        System.out.println(e.toString());
                        postEvent(e.getMessage());
                    }
                }
            } else if (file.isDirectory()) {
                removeAds(file.getAbsolutePath(), pat);
            }
        }
    }
}
