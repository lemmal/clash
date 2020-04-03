package com.clash;

public interface IManager extends ILifeCycle{
    /**
     * 加入游戏
     */
    IResult join();

    /**
     * 离开游戏
     */
    IResult leave();

    /**
     * 自定义游戏调用
     */
    IResult invoke();


}
