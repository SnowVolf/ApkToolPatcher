package ru.atomofiron.apknator.Managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import apk.tool.patcher.util.SysUtils;
import ru.atomofiron.apknator.Utils.Cmd;
//import ru.atomofiron.apknator.R;

public class ToolsManager {

    public static void updateLD(Context co) {
        String ld = SysUtils.getLdPath(co) + "/" + SysUtils.SP(co).getString(SysUtils.PREFS_LIBLD, "libld.so");
        SysUtils.Log("ld: " + ld);
        String linkld = SysUtils.getDataPath(co) + "/ld.so";

        Cmd.easyExec("rm " + linkld);
        Cmd.easyExec("cp " + ld + " " + linkld);
        Cmd.easyExec("chmod 0755 " + linkld);
    }

    public static String updateTools(Context co) {
        SysUtils.Log("updateTools()");
        SharedPreferences sp = SysUtils.SP(co);

        String toolsPathSdcard = sp.getString(SysUtils.PREFS_TOOLS_PATH_SDCARD, "");
        if (toolsPathSdcard.isEmpty())
            return "";
        String binPath = SysUtils.getBinPath(co);
        Cmd.easyExec("rm -r \"" + binPath + "\"");
        if (!SysUtils.needDir(new File(binPath)) || !SysUtils.needDir(new File(SysUtils.getScriptsPath(co))) || !SysUtils.needDir(new File(SysUtils.getLdPath(co))))
            return "Не могу настроить папку инструментария";
        Cmd.easyExec("chmod -R 0755 \"" + SysUtils.getFilesPath(co) + "\"");

        copyTools(co, toolsPathSdcard);
        liteReview(co);
        updateLD(co);
        return "";
    }

    public static void copyTools(Context co, String toolsPathSdcard) {
        SysUtils.Log("copyTools()");
        AssetManager assetManager = co.getAssets();
        String binPath = SysUtils.getBinPath(co);
        String frameworkPath = SysUtils.getTmpPath(co);
        String jarsPath = SysUtils.getJarsPath(co);
        String libPath = SysUtils.getLibPath(co);
        String scritsPath = SysUtils.getScriptsPath(co);
        String ldPath = SysUtils.getLdPath(co);

        Cmd.easyExec("rm \"" + jarsPath + "\"");
        Cmd.easyExec("rm " + libPath);
        Cmd.easyExec("ln -s \"" + toolsPathSdcard + "\" \"" + jarsPath + "\"");
        //Cmd.easyExec("ln -s \""+toolsPathSdcard+"/openjdk/lib\" \""+libPath+"\"");
        copyFolder3(toolsPathSdcard + "/openjdk/lib", libPath);

        FileManager.copyAssets(assetManager, "tools/bin", binPath);
        FileManager.copyFrom(new File(toolsPathSdcard + "/openjdk/bin"), binPath);
        FileManager.copyAssets(assetManager, SysUtils.DEBUG ? "tools/scripts-debug" : "tools/scripts", scritsPath);
        FileManager.copyAssets(assetManager, "ld", ldPath);

        Cmd.easyExec("chmod -R 0755 \"" + SysUtils.getToolsPath(co) + "\"");

        new File(frameworkPath).mkdir();
        Cmd.easyExec("chmod 0777 \"" + frameworkPath + "\"");
    }


    public static boolean copyFolder3(String from, String to) {
        try {
            File fFrom = new File(from);
            if (fFrom.isDirectory()) { // Если директория, копируем все ее содержимое
                createDir(to);
                String[] FilesList = fFrom.list();
                for (int i = 0; i < FilesList.length; i++)
                    if (!copyFolder3(from + "/" + FilesList[i], to + "/" + FilesList[i]))
                        return false; // Если при копировании произошла ошибка
            } else if (fFrom.isFile()) { // Если файл просто копируем его
                File fTo = new File(to);
                InputStream in = new FileInputStream(fFrom); // Создаем потоки
                OutputStream out = new FileOutputStream(fTo);
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close(); // Закрываем потоки
                out.close();
            }
        } catch (FileNotFoundException ex) { // Обработка ошибок
        } catch (IOException e) { // Обработка ошибок
        }
        return true; // При удачной операции возвращаем true
    }

    public static void createDir(String folder) {
        File f1 = new File(folder); //Создаем файловую переменную
        if (!f1.exists()) { //Если папка не существует
            f1.mkdirs();  //создаем её
        }
    }

    public static void liteReview(Context co) {
        SysUtils.Log("liteReview()...");
        String name;
        SharedPreferences sp = SysUtils.SP(co);

        File dir = new File(SysUtils.getJarsPath(co));
        File[] listFiles = dir.listFiles();
        if (dir.exists() && listFiles != null && dir.listFiles().length != 0) {
            for (File file : listFiles)
                if (file.isFile() && (name = file.getName()).endsWith(".jar")) {
                    if (name.startsWith(SysUtils.TOOL_APKTOOL)) {
                        if (sp.getString(SysUtils.TOOL_APKTOOL, "").isEmpty())
                            sp.edit().putString(SysUtils.TOOL_APKTOOL, name).apply();
                    } else if (name.startsWith(SysUtils.TOOL_SIGNAPK)) {
                        if (sp.getString(SysUtils.TOOL_SIGNAPK, "").isEmpty())
                            sp.edit().putString(SysUtils.TOOL_SIGNAPK, name).apply();
                    } else if (name.startsWith(SysUtils.TOOL_SMALI)) {
                        if (sp.getString(SysUtils.TOOL_SMALI, "").isEmpty())
                            sp.edit().putString(SysUtils.TOOL_SMALI, name).apply();
                    } else if (name.startsWith(SysUtils.TOOL_BAKSMALI)) {
                        if (sp.getString(SysUtils.TOOL_BAKSMALI, "").isEmpty())
                            sp.edit().putString(SysUtils.TOOL_BAKSMALI, name).apply();
                    } else if (name.startsWith(SysUtils.TOOL_DX)) {
                        if (sp.getString(SysUtils.TOOL_DX, "").isEmpty())
                            sp.edit().putString(SysUtils.TOOL_DX, name).apply();
                    }
                }
        } else
            SysUtils.Log("W T F jars");

        dir = new File(SysUtils.getBinPath(co));
        listFiles = dir.listFiles();

        if (dir.exists() && listFiles != null && dir.listFiles().length != 0) {
            for (File file : listFiles)
                if (file.isFile() && (name = file.getName()).startsWith(SysUtils.TOOL_AAPT) && sp.getString(SysUtils.TOOL_AAPT, "").isEmpty())
                    sp.edit().putString(SysUtils.TOOL_AAPT, name).apply();
        } else
            SysUtils.Log("W T F bin");
    }

    public static ToolSet fullReview(Context co) {
        SysUtils.Log("fullReview()...");
        String name;
        SharedPreferences sp = SysUtils.SP(co);
        SharedPreferences.Editor ed = sp.edit();

        Map<String, Tool> tools = new HashMap<>();
        for (String toolName : SysUtils.TOOLS_ARR)
            tools.put(toolName, new Tool());

        File dir = new File(SysUtils.getJarsPath(co));
        File[] listFiles = dir.listFiles();
        if (!dir.exists() || listFiles == null || dir.listFiles().length == 0)
            SysUtils.Log("W T F jars");
        else
            for (File file : listFiles) {
                if (file.isFile() && (name = file.getName()).endsWith(".jar")) {
                    if (name.startsWith(SysUtils.TOOL_APKTOOL)) {
                        tools.get(SysUtils.TOOL_APKTOOL).addViersion(name);
                        if (sp.getString(SysUtils.TOOL_APKTOOL, "").isEmpty())
                            ed.putString(SysUtils.TOOL_APKTOOL, name);
                    } else if (name.startsWith(SysUtils.TOOL_SIGNAPK)) {
                        tools.get(SysUtils.TOOL_SIGNAPK).addViersion(name);
                        if (sp.getString(SysUtils.TOOL_SIGNAPK, "").isEmpty())
                            ed.putString(SysUtils.TOOL_SIGNAPK, name);
                    } else if (name.startsWith(SysUtils.TOOL_SMALI)) {
                        tools.get(SysUtils.TOOL_SMALI).addViersion(name);
                        if (sp.getString(SysUtils.TOOL_SMALI, "").isEmpty())
                            ed.putString(SysUtils.TOOL_SMALI, name);
                    } else if (name.startsWith(SysUtils.TOOL_BAKSMALI)) {
                        tools.get(SysUtils.TOOL_BAKSMALI).addViersion(name);
                        if (sp.getString(SysUtils.TOOL_BAKSMALI, "").isEmpty())
                            ed.putString(SysUtils.TOOL_BAKSMALI, name);
                    } else if (name.startsWith(SysUtils.TOOL_DX)) {
                        tools.get(SysUtils.TOOL_DX).addViersion(name);
                        if (sp.getString(SysUtils.TOOL_DX, "").isEmpty())
                            ed.putString(SysUtils.TOOL_DX, name);
                    }
                }
            }
        dir = new File(SysUtils.getBinPath(co));
        listFiles = dir.listFiles();
        if (!dir.exists() || listFiles == null || dir.listFiles().length == 0)
            SysUtils.Log("W T F bin");
        else for (File file : listFiles)
            if (file.isFile() && (name = file.getName()).startsWith(SysUtils.TOOL_AAPT)) {
                tools.get(SysUtils.TOOL_AAPT).addViersion(name);
                if (sp.getString(SysUtils.TOOL_AAPT, "").isEmpty())
                    ed.putString(SysUtils.TOOL_AAPT, name);
            }
        ed.apply();
        return new ToolSet(tools);
    }

    private static class Tool {
        private ArrayList<String> versions = new ArrayList<>();

        ArrayList<String> getVersions() {
            return versions;
        }

        void addViersion(String name) {
            versions.add(name);
        }
    }

    public static class ToolSet {
        private Map<String, Tool> tools;

        ToolSet(Map<String, Tool> tools) {
            setTools(tools);
        }

        public void setTools(Map<String, Tool> tools) {
            this.tools = tools;
        }

        public ArrayList<String> getVersions(String name) {
            return tools.get(name).getVersions();
        }
    }
}
