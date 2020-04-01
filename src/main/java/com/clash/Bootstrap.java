package com.clash;

import com.clash.bean.*;
import com.clash.processor.IProcessor;
import com.clash.processor.ProcessorPipeline;
import com.clash.synchronizer.ISynchronizer;
import com.clash.synchronizer.NativeSynchronizer;

import java.util.concurrent.ScheduledExecutorService;

/**
 * 测试引导
 */
public class Bootstrap {

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
            System.out.println("init");
        }

        @Override
        public void start() {
            System.out.println("start");
        }

        @Override
        public void destroy() {
            System.out.println("destroy");
        }

        @Override
        public IResult join() {
            context.getJoinPipeline().process();
            System.out.println("join");
            return null;
        }

        @Override
        public IResult leave() {
            context.getSynchronizer().submit(() -> context.getLeavePipeline().process());
            System.out.println("leave");
            return null;
        }

        @Override
        public IResult invoke() {
            context.getInvokePipeline().process();
            System.out.println("invoke");
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
            System.out.println("process");
            return () -> true;
        }
    }

    public static class BootstrapJoinProcessor implements IProcessor {

        @Override
        public IResult process() {
            System.out.println("process join");
            return () -> true;
        }
    }

    public static class BootstrapLeaveProcessor implements IProcessor {

        @Override
        public IResult process() {
            System.out.println("process leave");
            return () -> true;
        }
    }

    public static class BootstrapInvokeProcessor implements IProcessor {

        @Override
        public IResult process() {
            System.out.println("process invoke");
            return () -> true;
        }
    }

    public static class BootstrapSynchronizer implements IBeanProvider<ISynchronizer> {

        @Override
        public ISynchronizer provide() {
            return new NativeSynchronizer();
        }
    }
}
