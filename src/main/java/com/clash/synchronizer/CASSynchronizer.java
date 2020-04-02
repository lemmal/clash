package com.clash.synchronizer;

import com.clash.logger.ClashLogger;

import java.util.concurrent.atomic.AtomicLong;

public class CASSynchronizer implements ISynchronizer {
    private AtomicLong mutex = new AtomicLong(EMPTY_THREAD);

    private static final long EMPTY_THREAD = -1;

    @Override
    public void submit(ISynchronizerRunnable task) {
        acquire();
        try {
            task.run();
        } finally {
            release();
        }
    }

    @Override
    public <T> T submit(ISynchronizerCallable<T> task) {
        acquire();
        try {
            return task.call();
        } finally {
            release();
        }
    }

    private void acquire() {
        long begin = System.currentTimeMillis();
        while(!mutex.compareAndSet(EMPTY_THREAD, Thread.currentThread().getId())) {

        }
        long duration = System.currentTimeMillis() - begin;
        if(duration > 500) {
            ClashLogger.info("acquire wait too long : {}", duration);
        }
    }

    private void release() {
        mutex.compareAndSet(Thread.currentThread().getId(), EMPTY_THREAD);
    }

}
