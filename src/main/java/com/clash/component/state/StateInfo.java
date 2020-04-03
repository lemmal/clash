package com.clash.component.state;

public class StateInfo {
    private final int stateId;
    private final int leftSec;

    public StateInfo(int stateId, int leftSec) {
        this.stateId = stateId;
        this.leftSec = leftSec;
    }

    public int getStateId() {
        return stateId;
    }

    public int getLeftSec() {
        return leftSec;
    }
}
