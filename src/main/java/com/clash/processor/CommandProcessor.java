package com.clash.processor;

import com.clash.IResult;
import com.clash.param.ICommandParam;
import com.clash.param.IParam;

public abstract class CommandProcessor implements IProcessor {

    @Override
    public IResult process(IParam param) {
        return _process((ICommandParam) param);
    }

    protected abstract IResult _process(ICommandParam param);
}
