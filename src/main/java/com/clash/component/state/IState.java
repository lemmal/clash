package com.clash.component.state;

public interface IState {
    /** 状态没有时间限制*/
    long LAST_FOREVER = -1;
    /** 状态瞬间切换，没有持续时间*/
    long INSTANT = 0;

    int NULL_STATE = -1;

    int getStateId();

    int getNextStateId();

    long getStateSecond();

    default void onStateStart() {

    }

    default void onStateOver() {

    }

    default boolean hasNextState() {
        return getNextStateId() != NULL_STATE;
    }

    default boolean isForever() {
        return getStateSecond() == LAST_FOREVER;
    }

}
