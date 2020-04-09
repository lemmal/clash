package com.clash.processor;

import com.clash.param.IParam;
import com.clash.IResult;

public interface IProcessorPipeline {

    IProcessorPipeline addLast(IProcessor processor);

    IResult process(IParam param);
}
