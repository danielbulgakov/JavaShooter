package com.example.javashooter.connection.database_hibernate;

import javax.persistence.*;

@Entity
@Table(name = "players", schema = "main", catalog = "")
public class PlayerEntity {
    @GeneratedValue(strategy = GenerationType.AUTO)
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
