package com.demo.finance.in.cli;

/**
 * Represents a command in the Command Line Interface (CLI).
 * Each command will implement this interface and provide an implementation
 * for the {@link #execute()} method to perform a specific action when triggered.
 */
public interface Command {

    /**
     * Executes the command's associated action.
     * Each implementing class will define the specific action that happens when this method is called.
     */
    void execute();
}