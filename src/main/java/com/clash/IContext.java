package com.clash;

import com.clash.component.ComponentContainer;
import com.clash.processor.ProcessorPipeline;
import com.clash.synchronizer.ISynchronizer;

import java.util.concurrent.ScheduledExecutorService;

public interface IContext extends ILifeCycle{

    ScheduledExecutorService getScheduler();

    ISynchronizer getSynchronizer();

    IPlayerContainer<?> getPlayerContainer();

    ComponentContainer getComponentContainer();

    ProcessorPipeline getJoinPipeline();

    ProcessorPipeline getLeavePipeline();

    ProcessorPipeline getInvokePipeline();

    default void init() {
        getPlayerContainer().init();
        getComponentContainer().getComponents().forEach(ILifeCycle::init);
    }

    default void start() {
        getPlayerContainer().start();
        getComponentContainer().getComponents().forEach(ILifeCycle::start);
    }

    default void destroy() {
        getPlayerContainer().destroy();
        getComponentContainer().getComponents().forEach(ILifeCycle::destroy);
    }
}
