package com.example.javashooter.connection.database;

import java.util.ArrayList;

public interface IDataBase {
    void addPlayer(PlayersEntity entity);
    PlayersEntity getPlayerWins(String name);
    void setPlayerWins(PlayersEntity entity);
    ArrayList<PlayersEntity> getAllPlayers();
}
