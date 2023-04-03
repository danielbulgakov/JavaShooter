package com.example.javashooter.myobjects;

public class MyPoint {
    private int x = 0, y = 0;
    private int r = 0;

    public MyPoint(int x, int y, int r) {
        this.x = x;
        this.y = y;
        this.r = r;
    }

    public MyPoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setNewPos(int x, int y){
        this.x = x;
        this.y = y;
    }
    public void setNewRadius(int r){
        this.r = r;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getR() {
        return r;
    }

    public void setR(int r) {
        this.r = r;
    }

    @Override
    public String toString() {
        return "MyPoint{" +
                "x=" + x +
                ", y=" + y +
                ", r=" + r +
                '}';
    }
}
