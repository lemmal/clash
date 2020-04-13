package com.clash.synchronizer;

import com.clash.ClashProperties;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class CASSynchronizer implements ISynchronizer {
    private AtomicLong mutex = new AtomicLong(EMPTY_THREAD);
    private AtomicInteger count = new AtomicInteger(0);

    private static final long EMPTY_THREAD = -1;
    private static final String WAIT_EACH = "clash.cas_synchronizer.wait_each";
    private static final String WAIT_TOTAL = "clash.cas_synchronizer.wait_total";

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
        long currentId = Thread.currentThread().getId();
        if(currentId != mutex.get()) {
            while(!mutex.compareAndSet(EMPTY_THREAD, currentId)) {
                long duration = System.currentTimeMillis() - begin;
                if(duration >= ClashProperties.INSTANCE.getIntVal(WAIT_TOTAL)) {
                    throw new RuntimeException("acquire wait too long : " + duration);
                }
                await();
            }
        }
        count.incrementAndGet();
    }

    private void await() {
        try {
            int milliSeconds = ClashProperties.INSTANCE.getIntVal(WAIT_EACH);
            Thread.sleep(milliSeconds);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void release() {
        long currentId = Thread.currentThread().getId();
        if(currentId == mutex.get()) {
            int count = this.count.decrementAndGet();
            if(count <= 0) {
                mutex.compareAndSet(currentId, EMPTY_THREAD);
            }
        }
    }

}
