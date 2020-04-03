package com.clash.component.state;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class StateRegister {
    private Collection<IState> states;
    private int firstStateId;

    private StateRegister(Collection<IState> states, int firstStateId) {
        this.states = states;
        this.firstStateId = firstStateId;
    }

    public static StateRegister createFromEnum(Class<? extends IState> clazz, IState firstState) {
        if(!clazz.isEnum()) {
            throw new IllegalArgumentException();
        }
        Set<IState> states = Arrays.stream(clazz.getEnumConstants()).collect(Collectors.toSet());
        return new StateRegister(states, firstState.getStateId());
    }

    public static StateRegister create(Collection<IState> states, int firstStateId) {
        return new StateRegister(states, firstStateId);
    }

    Collection<IState> getStates() {
        return states;
    }

    int getFirstStateId() {
        return firstStateId;
    }

}
