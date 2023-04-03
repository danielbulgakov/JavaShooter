package com.example.javashooter.connection;

public class ClientDataManager {
    private String playerName;
    private int  arrowsShoot = 0;
    private int pointsEarned = 0;

    public ClientDataManager(String playerName) {
        this.playerName = playerName;
    }

    public void increaseArrowsShoot(int a) {
        this.arrowsShoot += a;
    }

    public void reset() {
        arrowsShoot = 0;
        pointsEarned = 0;
    }

    public void increasePointsEarned(int a) {
        this.pointsEarned += a;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getArrowsShoot() {
        return arrowsShoot;
    }

    public int getPointsEarned() {
        return pointsEarned;
    }

    public void setArrowsShoot(int arrowsShoot) {
        this.arrowsShoot = arrowsShoot;
    }

    public void setPointsEarned(int pointsEarned) {
        this.pointsEarned = pointsEarned;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
}
