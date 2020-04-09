package com.clash;

import com.clash.param.IParam;

public interface IManager extends ILifeCycle{
    /**
     * 加入游戏
     */
    IResult join(IParam param);

    /**
     * 离开游戏
     */
    IResult leave(IParam param);

    /**
     * 自定义游戏调用
     */
    IResult invoke(IParam param);


}
