package ru.atomofiron.apknator.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Locale;

import apk.tool.patcher.util.SysUtils;

public class CommandFactory {

    private SharedPreferences sp;
    private String binPath;
    private String jarsPath;
    private String scriptsPath;
    private String tmpPath;

    public CommandFactory(Context co) {
        sp = SysUtils.SP(co);
        binPath = SysUtils.getBinPath(co);
        jarsPath = SysUtils.getJarsPath(co);
        scriptsPath = SysUtils.getScriptsPath(co);
        tmpPath = SysUtils.getTmpPath(co);
    }

    public String getDecompleApkCommand(String uri, String tool, int mode) {
        String[] key = {"", " -r ", " -s "};
        return String.format("%1$s/bin.sh java -jar -Xmx1024m -Djava.io.tmpdir=%2$s \"%3$s/%4$s\" d -f %5$s \"%6$s\" -o \"%7$s_src\"",
                scriptsPath, tmpPath, jarsPath, tool, key[mode], uri, uri.substring(0, uri.length() - 4));

    }

    public String getCompileApkCommand(String uri, String tool, String aapt) {
        return String.format("%1$s/bin.sh java -jar -Xmx1024m -Djava.io.tmpdir=%2$s \"%3$s/%4$s\" b -f -a \"%5$s/%6$s\" \"%7$s\" -o \"%8$s.apk\"",
                scriptsPath, tmpPath, jarsPath, tool, binPath, aapt, uri, uri);

    }

    public String getDecompileJarCommand(String uri, String tool) {
        return String.format("%1$s/bin.sh java -jar -Xmx1024m -Djava.io.tmpdir=%2$s \"%3$s/%4$s\" d -f \"%5$s\" -o \"%6$s_jar\"",
                scriptsPath, tmpPath, jarsPath, tool, uri, uri.substring(0, uri.length() - 4));
    }

    public String getCompileJarCommand(String uri, String smali) {
        return String.format("%1$s/bin.sh java -jar \"%2$s/%3$s\" a \"%4$s\" -o \"%5$s.dex\" " +
                        "&& %6$s/7za a -w%7$s -tzip \"%8$s.jar\" \"%9$s.dex\" && rm \"%10$s.dex\"",
                scriptsPath, jarsPath, smali, uri, uri,
                binPath, SysUtils.getParentPath(uri), uri, uri, uri);
    }

    public String getSignCommand(String uri, String tool) {
        return String.format("%1$s/bin.sh java -jar \"%2$s/%3$s\" \"%4$s/x509\" \"%4$s/pk8\" \"%5$s\" \"%6$s/%7$s_sign.%8$s\"",
                scriptsPath, jarsPath, tool, jarsPath, uri, SysUtils.getParentPath(uri), SysUtils.getFileTitle(uri), SysUtils.getFormat(uri));
    }

    public String getDecompileDexCommand(String uri, String tool) {
        return String.format(Locale.ENGLISH, "%1$s/bin.sh java -jar \"%2$s/%3$s\" d -a %4$d \"%5$s\" -o \"%6$s_dex\"",
                scriptsPath, jarsPath, tool, SysUtils.getApiLavel(sp), uri, uri.substring(0, uri.length() - 4));
    }

    public String getDecompileOdexCommand(String uri, String tool) {
        return String.format(Locale.ENGLISH, "%1$s/bin.sh java -jar \"%2$s/%3$s\" deodex -a %4$d \"%5$s\" -o \"%6$s_odex\"",
                scriptsPath, jarsPath, tool, SysUtils.getApiLavel(sp), uri, uri.substring(0, uri.lastIndexOf('.')));
    }

    public String getCompileDexCommand(String uri, String tool) {
        return String.format(Locale.ENGLISH, "%1$s/bin.sh java -jar \"%2$s/%3$s\" a -a %4$d \"%5$s\" -o \"%6$s.dex\"",
                scriptsPath, jarsPath, tool, SysUtils.getApiLavel(sp), uri, uri);
    }

    public String getDxCommand(String uri, String tool) {
        return String.format("%1$s/bin.sh java -jar \"%2$s/%3$s\" --dex --output=\"%4$s.dex\"  \"%5$s\"",
                scriptsPath, jarsPath, tool, uri.substring(0, uri.length() - 6), uri);
    }

    public String getJavacCommand(String uri) {
        return String.format("%1$s/bin.sh javac \"%2$s\"", scriptsPath, uri);
    }

    public String getZipalignCommand(String uri) {
        return String.format("%1$s/zipalign -f -v 4 \"%2$s\" \"%3$s_zipalign.%4$s\"",
                binPath, uri, SysUtils.getWithoutFormat(uri), SysUtils.getFormat(uri));
    }

    public String getExtractCommand(String uri, String what) {
        return String.format("%1$s/7za x -w\"%2$s\" -tzip \"%3$s\" \"%4$s\"",
                binPath, SysUtils.getParentPath(uri), uri, what);
    }

    public String getArchDeleteCommand(String uri, String what) {
        return String.format("%1$s/7za d -w\"%2$s\" -tzip \"%3$s\" \"%4$s\"",
                binPath, SysUtils.getParentPath(uri), uri, what);
    }

    public String getArchiveCommand(String uri, String what) {
        return String.format("%1$s/7za a -w\"%2$s\" -tzip \"%3$s\" \"%4$s/%5$s\"",
                binPath, SysUtils.getParentPath(uri), uri, SysUtils.getParentPath(uri), what);
    }

    public String getImportCommand(String uri, String tool) {
        return String.format("%1$s/bin.sh java -jar -Xmx1024m -Djava.io.tmpdir=%2$s \"%3$s/%4$s\" if \"%5$s\"",
                scriptsPath, tmpPath, jarsPath, tool, uri);
    }

    public String getOdexCommand(String uri) {
        return String.format("%1$s/dexopt-wrapper \"%2$s\" \"%3$s.odex\"",
                binPath, uri, SysUtils.getWithoutFormat(uri));
    }

    public String getPsCommand(String target, String path) {
        return String.format("ps | grep %1$s > %2$s/ps.txt && cat %2$s/ps.txt", target, path);
    }

    public String getExtractClassesCommand(String path, String tool) {
        return String.format("%1$s/bin.sh java -jar \"%2$s/%3$s\" l c \"%4$s\"",
                scriptsPath, jarsPath, tool, path);
    }

    public String getExtractClassCommand(String path, String tool) {
        return String.format("%1$s/bin.sh java -jar \"%2$s/%3$s\" du \"%4$s\"",
                scriptsPath, jarsPath, tool, path);
    }
}
