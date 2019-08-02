package com.gmail.heagoo.common;

public interface CommandInterface {

    boolean runCommand(String command, String[] env, Integer timeout);

    boolean runCommand(String command, String[] env, Integer timeout, boolean readWhileExec);

    String getStdOut();

    String getStdError();
}
