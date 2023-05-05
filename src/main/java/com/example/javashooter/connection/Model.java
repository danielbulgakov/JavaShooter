package com.example.javashooter.connection;

import com.example.javashooter.connection.database_hibernate.DataBaseHibernate;
import com.example.javashooter.connection.database_hibernate.PlayerEntity;
import com.example.javashooter.connection.database_jdbc.DataBase;
import com.example.javashooter.connection.responses.ShootState;
import com.example.javashooter.myobjects.MyPoint;


import java.util.ArrayList;

public class Model {
    private final ArrayList<IObserver> observerArrayList = new ArrayList<>();
    private ArrayList<ClientInfo> clientArrayList = new ArrayList<>();
    private ArrayList<MyPoint> targetArrayList = new ArrayList<>();
    private ArrayList<MyPoint> arrowArrayList = new ArrayList<>();
    private final ArrayList<String> readyList = new ArrayList<>();
    private static final int Y_BOUND = 560;
    private final ArrayList<String> waitingList = new ArrayList<>();
    private final ArrayList<String> shootingList = new ArrayList<>();
    private ArrayList<ClientInfo> entitiesList = new ArrayList<>();
    private String winner = null;
    private static final int WINNER_POINTS = 2;
    private volatile boolean isGameReset = true;
    private DataBase dataBase;
    private MainServer mc;
    public void update()
    {
        for (IObserver o : observerArrayList) {
            o.update();
        }
    }

    public void setMc(MainServer mc) {
        this.mc = mc;
    }

    public void updateScoreTable() {
        entitiesList = dataBase.getAllPlayers();
        mc.bcast();
    }

    // Usual model data
    public void init(DataBase dataBase) {
        this.dataBase = dataBase;
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
    public void ready(MainServer mc, String name) {
        if (readyList.isEmpty()) { readyList.add(name); return;}

        if (readyList.contains(name)) {readyList.remove(name); }
        else {readyList.add(name);}

        if (clientArrayList.size() > 1 && readyList.size() == clientArrayList.size()) {
            isGameReset = false;
            gameStart(mc);
        }
    }

    // Pause state handle
    public void requestPause(String name) {
        if (isGameReset) return;
        if (waitingList.contains(name)) {
            waitingList.remove(name);
            if (waitingList.size() == 0){
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
        if (isGameReset || waitingList.size() != 0) return;
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
                    int arr_move = 15;
                    while (true) {
                        if (isGameReset) {
                            winner = null;
                            mc.bcast();
                            break;
                        }
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
                                    ClientInfo client = clientArrayList.stream()
                                            .filter(clientData -> clientData.getPlayerName().equals(shootingList.get(finalI)))
                                            .findFirst()
                                            .orElse(null);
                                    int index = clientArrayList.indexOf(client);
                                    MyPoint p = arrowArrayList.get(index);
                                    p.setX(p.getX() + arr_move);
                                    shootManager(p, client);
                                }

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

    private void gameReset() {
        isGameReset = true;
        readyList.clear();
        targetArrayList.clear();
        arrowArrayList.clear();
        waitingList.clear();
        shootingList.clear();
        clientArrayList.forEach(ClientInfo::reset);
        this.init(dataBase);
    }


    private synchronized void shootManager(MyPoint p, ClientInfo player) {
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
        checkWinner();


    }

    private synchronized void checkWinner() {
        clientArrayList.forEach(clientDataManager -> {
            if (clientDataManager.getPointsEarned() >= WINNER_POINTS) {
                this.winner = clientDataManager.getPlayerName();
                var name = this.winner;
                gameReset();
                ClientInfo p = entitiesList.stream()
                        .filter(entity -> entity.getPlayerName().equals(name))
                        .findFirst()
                        .orElse(null);
                assert p != null;
                p.setWins(p.getWins() + 1);
                dataBase.setPlayerWins(p);

                return;
            }
        });

    }

    private synchronized ShootState targetHitCheck(MyPoint p) {

        if (contains(targetArrayList.get(1), p.getX() + p.getR(), p.getY())) {
            return ShootState.SMALL_SHOT;
        }
        if (contains(targetArrayList.get(0), p.getX() + p.getR(), p.getY())) {
            return ShootState.BIG_SHOT;
        }
        if (p.getX() > 650) {
            return ShootState.MISSED;
        }
        return ShootState.FLYING;
    }

    private boolean contains(MyPoint c, double x, double y) {
        return (Math.sqrt(Math.pow((x -c.getX()), 2) + Math.pow((y -c.getY()), 2)) < c.getR()) ;
    }














    public void addClient(ClientInfo clientData) {
        clientArrayList.add(clientData);
        clientData.setWins(0);
        entitiesList.add(clientData);
        dataBase.addPlayer(clientData);
        this.arrowsCountUpdate();
    }
    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public  void addObserver(IObserver o)
    {
        observerArrayList.add(o);
    }

    public ArrayList<ClientInfo> getClientArrayList() {
        return clientArrayList;
    }

    public void setClientArrayList(ArrayList<ClientInfo> clientArrayList) {
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

    public ArrayList<ClientInfo> getEntitiesList() {
        return entitiesList;
    }

    public void setEntitiesList(ArrayList<ClientInfo> entitiesList) {
        this.entitiesList = entitiesList;
    }
}
