package com.example.javashooter.connection;

import com.example.javashooter.myobjects.MyPoint;

import java.util.ArrayList;

public class Model {

    private final ArrayList<IObserver> observerArrayList = new ArrayList<>();
    private ArrayList<ClientDataManager> clientArrayList = new ArrayList<>();
    private ArrayList<MyPoint> targetArrayList = new ArrayList<>();
    private ArrayList<MyPoint> arrowArrayList = new ArrayList<>();

    // Game Modeling Constants
    private int ready = 0;
    private static final int Y_BOUND = 560;
    private final ArrayList<String> waitingList = new ArrayList<>();
    private final ArrayList<String> shootingList = new ArrayList<>();

    public void update()
    {
        for (IObserver o : observerArrayList) {
            o.update();
        }
    }

    // Usual model data
    public void init() {
        targetArrayList.add(new MyPoint(500,280, 60));
        targetArrayList.add(new MyPoint(650,280, 30));
        arrowsCountUpdate();
    }

    // Add arrows for each player
    private synchronized void arrowsCountUpdate() {
        arrowArrayList.clear();
        int clientsCount = clientArrayList.size();
        for (int i = 1; i <= clientsCount; i++) {
            int step = Y_BOUND / (clientsCount + 1);
            arrowArrayList.add(new MyPoint(50, step * i, 100));
        }

    }

    // Ready state handle
    public void ready(MainServer mc) {
        ready++;
        if (ready == clientArrayList.size())
            gameStart(mc);
    }
    public void notReady() {
        ready--;
    }

    // Pause state handle
    public void requestPause(String name) {
        if (waitingList.contains(name)) {
            waitingList.remove(name);
            if (waitingList.size() == 0){
                int a = 0;
                synchronized(this) {
                    notifyAll();
                }
            }
        } else {
            waitingList.add(name);
        }
    }

    // Shoot state handle
    public void requestShoot(String playerName) {
        var player = clientArrayList.stream()
                .filter(clientData -> clientData.getPlayerName().equals(playerName))
                .findFirst()
                .orElse(null);
        assert player != null;
        if (! shootingList.contains(player.getPlayerName())){
            shootingList.add(player.getPlayerName());
            player.increaseArrowsShoot(1);
        }
    }

    public void gameStart(MainServer mc) {
        Thread thread = new Thread(
                ()->
                {
                    int big_move = 5;
                    int sml_move = 10;
                    int arr_move = 5;
                    while (true) {
                        if (waitingList.size() != 0) {
                            synchronized(this) {
                                try {
                                    wait();
                                } catch(InterruptedException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                        if (shootingList.size() != 0) {

                                for (int i = 0; i < shootingList.size(); i++) {
                                    int finalI = i;
                                    if (shootingList.get(finalI) == null) break;
                                    ClientDataManager client = clientArrayList.stream()
                                            .filter(clientData -> clientData.getPlayerName().equals(shootingList.get(finalI)))
                                            .findFirst()
                                            .orElse(null);
                                    int index = clientArrayList.indexOf(client);
                                    MyPoint p = arrowArrayList.get(index);
                                    p.setX(p.getX() + arr_move);
                                    shootManager(p, client);
                                };

                        }
                        MyPoint big = targetArrayList.get(0);
                        MyPoint small = targetArrayList.get(1);

                        if (small.getY() <= small.getR() || Y_BOUND - small.getY()  <= small.getR()) {
                            sml_move = -1 * sml_move;
                        }
                        small.setY(small.getY() + sml_move);
                        if (big.getY() <= big.getR() || Y_BOUND - big.getY()  <= big.getR()) {
                            big_move = -1 * big_move;
                        }
                        big.setY(big.getY() + big_move);

                        mc.bcast();

                        try {
                            Thread.sleep(20);
                        } catch (InterruptedException ignored) {
                        }
                    }
                }
        );
        thread.start();

    }

    private synchronized void shootManager(MyPoint p, ClientDataManager player) {
        ShootState shootState = targetHitCheck(p);
        System.out.println(shootState);
        if (shootState.equals(ShootState.FLYING)) return;
        if (shootState.equals(ShootState.BIG_SHOT)) player.increasePointsEarned(1);
        if (shootState.equals(ShootState.SMALL_SHOT)) player.increasePointsEarned(2);
        p.setX(50);
        if (shootingList.size() == 1) shootingList.clear();
        else {
            shootingList.remove(player.getPlayerName());
        }


    }

    private synchronized ShootState targetHitCheck(MyPoint p) {

        if (contains(targetArrayList.get(1), p.getX() + p.getR(), p.getY())) {
            return ShootState.SMALL_SHOT;
        }
        if (contains(targetArrayList.get(0), p.getX() + p.getR(), p.getY())) {
            return ShootState.BIG_SHOT;
        }
        if (p.getX() > 700) {
            return ShootState.MISSED;
        }
        return ShootState.FLYING;
    }

    private boolean contains(MyPoint c, double x, double y) {
        return (Math.sqrt(Math.pow((x -c.getX()), 2) + Math.pow((y -c.getY()), 2)) < c.getR()) ;
    }














    public void addClient(ClientDataManager clientData) {
        clientArrayList.add(clientData);
        this.arrowsCountUpdate();
    }
    public  void addObserver(IObserver o)
    {
        observerArrayList.add(o);
    }

    public ArrayList<ClientDataManager> getClientArrayList() {
        return clientArrayList;
    }

    public void setClientArrayList(ArrayList<ClientDataManager> clientArrayList) {
        this.clientArrayList = clientArrayList;
    }

    public ArrayList<MyPoint> getTargetArrayList() {
        return targetArrayList;
    }

    public void setTargetArrayList(ArrayList<MyPoint> targetArrayList) {
        this.targetArrayList = targetArrayList;
    }

    public ArrayList<MyPoint> getArrowsArrayList() {
        return arrowArrayList;
    }

    public void setArrowArrayList(ArrayList<MyPoint> arrowArrayList) {
        this.arrowArrayList = arrowArrayList;
    }


}
