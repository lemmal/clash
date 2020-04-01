package com.clash;

public interface IManager {
    /**
     * 初始化工作
     */
    void init();

    /**
     * 回合流转开始
     */
    void start();

    /**
     * 游戏结束销毁内容
     */
    void destroy();

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
