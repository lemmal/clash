package com.clash;

import java.util.Collection;

public interface IPlayerContainer<Identity> extends ILifeCycle{

    int getPlayerNumber();

    Collection<? extends IPlayer<Identity>> getPlayers();

    IPlayer<Identity> getPlayer(Identity id);

    IResult join(Identity id);

    IResult leave(Identity id);

}
