package com.clash.synchronizer;

public class NativeSynchronizer implements ISynchronizer {

    @Override
    public synchronized void submit(ISynchronizerRunnable task) {
        task.run();
    }

    @Override
    public synchronized <T> T submit(ISynchronizerCallable<T> task) {
        return task.call();
    }
}
