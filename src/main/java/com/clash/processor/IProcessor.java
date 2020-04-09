package com.clash.processor;

import com.clash.param.IParam;
import com.clash.IResult;

public interface IProcessor {
    IResult process(IParam param);
}
