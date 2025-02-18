package com.darewro.rider.view.customViews;

import java.util.TimerTask;

public abstract class NonInterruptableTask extends TimerTask {

    protected boolean isDone = false;

    public boolean isDone() {return isDone;}

    protected abstract void doTaskWork();

    @Override
    public void run() {
        isDone = false;
        doTaskWork();
        isDone = true;
    }

}