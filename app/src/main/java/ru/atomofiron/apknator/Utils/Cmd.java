package ru.atomofiron.apknator.Utils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import apk.tool.patcher.util.SysUtils;

public class Cmd {

    public static boolean init(boolean su) {
        try {
            //Runtime.getRuntime().exec(su ? "su" : "sh").destroy();
            Process exec = Runtime.getRuntime().exec("sh");
            DataOutputStream dos = new DataOutputStream(exec.getOutputStream());
            dos.writeBytes(su ? "su" : "sh");
            dos.flush();
            dos.close();
            int code = exec.waitFor();
            dos.close();
            exec.destroy();
            return code == 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static ResultData Exec(String command) {
        SysUtils.Log("Exec(): " + command);
        return Exec(false, command);
    }

    public static ResultData SuExec(String command) {
        return Exec(true, command);
    }

    public static ResultData Exec(boolean su, String command) {
        SysUtils.Log("Exec(): " + command);
        return Exec(su, SysUtils.rmSlashN(command).split("\n"));
    }

    private static ResultData Exec(boolean su, String[] commands) {
        Process exec = null;
        InputStream execIn = null;
        InputStream execErr = null;
        OutputStream execOs = null;

        ResultData resultData;
        try {
            exec = Runtime.getRuntime().exec(su ? "su" : "sh");
            execIn = exec.getInputStream();
            execErr = exec.getErrorStream();
            execOs = exec.getOutputStream();
            DataOutputStream dos = new DataOutputStream(execOs);
            for (String com : commands)
                if (!com.isEmpty()) {
                    dos.writeBytes(com + "\n");
                    dos.flush();
                }
            dos.close();

            resultData = new ResultData(exec.waitFor(), inputStream2String(execIn, "utf-8"), inputStream2String(execErr, "utf-8"));
        } catch (Exception e) {
            SysUtils.Log(e.toString());
            resultData = new ResultData(-1, "", e.toString());
        } finally {
            try {
                if (execIn != null) execIn.close();
                if (execErr != null) execErr.close();
                if (execOs != null) execOs.close();
                if (exec != null) exec.destroy();
            } catch (Exception ignored) {
            }
        }
        return resultData;
    }

    public static void easyExec(String command) {
        easyExec(false, command);
    }

    public static void easySuExec(String command) {
        easyExec(true, command);
    }

    public static int easyExec(boolean su, String command) {
        while (command.contains("\n\n")) command = command.replace("\n\n", "\n");
        if (command.startsWith("\n")) command = command.substring(1);
        if (command.endsWith("\n")) command = command.substring(0, command.length() - 1);

        int code = -1;
        Process exec = null;
        OutputStream execOs = null;
        try {
            exec = Runtime.getRuntime().exec(su ? "su" : "sh");
            execOs = exec.getOutputStream();
            DataOutputStream dos = new DataOutputStream(execOs);
            for (String com : command.split("\n"))
                if (!com.isEmpty()) {
                    dos.writeBytes(com + "\n");
                    dos.flush();
                }
            dos.close();
            code = exec.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (execOs != null) execOs.close();
                if (exec != null) exec.destroy();
            } catch (Exception ignored) {
            }
        }
        return code;
    }

    public static String inputStream2String(InputStream in, String encoding) throws Exception {
        StringBuilder out = new StringBuilder();
        InputStreamReader inread = new InputStreamReader(in, encoding);
        char[] b = new char[1024];
        int n;
        while ((n = inread.read(b)) != -1) {
            String s = new String(b, 0, n);
            out.append(s);
            //if (n < 1024) break;
        }
        return out.toString();
    }

/*
    public static void Exec(String command) {
		SysUtils.Log("Exec()");
		command = command.replace("\'","");
		SysUtils.Log("Exec: "+command);
		Process exec = null;
		DataOutputStream processOutput = null;
		try {
			exec = Runtime.getRuntime().exec(command);
			InputStream execErr = exec.getErrorStream();
			InputStream execIn = exec.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(execErr));
			String str;
			SysUtils.Log("while readLine()");
			while ((str = br.readLine()) != null) SysUtils.Log("Exec: "+str);
			exec.waitFor();
			SysUtils.Log("End Exec: "+ Cmd.inputStream2String(execIn, "utf-8"));
			SysUtils.Log("Err Exec: "+ Cmd.inputStream2String(execErr, "utf-8"));
		} catch (Exception e) {
			SysUtils.Log("Exc Exec:"+e.getMessage());
			return;
		} finally {
			try {
				if (processOutput != null) processOutput.close();
				exec.destroy();
			} catch (Exception e) {e.printStackTrace();}
		}
		SysUtils.Log("Exec END");
	}
	public static void SuExec(String command) {
		SysUtils.Log("SuExec()");
		Process exec = null;
		DataOutputStream outputStream = null;

		try {
			exec = Runtime.getRuntime().exec(new String[]{"su","-c",command+"\n"});
			InputStream execErr = exec.getErrorStream();
			InputStream execIn = exec.getInputStream();
			outputStream = new DataOutputStream(exec.getOutputStream());
			SysUtils.Log("writeBytes... ");
			outputStream.writeBytes(command + "\n");
			SysUtils.Log("writeBytes... ");
			outputStream.writeBytes("exit\n");
			SysUtils.Log("outputStream.flush()");
			outputStream.flush();
			BufferedReader br = new BufferedReader(new InputStreamReader(execErr));

			String str;
			SysUtils.Log("br.readLine()");
			while ((str = br.readLine()) != null) SysUtils.Log("SuzExec: "+str);
			SysUtils.Log("exec.waitFor()");
			exec.waitFor();
			SysUtils.Log("Waited!");
			//SysUtils.Log("while readLine()");
			//while ((str = br.readLine()) != null) SysUtils.Log("Exec: "+str);
			//SysUtils.Log("End Exec: "+ Cmd.inputStream2String(suIn, "utf-8"));
			//SysUtils.Log("Err Exec: "+ Cmd.inputStream2String(suErr, "utf-8"));
			//SysUtils.Log("End Exec: "+ read(suIn));
			//SysUtils.Log("Err Exec: "+ read(suErr));
		} catch (Exception e) {
			SysUtils.Log("Exc Exec:"+e.getMessage());
			e.printStackTrace();
		} finally {
			SysUtils.Log("finally");
			try {
				if (outputStream != null) outputStream.close();
				exec.destroy();
			} catch (Exception ignored) {}
		}
		SysUtils.Log("Exec END");
	}
*/

    public static String read(InputStream is) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) != -1)
                baos.write(buffer, 0, length);
            return baos.toString("UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return e.toString();
        }
    }

    public static String removeRepeatedChar(String s) {
        if (s == null || s.length() < 2)
            return s;

        StringBuilder sb = new StringBuilder();
        int i = 0, len = s.length();
        while (i < len) {
            char c = s.charAt(i);
            sb.append(c);
            i++;
            if (c == '/')
                while (i < len && s.charAt(i) == c)
                    i++;
        }
        return sb.toString();
    }

    public static class ResultData {
        private int resultCode;
        private String resultData;
        private String resultError;

        ResultData(int code, String info, String err) {
            resultCode = code;
            resultData = info == null ? "" : info;
            resultError = err == null ? "" : err;
        }

        public int getResultCode() {
            return resultCode;
        }

        public String getResultData() {
            return resultData;
        }

        public String getResultError() {
            return resultError;
        }

        public String getResult() {
            return toString();
        }

        public String toString() {
            return resultData + "\n" + resultError;
        }
    }
}


