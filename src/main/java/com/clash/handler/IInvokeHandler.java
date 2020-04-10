package com.clash.handler;

import com.clash.IResult;
import com.clash.param.ICommandParam;

public interface IInvokeHandler {

    IResult invoke(ICommandParam param);
}
