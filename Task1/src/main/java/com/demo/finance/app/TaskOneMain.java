package com.demo.finance.app;

import com.demo.finance.in.cli.CliHandler;

public class TaskOneMain {
    public static void main(String[] args) {
        ApplicationConfig config = new ApplicationConfig();
        CliHandler cliHandler = config.getCliHandler();
        cliHandler.start();
    }
}