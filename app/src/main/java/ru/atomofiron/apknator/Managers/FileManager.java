package ru.atomofiron.apknator.Managers;

import android.content.res.AssetManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import apk.tool.patcher.util.SysUtils;
import ru.atomofiron.apknator.Utils.Cmd;

public class FileManager {

    public static boolean isLink(String path) {
        return Cmd.SuExec("ls -ld \"" + path + "\"").getResult().startsWith("l");
    }

    private static void copy(File source, String destDir) {
        SysUtils.Log("copy()");
        if (source.isDirectory())
            for (File file : source.listFiles())
                copy(file, destDir + "/" + source.getName());
        else if (source.isFile())
            copyFile(source.getParent(), source.getName(), destDir, false);
    }

    static void copyFrom(File dir, String destDir) {
        SysUtils.Log("copyFrom() " + dir.getName());
        if (dir.isDirectory())
            for (File file : dir.listFiles())
                copy(file, destDir);
    }

    private static boolean deleteDir(File dir) {
        boolean ok = true;
        if (dir.isFile())
            return dir.delete();
        else {
            for (File file : dir.listFiles())
                if (!deleteDir(file)) {
                    ok = false;
                    SysUtils.Log("[ERROR] del " + file.getAbsolutePath());
                }
            ok = ok && dir.delete();
        }
        return ok;
    }

    private static boolean copyFile(String inputPath, String inputFile, String outputPath, boolean move) {
        SysUtils.Log("copyFile() " + inputFile);
        inputFile = "/" + inputFile;
        InputStream in;
        OutputStream out;
        File test = new File(outputPath + inputFile);
        if (test.exists() && test.length() == new File(inputPath + inputFile).length()) return true;
        try {
            if (!SysUtils.needDir(new File(outputPath))) {
                SysUtils.Log("error to copy to " + outputPath);
                return false;
            }
            in = new FileInputStream(inputPath + inputFile);
            out = new FileOutputStream(outputPath + inputFile);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) out.write(buffer, 0, read);
            in.close();
            out.flush();
            out.close();
            if (move && !new File(inputPath + inputFile).delete())
                SysUtils.Log("can't delete after copy for moving");
            return true;
        } catch (Exception e) {
            SysUtils.Log(e.toString());
            return false;
        }
    }

    static void copyAssets(AssetManager assetManager, String dir, String dest) {
        SysUtils.Log("copyAssets(): " + dir);

        InputStream in = null;
        OutputStream out = null;
        try {
            String[] assets = assetManager.list(dir);
            if (!dir.endsWith("/"))
                dir += "/";
            if (!dest.endsWith("/"))
                dest += "/";

            SysUtils.Log("list " + Arrays.toString(assets));
            for (String name : assets) {
                SysUtils.Log("name = " + name);
                in = assetManager.open(dir + name);
                out = new FileOutputStream(dest + name);
                byte[] buffer = new byte[1024];
                int read;
                while ((read = in.read(buffer)) != -1)
                    out.write(buffer, 0, read);
                in.close();
                out.flush();
                out.close();
                SysUtils.Log("copied " + name);
            }
        } catch (Exception e) {
            SysUtils.Log("dest " + Arrays.toString(new File(dest).listFiles()));
            SysUtils.Loge(e.toString());
            try {
                if (in != null) in.close();
                if (out != null) out.close();
            } catch (Exception ignored) {
            }
        }
    }

    public static class Filek {
        public static final int FILE = 0;
        private static final int DIR = 1;
        private static final int LINK = 2;

        private String parent = "";
        private String name = "";
        private long length = 0;
        private String size = "0B";
        private String dataTime = "";
        private int type = -1;
        private boolean readable = false;

        private Filek(File file) {
            parent = file.getParent();
            parent = file.getParent();
            name = file.getName();
            length = file.length();
            size = SysUtils.getHumanSize(length);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale("ru"));
            dataTime = dateFormat.format(new Date(file.lastModified()));
            type = file.isFile() ? FILE : file.isDirectory() ? DIR : LINK;
            readable = file.canRead();
        }

        public Filek(String parent, final String line) {
            initFromLine(parent, line);
        }

        private static String getName(String line) {
            if (line.length() < 56) {
                SysUtils.Log("[ERROR] incorrect file line");
                return null;
            }
            String control = line.substring(19);
            int offset = control.indexOf(" "); // если владелец или группа > 8
            control = line.substring(28 + offset);
            offset += control.indexOf(" ");
            line = line.substring(55 + offset);
            int index = line.indexOf(' ');
            if (index != -1) line = line.substring(0, index);
            return line;
        }

        public static ArrayList<Filek> getFileks(File[] files) {
            ArrayList<Filek> fileks = new ArrayList<>();
            for (File file : files)
                fileks.add(new Filek(file));
            return fileks;
        }

        public static void getFileks(File[] files, ArrayList<Filek> fileks) {
            fileks.clear();
            for (File file : files) {
                fileks.add(new Filek(file));
            }
        }

        // drwxrwxrwx root     root         1234 2016-08-01 15:35 name   -> aa
        public static File[] getFiles2(String parentPath) {
            String text = Cmd.SuExec("ls -ls \"" + parentPath + "\"").getResult();
            int index = text.indexOf("\n") + 1;
            if (!text.startsWith("total")) return new File[0];
            text = text.substring(index);
            String[] lines = text.split("\n");
            int lenght = lines.length;
            if (lines.length == 0 || lines[0].length() < 56 || !(text.startsWith("-") || text.startsWith("d") || text.startsWith("l")))
                return new File[0];
            File[] files = new File[lenght];
            for (int i = 0; i < lenght; i++) {
                files[i] = new File(parentPath + "/" + getName(lines[i]));
            }
            return files;
        }

        public static File[] getFiles(String parentPath) {
            String text = Cmd.SuExec("ls \"" + parentPath + "\"").getResultData();
            String[] lines = text.split("\n");
            int length;
            if (lines == null || (length = lines.length) == 0 || lines[0].length() == 0)
                return new File[0];
            File[] files = new File[length];
            for (int i = 0; i < length; i++) {
                SysUtils.Log("== " + lines[i]);
                files[i] = new File(parentPath + "/" + lines[i]);
            }
            return files;
        }

        private void initFromLine(String parent, String line) {
            // drwxrwxrwx root     root     00001234 2016-07-31 15:30 dir_name -> bla
            switch (line.substring(0, 1)) {
                case "-":
                    type = FILE;
                    break;
                case "d":
                    type = DIR;
                    break;
                case "l":
                    type = LINK;
                    break;
            }
            if (type == -1) return;
            name = getName(line);
            this.parent = parent;
            if (isFile()) {
                size = line.substring(28, 37);
                size = size.substring(size.lastIndexOf(' '));
                length = Long.parseLong(size);
                size = SysUtils.getHumanSize(length);
            }
            dataTime = line.substring(38, 55);
        }

        public String getName() {
            return name;
        }

        public String getAbsolutePath() {
            return parent + "/" + name;
        }

        public boolean canRead() {
            return readable;
        }

        public boolean isFile() {
            return type == FILE;
        }

        public boolean isDirectory() {
            return type == DIR;
        }

        public boolean isLink() {
            return type == LINK;
        }

        public double length() {
            return length;
        }

        public String size() {
            return size;
        }

        public String getDataTime() {
            return dataTime;
        }

        public String toString() {
            String t = "NONE";
            switch (type) {
                case FILE:
                    t = "FILE ";
                    break;
                case DIR:
                    t = "DIR  ";
                    break;
                case LINK:
                    t = "LINK ";
                    break;
            }
            String s = size + " ";
            while (s.length() < 9) s = " " + s;
            return t + dataTime + s + parent;
        }
    }

}
