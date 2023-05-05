package com.example.javashooter.connection;

public class ClientInfo {
    private String playerName;
    private int  arrowsShoot = 0;
    private int pointsEarned = 0;
    private int wins = 0;

    public ClientInfo(String playerName) {
        this.playerName = playerName;
    }

    public void increaseArrowsShoot(int a) {
        this.arrowsShoot += a;
    }

    public void reset() {
        arrowsShoot = 0;
        pointsEarned = 0;
    }

    public void resetInfo() {
        this.arrowsShoot = 0;
        this.pointsEarned = 0;
    }

    public void increasePointsEarned(int a) {
        this.pointsEarned += a;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getPlayerMarkedName() {
        return playerName + " (Вы)";
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

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    @Override
    public String toString() {
        return "ClientInfo{" +
                "playerName='" + playerName + '\'' +
                ", wins=" + wins +
                '}';
    }
}
