package com.clash.synchronizer;

import java.util.concurrent.ExecutorService;

public class ThreadModeSynchronizer implements ISynchronizer{
    private ExecutorService scheduler;

    @Override
    public void submit(ISynchronizerRunnable task) {
        //TODO
    }

    @Override
    public <T> T submit(ISynchronizerCallable<T> task) {
        //TODO
        return null;
    }
}
