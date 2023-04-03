package com.example.javashooter;

import com.example.javashooter.connection.ClientDataManager;
import com.example.javashooter.connection.Model;
import com.example.javashooter.connection.ModelBuilder;
import com.example.javashooter.connection.responses.ClientActions;
import com.example.javashooter.connection.responses.ClientReq;
import com.example.javashooter.connection.IObserver;
import com.example.javashooter.myobjects.Arrow;
import com.example.javashooter.myobjects.MyPoint;
import com.example.javashooter.myobjects.PlayerInfo;
import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientFrame implements IObserver {
    @FXML
    private VBox infoBox;
    @FXML
    private Pane gamePane;
    @FXML
    private VBox playersBox;
    ArrayList<Button> players = new ArrayList<>();
    ArrayList<VBox> playersInfo = new ArrayList<>();
    ArrayList<Arrow> arrows = new ArrayList<>();
    ArrayList<Circle> targets = new ArrayList<>();

    private String playerName;
    private Gson gson = new Gson();
    private Socket socket;
    private DataOutputStream dos;
    private OutputStream os;

    private Model m = ModelBuilder.build();

    public void initialize() {
        m.addObserver(this);
    }

    private void sendRequest(ClientReq msg)
    {
        try {
            String s_msg = gson.toJson(msg);
            dos.writeUTF(s_msg);
        } catch (IOException ignored) { }
    }
    public void dataInit(Socket socket, String playersName) {
        this.socket = socket;
        this.playerName = playersName;
        try {
            os = socket.getOutputStream();
            dos = new DataOutputStream(os);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void onReady(MouseEvent mouseEvent) {
        sendRequest(new ClientReq(ClientActions.READY));
    }

    public void onPause(MouseEvent mouseEvent) {
        sendRequest(new ClientReq(ClientActions.STOP));
    }

    public void onShoot(MouseEvent mouseEvent) {
        sendRequest(new ClientReq(ClientActions.SHOOT));
    }

    @Override
    public void update() {
        updateCircles(m.getTargetArrayList());
        updatePlayersInfo(m.getClientArrayList());
        updatePlayers(m.getClientArrayList());
        updateArrows(m.getArrowsArrayList());
    }



    private void updateCircles(ArrayList<MyPoint> a) {
        if (a == null || a.size() == 0) return;
        Platform.runLater(new Runnable(){
            @Override
            public void run() {
                for (int i = 0; i < a.size(); i++) {
                    if (i >= targets.size()) {
                        Circle c = new Circle(a.get(i).getX(), a.get(i).getY(), a.get(i).getR());
                        c.getStyleClass().add("targets");
                        targets.add(c);
                        gamePane.getChildren().add(c);
                    } else if (a.size() > targets.size()){
                        for (int j = 0; j < a.size() - targets.size(); j++) {
                            targets.remove(targets.size() - 1);
                            gamePane.getChildren().remove(
                                    gamePane.getChildren().size() - 1
                            );
                        }
                    }
                    else {
                        targets.get(i).setRadius(a.get(i).getR());
                        targets.get(i).setCenterX(a.get(i).getX());
                        targets.get(i).setCenterY(a.get(i).getY());
                    }
                }
            }
        });
    }
    private void updateArrows(ArrayList<MyPoint> a) {
        System.out.println("{");
        a.forEach(System.out::println);
        System.out.println("}");
        if (a == null || a.size() == 0) return;
        Platform.runLater(new Runnable(){
            @Override
            public void run() {
                arrows.forEach(arrow -> gamePane.getChildren().remove(arrow));
                for (int i = 0; i < a.size(); i++) {

                    Arrow arr = new Arrow(a.get(i).getX(),a.get(i).getY(), a.get(i).getR());
                    arrows.add(arr);
                    gamePane.getChildren().add(arr);

                }
            }
        });

    }

    private void updatePlayersInfo(ArrayList<ClientDataManager> a) {
        if (a == null || a.size() == 0) return;
        Platform.runLater(new Runnable(){
            @Override
            public void run() {
                for (int i = 0; i < a.size(); i++) {
                    if (i >= players.size()) {
                        VBox vb = PlayerInfo.createVbox(a.get(i));
                        playersInfo.add(vb);
                        infoBox.getChildren().add(vb);
                    } else {
                        PlayerInfo.setPlayerName(playersInfo.get(i), a.get(i).getPlayerName());
                        PlayerInfo.setPlayerShots(playersInfo.get(i), a.get(i).getArrowsShoot());
                        PlayerInfo.setPlayerPoints(playersInfo.get(i), a.get(i).getPointsEarned());
                    }
                }
            }
        });

    }

    private void updatePlayers(ArrayList<ClientDataManager> a) {
        if (a == null || a.size() == 0 || players.size() == a.size()) return;
        Platform.runLater(new Runnable(){
            @Override
            public void run() {
                for (int i = 0; i < a.size(); i++) {
                    if (i >= players.size()) {
                        Button b = new Button();
                        b.setPrefHeight(140);
                        b.setPrefWidth(140);

                        if (a.get(i).getPlayerName().equals(playerName)){
                            b.getStyleClass().add("player-client");
                            b.setText("Вы");
                        } else {
                            b.getStyleClass().add("player-connect");
                        }

                        players.add(b);
                        playersBox.getChildren().add(b);
                    }
                }
            }
        });


    }
}
