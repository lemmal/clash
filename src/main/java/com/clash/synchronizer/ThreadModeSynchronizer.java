package com.clash.synchronizer;

import com.clash.IContext;
import com.clash.bean.*;
import com.clash.logger.ClashLogger;

import java.util.concurrent.ExecutionException;

@BeanConsumer
public class ThreadModeSynchronizer implements ISynchronizer {
    @BeanAutowire
    private IContext context;

    @Override
    public void submit(ISynchronizerRunnable task) {
        context.getScheduler().submit(() -> run(task));
    }

    @Override
    public <T> T submit(ISynchronizerCallable<T> task) {
        try {
            return context.getScheduler().submit(() -> call(task)).get();
        } catch (InterruptedException | ExecutionException e) {
            ClashLogger.error("call failed.", e);
            return null;
        }
    }

    private void run(ISynchronizerRunnable task) {
        try {
            task.run();
        } catch (Throwable t) {
            ClashLogger.error("run failed.", t);
        }
    }

    private <T> T call(ISynchronizerCallable<T> task) {
        try {
            return task.call();
        } catch (Throwable t) {
            ClashLogger.error("call failed.", t);
            return null;
        }
    }

}
