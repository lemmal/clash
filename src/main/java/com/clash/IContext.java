package com.clash;

import java.util.concurrent.ScheduledExecutorService;

public interface IContext {

    ScheduledExecutorService getScheduler();

}
