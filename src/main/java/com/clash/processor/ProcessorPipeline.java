package com.clash.processor;

import com.clash.IResult;

import java.util.LinkedList;
import java.util.List;

public class ProcessorPipeline implements IProcessorPipeline {
    private List<IProcessor> processors = new LinkedList<>();

    @Override
    public ProcessorPipeline addLast(IProcessor processor) {
        if(null != processor) {
            processors.add(processor);
        }
        return this;
    }

    @Override
    public IResult process() {
        IResult result = null;
        for (IProcessor processor : processors) {
            result = processor.process();
            if (null == result || !result.isSuccess()) {
                break;
            }
        }
        return null != result ? result : () -> false;
    }


}
