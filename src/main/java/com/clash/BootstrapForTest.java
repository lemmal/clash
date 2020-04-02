package com.clash;

import com.clash.bean.*;
import com.clash.logger.ClashLogger;
import com.clash.processor.IProcessor;
import com.clash.processor.ProcessorPipeline;
import com.clash.synchronizer.CASSynchronizer;
import com.clash.synchronizer.ISynchronizer;

import java.util.concurrent.ScheduledExecutorService;

/**
 * 测试引导
 */
public class BootstrapForTest {

    public static void main(String[] args) throws BeanParseException, BeanConstructException {
        IManager manager = BeanFactory.buildManager(BootstrapManager.class);
        manager.init();
        manager.start();
        manager.join();
        manager.leave();
        manager.destroy();
    }

    @BeanConsumer
    @BeanConstruct(IManager.class)
    public static class BootstrapManager implements IManager {
        @BeanAutowire
        IContext context;

        @Override
        public void init() {
            ClashLogger.info("init");
        }

        @Override
        public void start() {
            ClashLogger.info("start");
        }

        @Override
        public void destroy() {
            ClashLogger.info("destroy");
        }

        @Override
        public IResult join() {
            context.getJoinPipeline().process();
            ClashLogger.info("join");
            return null;
        }

        @Override
        public IResult leave() {
            context.getSynchronizer().submit(() -> context.getLeavePipeline().process());
            ClashLogger.info("leave");
            return null;
        }

        @Override
        public IResult invoke() {
            context.getInvokePipeline().process();
            ClashLogger.info("invoke");
            return null;
        }
    }

    @BeanConsumer
    @BeanConstruct(IContext.class)
    public static class BootstrapContext implements IContext {
        @BeanAutowire
        ISynchronizer synchronizer;
        ProcessorPipeline joinPipeline = new ProcessorPipeline().addLast(new BootstrapProcessor()).addLast(new BootstrapJoinProcessor());
        ProcessorPipeline leavePipeline = new ProcessorPipeline().addLast(new BootstrapProcessor()).addLast(new BootstrapLeaveProcessor());
        ProcessorPipeline invokePipeline = new ProcessorPipeline().addLast(new BootstrapProcessor()).addLast(new BootstrapInvokeProcessor());


        @Override
        public ScheduledExecutorService getScheduler() {
            return null;
        }

        @Override
        public ISynchronizer getSynchronizer() {
            return synchronizer;
        }

        @Override
        public ProcessorPipeline getJoinPipeline() {
            return joinPipeline;
        }

        @Override
        public ProcessorPipeline getLeavePipeline() {
            return leavePipeline;
        }

        @Override
        public ProcessorPipeline getInvokePipeline() {
            return invokePipeline;
        }
    }

    public static class BootstrapProcessor implements IProcessor {

        @Override
        public IResult process() {
            ClashLogger.info("process");
            return () -> true;
        }
    }

    public static class BootstrapJoinProcessor implements IProcessor {

        @Override
        public IResult process() {
            ClashLogger.info("process join");
            return () -> true;
        }
    }

    public static class BootstrapLeaveProcessor implements IProcessor {

        @Override
        public IResult process() {
            ClashLogger.info("process leave");
            return () -> true;
        }
    }

    public static class BootstrapInvokeProcessor implements IProcessor {

        @Override
        public IResult process() {
            ClashLogger.info("process invoke");
            return () -> true;
        }
    }

    public static class BootstrapSynchronizer implements IBeanProvider<ISynchronizer> {

        @Override
        public ISynchronizer provide() {
            return new CASSynchronizer();
        }
    }
}
