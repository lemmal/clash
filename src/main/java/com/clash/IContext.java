package com.clash;

import com.clash.processor.ProcessorPipeline;
import com.clash.synchronizer.ISynchronizer;

import java.util.concurrent.ScheduledExecutorService;

public interface IContext {

    ScheduledExecutorService getScheduler();

    ISynchronizer getSynchronizer();

    ProcessorPipeline getJoinPipeline();

    ProcessorPipeline getLeavePipeline();

    ProcessorPipeline getInvokePipeline();
}
