package com.clash.synchronizer;

import java.util.concurrent.ExecutorService;

public class ThreadModeSynchronizer implements ISynchronizer{
    private ExecutorService scheduler;

    @Override
    public void submit(ISynchronizerRunnable task) {

    }

    @Override
    public <T> T submit(ISynchronizerCallable<T> task) {
        return null;
    }
}
