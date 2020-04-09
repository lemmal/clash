package com.clash.processor;

import com.clash.param.IParam;
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
    public IResult process(IParam param) {
        IResult result = null;
        for (IProcessor processor : processors) {
            result = processor.process(param);
            if (null == result || !result.isSuccess()) {
                break;
            }
        }
        return null != result ? result : () -> false;
    }


}
