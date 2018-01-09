package com.library.menu;

public interface Command {
    String getName();

    void execute();

    default boolean isExit() {
        return false;
    }
}
