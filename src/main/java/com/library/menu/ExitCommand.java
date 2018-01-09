package com.library.menu;

public class ExitCommand implements Command {

    @Override
    public boolean isExit() {
        return true;
    }

    @Override
    public String getName() {
        return "Exit";
    }

    @Override
    public void execute() {
    }
}
