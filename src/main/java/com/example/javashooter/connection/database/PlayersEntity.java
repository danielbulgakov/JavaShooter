package com.example.javashooter.connection.database;

import javax.persistence.*;

@Entity
@Table(name = "players", schema = "main")
public class PlayersEntity {

    @Id
    @Column(name = "name", nullable = true, length = 255)
    private String name;
    @Basic
    @Column(name = "wins", nullable = false)
    private int wins;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }
}
