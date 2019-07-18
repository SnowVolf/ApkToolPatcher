package apk.tool.patcher.util;

import java.util.regex.Pattern;

public class RegexpRepository {
    private static RegexpRepository repository;
    private final String mapsPattern = "<meta-data android:name=\"com\\.google\\.android\\.(geo|maps\\.v2)\\.API_KEY\" android:value=(.+)/>";
    public Pattern MAPS_KEY = Pattern.compile(mapsPattern);
    private String adsActivities = "<activity(.+)(\\.ads\\.|adwhirl|amobee|burstly|com\\.adknowledge\\.|cauly\\.android\\.ad\\.|\\.greystripe\\.|inmobi\\.|inneractive\\.api\\.ads\\.|\\.jumptap\\.adtag\\.|medialets\\.advertising\\.|\\.millennialmedia\\.android\\.|\\.mobclix\\.android\\.sdk\\.|\\.mobfox\\.sdk\\.|\\.adserver\\.adview\\.|\\.mopub\\.mobileads\\.|com\\.oneriot\\.|\\.papaya\\.offer\\.|pontiflex\\.mobile\\.webview\\.sdk\\.activities|\\.qwapi\\.adclient\\.android\\.view\\.|\\.smaato\\.SOMA\\.|\\.vdopia\\.client\\.android\\.|\\.zestadz\\.android\\.|com\\.appenda\\.|com\\.airpush\\.android\\.|com\\.Leadbolt\\.|com\\.moolah\\.|com\\.tapit\\.adview\\.notif\\.|com\\.urbanairship\\.push\\.|com\\.xtify\\.android\\.sdk\\.|MediaPlayerWrapper|\\.vungle\\.|\\.tapjoy\\.|\\.nbpcorp\\.|com\\.appenda\\.|\\.plus1\\.sdk\\.|\\.adsdk\\.|\\.mdotm\\.|AdView|mad\\.ad\\.)(.+)/>";
    public Pattern ADS_ACTS = Pattern.compile(adsActivities);

    public RegexpRepository() {
        repository = this;
    }

    public static RegexpRepository get() {
        if (repository == null) {
            repository = new RegexpRepository();
        }
        return repository;
    }

}
