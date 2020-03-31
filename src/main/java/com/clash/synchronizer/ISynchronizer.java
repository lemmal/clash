package com.clash.synchronizer;

public interface ISynchronizer {
    void submit(ISynchronizerRunnable task);

    <T> T submit(ISynchronizerCallable<T> task);
}
