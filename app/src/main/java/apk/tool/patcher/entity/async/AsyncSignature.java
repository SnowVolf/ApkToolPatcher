package apk.tool.patcher.entity.async;

import android.content.res.AssetManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.afollestad.async.Action;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.cert.Certificate;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import apk.tool.patcher.App;
import apk.tool.patcher.util.StreamUtil;
import sun.security.pkcs.PKCS7;

public class AsyncSignature extends Action<Integer> {
    private static final String TAG = "AsyncSignature";
    public static String smali = ".smali";
    public static String xml = ".xml";
    private static byte[] signatures;
    private static String apli;

    private static byte[] getApkSignatureData(String apkFil) throws Exception {
        Log.d(TAG, "getApkSignatureData() called with: apkFil = [" + apkFil + "]");
        String file = apkFil.replaceAll("(\\.apk)?_src", ".apk");
        File apkFile = new File(file);
        ZipFile zipFile = new ZipFile(apkFile);
        Enumeration<ZipEntry> entries = (Enumeration<ZipEntry>) zipFile.entries();
        while (entries.hasMoreElements()) {
            ZipEntry ze = entries.nextElement();
            String name = ze.getName().toUpperCase();
            if (name.startsWith("META-INF/") && (name.endsWith(".RSA") || name.endsWith(".DSA"))) {
                PKCS7 pkcs7 = new PKCS7(StreamUtil.readBytes(zipFile.getInputStream(ze)));
                Certificate[] certs = pkcs7.getCertificates();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                DataOutputStream dos = new DataOutputStream(baos);
                dos.write(certs.length);
                for (int i = 0; i < certs.length; i++) {
                    byte[] data = certs[i].getEncoded();
                    System.out.printf("  --SignatureHash[%d]: %08x\n", i, Arrays.hashCode(data));
                    dos.writeInt(data.length);
                    dos.write(data);
                }
                return baos.toByteArray();
            }
        }
        throw new Exception("META-INF/XXX.RSA (DSA) file not found.");
    }

    @NonNull
    @Override
    public String id() {
        return "signature";
    }

    @Nullable
    @Override
    protected Integer run(String... params) throws InterruptedException {
        Log.d(TAG, "run() called with: params = [" + Arrays.toString(params) + "]");
        //Здесь вся тяжелая работа
        try {
            copyFolder(params[0] + "/smali", "signatureHack");
            prepareAppClass(params[0]);
            replaceAppClass(params[0] + "/AndroidManifest.xml");
        } catch (Exception err) {
            postError(err);
            err.printStackTrace();
        }
        postEvent(String.format(Locale.ENGLISH, "%d matches replaced", progress));
        //Возврат должен быть... любой
        return progress;
    }

    private void replaceAppClass(String filePath) {
        Log.d(TAG, "replaceAppClass() called with: filePath = [" + filePath + "]");
        String regexp = "<application (.+) android:name=\"([^\"]+)\"(.+)";
        Pattern pattern = Pattern.compile(regexp);
        String regexp3 = "<application(.+)>";
        Pattern pattern3 = Pattern.compile(regexp3);
        File fileToBeModified = new File(filePath); // путь до файла
        StringBuilder url = new StringBuilder();
        BufferedReader reader = null;
        FileWriter writer = null;
        try {
            reader = new BufferedReader(new FileReader(fileToBeModified));
            String line = reader.readLine();
            while (line != null) {
                url.append(line).append("\n");
                line = reader.readLine();
            }
            Matcher m = pattern.matcher(url.toString());
            if (m.find()) {
                progress++;
                postProgress(this, progress);
                apli = m.group(2);
                url = new StringBuilder(m.replaceAll("<application " + m.group(1) + " android:name=\"cc.binmt.signature.PmsHookApplication\"" + m.group(3) + ""));
                replaceAplication(filePath);
            } else {
                Matcher m2 = pattern3.matcher(url.toString());
                if (m2.find()) {
                    progress++;
                    postProgress(this, progress);
                    url = new StringBuilder(m2.replaceAll("<application" + m2.group(1) + " android:name=\"cc.binmt.signature.PmsHookApplication\">"));
                }
            }
            writer = new FileWriter(fileToBeModified);
            writer.write(url.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void copyFolder(String outPath, String basePath) {
        Log.d(TAG, "copyFolder() called with: outPath = [" + outPath + "], basePath = [" + basePath + "]");
        InputStream inputStream;
        AssetManager assetManager = App.get().getAssets();
        try {
            String[] assets = assetManager.list(basePath);
            for (String s : assets) {
                String[] tmp = assetManager.list(basePath + "/" + s);
                if (tmp.length > 0) {
                    File dir = new File(outPath + "/" + s);
                    dir.mkdir();
                    copyFolder(outPath + "/" + s, basePath + "/" + s);
                    continue;
                }
                byte[] inputBuffer = new byte[1000];
                int count;
                FileOutputStream f = new FileOutputStream(outPath + "/" + s);
                inputStream = assetManager.open(basePath + "/" + s);
                while ((count = inputStream.read(inputBuffer)) > 0)
                    f.write(inputBuffer, 0, count);
                f.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void prepareAppClass(String decodePath) throws Exception {
        Log.d(TAG, "prepareAppClass() called with: decodePath = [" + decodePath + "]");
        signatures = getApkSignatureData(decodePath);
        File initialFile = new File(decodePath + "/smali/cc/binmt/signature/PmsHookApplication.smali");
        InputStream fis = new FileInputStream(initialFile);
        String src = new String(StreamUtil.readBytes(fis), StandardCharsets.UTF_8);
        fis.close();
        src = src.replace("### Signatures Data ###", android.util.Base64.encodeToString(signatures, 0).replace("\n", "\\n"));
        OutputStream fos = new FileOutputStream(initialFile);
        fos.write(src.getBytes());
        fos.flush();
        fos.close();
    }

    private void replaceAplication(String filePath) {
        Log.d(TAG, "replaceAplication() called with: filePath = [" + filePath + "]");
        String filePath2 = filePath.replaceAll("/AndroidManifest.xml", "");
        String regexp = "Landroid/app/Application;";
        String apli2 = "L" + apli.replace(".", "/") + ";";

        Pattern pattern = Pattern.compile(regexp);

        File fileToBeModified = new File(filePath2 + "/smali/cc/binmt/signature/PmsHookApplication.smali");
        StringBuilder url = new StringBuilder();
        BufferedReader reader = null;
        FileWriter writer = null;
        try {
            reader = new BufferedReader(new FileReader(fileToBeModified));
            String line = reader.readLine();
            while (line != null) {
                url.append(line).append("\n");

                line = reader.readLine();
            }
            Matcher m = pattern.matcher(url.toString());

            if (m.find()) {
                url = new StringBuilder(m.replaceAll(apli2));
            }
            writer = new FileWriter(fileToBeModified);
            writer.write(url.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
