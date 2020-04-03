package com.clash.component.state;

import com.clash.IComponent;

public interface IStateComponent extends IComponent {

    void change(int stateId);
    void changeToNext();
    StateInfo getInfo();
}
