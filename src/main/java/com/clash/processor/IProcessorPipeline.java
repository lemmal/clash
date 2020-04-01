package com.clash.processor;

import com.clash.IResult;

public interface IProcessorPipeline {

    IProcessorPipeline addLast(IProcessor processor);

    IResult process();
}
