package ru.atomofiron.apknator.Managers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;

import apk.tool.patcher.R;
import apk.tool.patcher.ui.modules.decompiler.ApkToolActivity;
import apk.tool.patcher.util.SysUtils;
import apk.tool.patcher.util.TextUtil;
import ru.atomofiron.apknator.Utils.Cmd;
import ru.atomofiron.apknator.Utils.CommandFactory;

public class TaskManager {

    private Context co;
    private ApkToolActivity mainActivity;
    private SharedPreferences sp;
    private MessageHandler messageHandler = new MessageHandler();
    private SysUtils.ActionListener actionListener;

    private int count = 100;
    private Task tasks[] = new Task[]{null, null, null, null};
    private ArrayList<Integer> forgotted = new ArrayList<>();
    private String scriptsPath;
    private String jarsPath;
    private String binPath;
    private boolean userFarAway = false;
    private CommandFactory commandFactory;
    private TaskListener taskListener;

    public TaskManager(ApkToolActivity activity, SysUtils.ActionListener listener) {
        mainActivity = activity;
        co = activity;
        sp = SysUtils.SP(mainActivity);
        actionListener = listener;
        commandFactory = new CommandFactory(co);

        scriptsPath = SysUtils.getScriptsPath(co);
        jarsPath = SysUtils.getJarsPath(co);
        binPath = SysUtils.getBinPath(co);
        String appSystemName = Cmd.Exec("ls -ld " + SysUtils.getDataPath(co)).getResult();
        if (appSystemName.length() < 33 || appSystemName.charAt(10) != ' ' || appSystemName.charAt(11) == ' ')
            return;
        SysUtils.Log(appSystemName.split("\n")[0]);
        appSystemName = appSystemName.split(" ")[1];
        SysUtils.Log("appSystemName = " + appSystemName);
    }

    public void onStop(boolean value) {
        userFarAway = value;
    }

    private String getString(int id) {
        return mainActivity.getString(id);
    }

    private boolean checkTool(String tool, String name) {
        SysUtils.Log("tool = " + tool);
        if (tool.isEmpty()) {
            mainActivity.snack(mainActivity.getString(R.string.no_tool, name));
            return false;
        }
        return true;
    }

    public void startDecompleApk(String uri, int mode) {
        SysUtils.Log("startDecompleApk()");
        String tool = sp.getString(SysUtils.TOOL_APKTOOL, "");
        if (!checkTool(tool, SysUtils.TOOL_APKTOOL))
            return;

        String command = commandFactory.getDecompleApkCommand(uri, tool, mode);
        new Task(mainActivity, uri, getString(R.string.decompiling), command, R.string.decompile_all_finish).start();
    }

    public void startCompileApk(String uri) {
        SysUtils.Log("startCompileApk()");
        String aapt = sp.getString(SysUtils.TOOL_AAPT, "");
        if (!checkTool(aapt, SysUtils.TOOL_AAPT))
            return;
        String apktool = sp.getString(SysUtils.TOOL_APKTOOL, "");
        if (!checkTool(apktool, SysUtils.TOOL_APKTOOL))
            return;

        String command = commandFactory.getCompileApkCommand(uri, apktool, aapt);
        Task task = new Task(mainActivity, uri, getString(R.string.recompiling), command, R.string.recompile_finish);
        task.signable = true;
        task.start();
    }

    public void startDecompileJar(String uri) {
        SysUtils.Log("startDecompileJar()");
        String tool = sp.getString(SysUtils.TOOL_APKTOOL, "");
        if (!checkTool(tool, SysUtils.TOOL_APKTOOL))
            return;

        String command = commandFactory.getDecompileJarCommand(uri, tool);
        new Task(mainActivity, uri, getString(R.string.decompiling), command, R.string.decompile_all_finish).start();
    }

    public void startCompileJar(String uri) {
        SysUtils.Log("startCompileJar()");
        String smali = sp.getString(SysUtils.TOOL_SMALI, "");
        if (!checkTool(smali, SysUtils.TOOL_SMALI))
            return;

        String command = commandFactory.getCompileJarCommand(uri, smali);
        new Task(mainActivity, uri, getString(R.string.recompiling), command, R.string.recompile_finish).start();
    }

    public void startSign(String uri) {
        SysUtils.Log("startSign()");
        String command = getSignCommand(uri);

        if (!command.isEmpty())
            new Task(mainActivity, uri, getString(R.string.signing), command, R.string.sign_finish).start();
    }

    private String getSignCommand(String uri) {
        String tool = sp.getString(SysUtils.TOOL_SIGNAPK, "");
        if (!checkTool(tool, SysUtils.TOOL_SIGNAPK))
            return "";

        return commandFactory.getSignCommand(uri, tool);
    }

    public void startDecompileOdex(String uri) {
        SysUtils.Log("startDecompileOdex()");
        String bakslami = sp.getString(SysUtils.TOOL_BAKSMALI, "");
        if (!checkTool(bakslami, SysUtils.TOOL_BAKSMALI))
            return;

        String command = commandFactory.getDecompileOdexCommand(uri, bakslami);
        new Task(mainActivity, uri, getString(R.string.decompiling), command, R.string.decompile_odex_finish).start();
    }

    public void startCompileDex(String uri) {
        SysUtils.Log("startCompileDex()");
        String smali = sp.getString(SysUtils.TOOL_SMALI, "");
        if (!checkTool(smali, SysUtils.TOOL_SMALI))
            return;

        String command = commandFactory.getCompileDexCommand(uri, smali);
        new Task(mainActivity, uri, getString(R.string.recompiling), command, R.string.recompile_finish).start();
    }

    public void startDecompileDex(String uri) {
        SysUtils.Log("startDecompileDex()");
        String bakslami = sp.getString(SysUtils.TOOL_BAKSMALI, "");
        if (!checkTool(bakslami, SysUtils.TOOL_BAKSMALI))
            return;

        String command = commandFactory.getDecompileDexCommand(uri, bakslami);
        new Task(mainActivity, uri, getString(R.string.decompiling), command, R.string.decompile_dex_finish).start();
    }

    public void startDx(String uri) {
        SysUtils.Log("startCompileDex()");
        String dx = sp.getString(SysUtils.TOOL_DX, "");
        if (!checkTool(dx, SysUtils.TOOL_DX))
            return;

        String command = commandFactory.getDxCommand(uri, dx);
        new Task(mainActivity, uri, getString(R.string.recompiling), command, R.string.recompile_finish).start();
    }

    public void startJavac(String uri) {
        SysUtils.Log("startCompileDex()");
        String dx = sp.getString(SysUtils.TOOL_DX, "");
        if (!checkTool(dx, SysUtils.TOOL_DX))
            return;

        String command = commandFactory.getJavacCommand(uri);
        new Task(mainActivity, uri, getString(R.string.recompiling), command, R.string.recompile_finish).start();
    }

    public void startDelete(String uri, int answer) {
        SysUtils.Log("startDelete()");

        String command = String.format("rm -r \"%s\"", uri);
        new Task(mainActivity, uri, getString(R.string.deleting), command, answer).start();
    }

    public void startZipalign(String uri) {
        SysUtils.Log("startZipalign()");

        String command = commandFactory.getZipalignCommand(uri);
        new Task(mainActivity, uri, getString(R.string.aligning), command, R.string.zip_finish).start();
    }

    public void startExtract(String uri, String what, int answer) {
        SysUtils.Log("startArchive()");

        String command = commandFactory.getExtractCommand(uri, what);
        new Task(mainActivity, uri, getString(R.string.extracting), command, answer).start();
    }

    public void startArchDelete(String uri, String what, int answer) {
        SysUtils.Log("startArchive()");

        String command = commandFactory.getArchDeleteCommand(uri, what);
        new Task(mainActivity, uri, getString(R.string.extracting), command, answer).start();
    }

    public void startArchive(String uri, String what, int answer) {
        SysUtils.Log("startArchive()");

        String command = commandFactory.getArchiveCommand(uri, what);
        new Task(mainActivity, uri, getString(R.string.extracting), command, answer).start();
    }

    public void startImport(String uri) {
        SysUtils.Log("startImport()");
        String tool = sp.getString(SysUtils.TOOL_APKTOOL, "");
        if (!checkTool(tool, SysUtils.TOOL_APKTOOL))
            return;

        String command = commandFactory.getImportCommand(uri, tool);
        new Task(mainActivity, uri, getString(R.string.importing_framework), command, R.string.import_finish).start();
    }

    public void startOdex(String uri) { // НЕ РАБОТАЕТ
        SysUtils.Log("startOdex()");

        String command = commandFactory.getOdexCommand(uri);
        new Task(mainActivity, uri, getString(R.string.making), command, R.string.odex_created).start();
    }

    private void threadWork(final String uri, final String taskType, final String command, final int action, final Task task) {
        final boolean useRoot = sp.getBoolean(SysUtils.PREFS_USE_ROOT, false);
        SysUtils.Log("threadWork(): " + command);

        new Thread(new Runnable() {
            @Override
            public void run() {
                Bundle bundle = new Bundle();

                long timeMillis = System.currentTimeMillis();
                bundle.putString(SysUtils.FILENAME, SysUtils.getFullFileName(uri));
                bundle.putBoolean(SysUtils.FINISH, false);
                bundle.putInt(SysUtils.TASK_NUM, task.num);
                SysUtils.Log("putInt " + task.num);
                bundle.putInt(SysUtils.ACTION, action);

                String answer = "";
                Process exec = null;
                InputStream execIn = null;
                DataOutputStream dos = null;
                BufferedReader br = null;
                try {
                    exec = Runtime.getRuntime().exec(useRoot ? "su" : "sh");
                    execIn = exec.getInputStream();
                    dos = new DataOutputStream(exec.getOutputStream());

                    dos.writeBytes(command + " 2>&1\n");
                    dos.flush();
                    dos.close();

                    int pid;
                    try {
                        Field f = exec.getClass().getDeclaredField("pid");
                        f.setAccessible(true);
                        pid = f.getInt(exec);
                        f.setAccessible(false);
                    } catch (Throwable e) {
                        pid = -1;
                    }
                    SysUtils.Log("pid = " + pid);
                    task.pid = pid;
                    bundle.putInt(SysUtils.PID, pid);
                    if (useRoot) {
                        String text = Cmd.SuExec(commandFactory.getPsCommand(SysUtils.getBinPath(co) + "/" + task.binary, SysUtils.getFilesPath(co))).getResult();
                        if (!text.isEmpty())
                            for (String line : text.split("\n")) {
                                while (line.contains("  "))
                                    line = line.replace("  ", " ");
                                String[] words = line.split(" ");
                                if (words.length > 1) {
                                    boolean found = false;
                                    int cpid = Integer.parseInt(words[1]);
                                    for (Task task : tasks) // ищем вдруг этот cpid принадлежит другой задаче и уже был найден
                                        found = found || task.cpid == cpid;
                                    if (!found) {
                                        task.cpid = Integer.parseInt(words[1]);
                                        break;
                                    }
                                }
                            }
                    }
                    String str;

                    br = new BufferedReader(new InputStreamReader(execIn));
                    SysUtils.Log("execIn... ");
                    StringBuilder answerBuilder = new StringBuilder();
                    while ((str = br.readLine()) != null) {
                        SysUtils.Log("execIn: " + str);
                        if (str.contains("monotonic clock"))
                            continue;

                        answerBuilder.append(str).append("\n");
                        Message message = new Message();
                        Bundle bdl = new Bundle(bundle);
                        bdl.putString(SysUtils.LOG, str);
                        message.setData(bdl);
                        messageHandler.sendMessage(message);
                    }
                    answer = answerBuilder.toString();

                    SysUtils.Log("waitFor...");
                    int code = exec.waitFor();
                    SysUtils.Log(taskType + " code = " + code);
                    bundle.putBoolean(SysUtils.SUCCESS, code == 0);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (execIn != null) execIn.close();
                        if (dos != null) dos.close();
                        if (br != null) br.close();
                    } catch (Exception ignored) {
                    }
                    if (exec != null) exec.destroy();
                }

                bundle.putLong(SysUtils.TIME, timeMillis);
                bundle.putString(SysUtils.OUTPUT, (answer.isEmpty()) ? "<empty>" : answer);
                bundle.putBoolean(SysUtils.FINISH, true);

                Message message = new Message();
                message.setData(bundle);
                messageHandler.sendMessage(message);
            }
        }).start();

        actionListener.onAction(SysUtils.FR_NAV_TASK, taskType, task.num, true);
    }

    public void openTask(int num) {
        SysUtils.Log("openTask() " + num);
        if (tasks[num] != null)
            tasks[num].progressDialog.show();
        else
            closeTask(num, false);
    }

    private void closeTask(int num, boolean stop) {
        tasks[num].closeTask(stop);
    }

    private void onTaskFinish(String taskType, final Bundle bundle) {
        int num = bundle.getInt(SysUtils.TASK_NUM);
        final String output = bundle.getString(SysUtils.OUTPUT, "<empty_output>");

        closeTask(num, false);
        if (sp.getBoolean(SysUtils.PREFS_VIBRATE, false))
            ((Vibrator) mainActivity.getSystemService(Service.VIBRATOR_SERVICE)).vibrate(new long[]{0, 200, 100, 200}, -1);

        long time = ((System.currentTimeMillis() - bundle.getLong(SysUtils.TIME, 0)) / 1000);
        String text = bundle.getString(SysUtils.FILENAME, "<unknown>") + "\n" +
                mainActivity.getString(R.string.cost_time) + " " + (time / 3600) + ":" + (time / 60) + ":" + (time % 60) + "\n" + output;
        //String title = text.endsWith("Success!\n") ? taskType : getString(R.string.error);
        String title = bundle.getBoolean(SysUtils.SUCCESS, false) ? taskType : getString(R.string.error);

        if (sp.getBoolean(SysUtils.PREFS_NOTIFY, false)) {
            PendingIntent intent = PendingIntent.getActivity(mainActivity, 0,
                    new Intent(mainActivity, ApkToolActivity.class).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
                    PendingIntent.FLAG_CANCEL_CURRENT);
            Notification.Builder builder = new Notification.Builder(mainActivity)
                    .setContentTitle(bundle.getString(SysUtils.FILENAME, "<unknown>") + ": " + title)
                    .setTicker(title)
                    .setWhen(System.currentTimeMillis())
                    .setContentIntent(intent)
                    .setAutoCancel(true)
                    .setSmallIcon(R.mipmap.ic_launcher);
            Notification notif;
            builder.setStyle(new Notification.BigTextStyle()
                    .setBigContentTitle(bundle.getString(SysUtils.FILENAME, "<unknown>") + ": " + title)
                    .bigText(output));
            notif = builder.build();
            ((NotificationManager) mainActivity.getSystemService(Context.NOTIFICATION_SERVICE)).notify(count++, notif);
        }
        new AlertDialog.Builder(mainActivity).setTitle(title).setMessage(text)
                .setPositiveButton(mainActivity.getString(R.string.ok), null)
                .setNeutralButton((mainActivity.getString(R.string.copy)),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                TextUtil.copyToClipboard(output);
                            }
                        }).create().show();

        if (!sp.getBoolean(SysUtils.PREFS_NOTIFY, false) && userFarAway)
            SysUtils.Toast(mainActivity, taskType);

        actionListener.onAction(SysUtils.FR_NAV_TASK, taskType, num, false);
    }

    private int getFreeTask() {
        for (int i = 0; i < tasks.length; i++)
            if (tasks[i] == null)
                return i;

        return -1;
    }

    public void setTaskListener(TaskListener listener) {
        taskListener = listener;
    }

    public interface TaskListener {
        void onTask(int taskNum, String taskName, boolean start);
    }

    private class MessageHandler extends Handler {

        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            boolean finish = bundle.getBoolean(SysUtils.FINISH, false);
            int num = bundle.getInt(SysUtils.TASK_NUM);
            SysUtils.Log("getInt " + num);
            SysUtils.Log("handleMessage getFreeTask " + getFreeTask());

            if (!forgotted.contains(bundle.getInt(SysUtils.PID, -2))) {
                if (!bundle.getBoolean(SysUtils.FINISH, false))
                    tasks[num].logView.append(bundle.getString(SysUtils.LOG, "<empty>") + "\n");
                else if (!(tasks[num].signable && sp.getBoolean(SysUtils.PREFS_AUTOSIGN, false)) || !bundle.getBoolean(SysUtils.SUCCESS))
                    tasks[num].die(bundle);
                else {
                    Task newTask = new Task(mainActivity, tasks[num].uri + ".apk", getString(R.string.signing),
                            getSignCommand(tasks[num].uri + ".apk"), R.string.sign_finish);
                    newTask.receiveInheritance(tasks[num], bundle);
                }
            } else if (finish)
                forgotted.remove((Integer) bundle.getInt(SysUtils.PID, -2));
        }
    }

    private class Task {
        int num = -1;
        int pid = -1;
        int cpid = -1;
        Task nextTask = null;
        String lastResult = "";
        long lastTime = 0;
        String binary = null;
        boolean signable = false;

        Context co;
        String uri;
        String taskType;
        String command;
        int action;

        AlertDialog progressDialog;
        TextView logView;

        Task(Context co, final String uri, final String taskType, final String command, final int action) {
            this.co = co;
            this.uri = uri;
            this.taskType = taskType;
            this.command = command;
            this.action = action;

            String[] args = command.split(" ");
            binary = args[0].endsWith(".sh") ? args[1] : SysUtils.getLastPart(args[0], '/');
        }

        void start() {
            num = getFreeTask();
            SysUtils.Log("getFreeTask " + num);
            if (num == -1) {
                tasks[num] = null;
                SysUtils.Toast(mainActivity, mainActivity.getString(R.string.no_free_task));
                return;
            }
            tasks[num] = this;
            taskListener.onTask(num, taskType, true);

            if (progressDialog == null) { // если это дочерний таск, то диалог достался от родительского
                final int taskNum = num;
                View view = View.inflate(co, R.layout.progress_info, null);
                ((TextView) view.findViewById(R.id.hint)).setText(taskType);
                progressDialog = new AlertDialog.Builder(co)
                        .setTitle(SysUtils.getFullFileName(uri))
                        .setView(view)
                        .setPositiveButton(mainActivity.getString(R.string.hide), null)
                        .setNeutralButton(mainActivity.getString(R.string.dostop),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        tasks[taskNum].closeTask(true);
                                        dialog.dismiss();
                                    }
                                })
                        .setNegativeButton(mainActivity.getString(R.string.forget),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        tasks[taskNum].closeTask(false);
                                        dialog.dismiss();
                                    }
                                })
                        .create();
                logView = (TextView) view.findViewById(R.id.info);
            }

            if (!sp.getBoolean(SysUtils.PREFS_AUTOMINIMIZE, false))
                progressDialog.show();
            else
                actionListener.onAction(SysUtils.FR_SNACK, null, R.string.hidden, false);

            threadWork(uri, taskType, command, action, this);
        }

        private void closeTask(boolean stop) {
            SysUtils.Log("closeTask() " + num);
            taskListener.onTask(num, "", false);
            progressDialog.dismiss();
            if (stop) {
                if (tasks[num].pid != -1)
                    SysUtils.killPid(sp.getBoolean(SysUtils.PREFS_USE_ROOT, false), tasks[num].pid);
                if (tasks[num].cpid != -1)
                    SysUtils.killPid(true, tasks[num].cpid);
            } else
                forgotted.add(tasks[num].pid);
            tasks[num] = null;
        }

        void receiveInheritance(Task parentTask, Bundle bundle) {
            lastResult = parentTask.lastResult + "\n" + bundle.getString(SysUtils.OUTPUT);
            lastTime += bundle.getLong(SysUtils.TIME);
            progressDialog = parentTask.progressDialog;
            logView = parentTask.logView;
            tasks[parentTask.num] = this;
            start();
        }

        void die(Bundle bundle) {
            bundle.putString(SysUtils.OUTPUT, lastResult + "\n" + bundle.getString(SysUtils.OUTPUT)); // надо бы упростить
            onTaskFinish(mainActivity.getString(bundle.getInt(SysUtils.ACTION)), bundle);
        }

    }

}
