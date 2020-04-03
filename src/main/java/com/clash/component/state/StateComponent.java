package com.clash.component.state;

import com.clash.IContext;
import com.clash.bean.BeanAutowire;
import com.clash.bean.BeanConsumer;
import com.clash.logger.ClashLogger;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@BeanConsumer
public class StateComponent implements IStateComponent {
    @BeanAutowire
    private IContext context;
    @BeanAutowire
    private StateRegister register;
    private Map<Integer, IState> states;
    private int curStateId;
    private LocalDateTime nextStateTime;
    private Future<?> future;
    private boolean isDestroy;

    @Override
    public void init() {

    }

    @Override
    public void start() {
        change(register.getFirstStateId());
    }

    @Override
    public void change(int nextStateId) {
        IState newState = getState(nextStateId);
        if(null == newState) {
            throw new IllegalArgumentException("nextStateId not found : " + nextStateId);
        }
        _change(newState);
    }

    private void _change(IState newState) {
        context.getSynchronizer().submit(() -> {
            try {
                if(isDestroy) {
                    ClashLogger.error("already destroy.");
                    return;
                }
                IState curState = getState(curStateId);
                if(null != curState) {
                    curState.onStateOver();
                }
                curStateId = newState.getStateId();
                newState.onStateStart();
            } finally {
                changeNextStateLater(newState);
            }

        });
    }

    @Override
    public void changeToNext() {
        IState state = getState(curStateId);
        if(null == state) {
            throw new IllegalStateException("state not exist : " + curStateId);
        }
        if(!state.hasNextState()) {
            throw new IllegalStateException("next state not exist");
        }
        change(state.getNextStateId());
    }

    private void changeNextStateLater(IState state) {
        if(null != future) {
            future.cancel(false);
        }
        if(isDestroy) {
            return;
        }
        if(!state.hasNextState() || state.isForever()) {
            return;
        }
        long second = state.getStateSecond();
        future = context.getScheduler().schedule(() -> changeWithValidation(state.getNextStateId(), state.getStateId()), second, TimeUnit.SECONDS);
        nextStateTime = LocalDateTime.now().plusSeconds(second);
    }

    private void changeWithValidation(int nextStateId, int curStateId) {
        if(this.curStateId != curStateId) {
            return;
        }
        change(nextStateId);
    }

    @Override
    public StateInfo getInfo() {
        return context.getSynchronizer().submit(() -> new StateInfo(curStateId, getStateLeftSec()));
    }

    private int getStateLeftSec() {
        return null != nextStateTime ? (int) LocalDateTime.now().until(nextStateTime, ChronoUnit.SECONDS) : 0;
    }

    @Override
    public void destroy() {
        context.getSynchronizer().submit(() -> {
            if(isDestroy) {
                return;
            }
            removeFuture();
            isDestroy = true;
        });
    }

    private IState getState(int stateId) {
        if(null == states) {
            states = register.getStates().stream().collect(Collectors.toMap(IState::getStateId, s -> s));
        }
        return states.get(stateId);
    }

    private void removeFuture() {
        if(null != future) {
            future.cancel(false);
            future = null;
        }
    }

}