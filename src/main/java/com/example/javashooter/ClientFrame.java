package com.example.javashooter;

import com.example.javashooter.connection.ClientInfo;
import com.example.javashooter.connection.Model;
import com.example.javashooter.connection.ModelBuilder;
import com.example.javashooter.connection.database_hibernate.PlayerEntity;
import com.example.javashooter.connection.responses.ClientActions;
import com.example.javashooter.connection.responses.ClientReq;
import com.example.javashooter.connection.IObserver;
import com.example.javashooter.connection.responses.SocketMesWrapper;
import com.example.javashooter.myobjects.Arrow;
import com.example.javashooter.myobjects.MyPoint;
import com.example.javashooter.myobjects.PlayerInfoBox;
import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;

public class ClientFrame implements IObserver {
    @FXML
    private VBox infoBox;
    @FXML
    private Pane gamePane;
    @FXML
    private VBox playersBox;
    final ArrayList<Button> players = new ArrayList<>();
    final ArrayList<VBox> playersInfo = new ArrayList<>();
    final ArrayList<Arrow> arrows = new ArrayList<>();
    final ArrayList<Circle> targets = new ArrayList<>();

    private String playerName;
    private final Gson gson = new Gson();
    private SocketMesWrapper socketMesWrapper;

    private final Model m = ModelBuilder.build();

    public void initialize() {
        m.addObserver(this);
    }

    private boolean isShowTable = false;

    private void sendRequest(ClientReq msg)
    {
        try {
            socketMesWrapper.writeData(gson.toJson(msg));
        } catch (IOException ignored) { }
    }
    public void dataInit(SocketMesWrapper socketMesWrapper, String playersName) {
        this.socketMesWrapper = socketMesWrapper;
        this.playerName = playersName;
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
    public void onScoreTable(MouseEvent mouseEvent) {
        sendRequest(new ClientReq(ClientActions.SCORE_TABLE));
        isShowTable = true;
    }

    @Override
    public void update() {
        checkWinner();
        updateCircles(m.getTargetArrayList());
        updatePlayersInfo(m.getClientArrayList());
        updatePlayers(m.getClientArrayList());
        updateArrows(m.getArrowsArrayList());
        if (isShowTable && m.getEntitiesList() != null && m.getEntitiesList().size() != 0) {
            alertPlayersTable();
            isShowTable = false;
        }
    }

    private void alertPlayersTable() {
        m.getEntitiesList().forEach(System.out::println);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                TableView tableView = new TableView();

                TableColumn<ClientInfo, String> column1 =
                        new TableColumn<>("Имя");

                column1.setCellValueFactory(
                        new PropertyValueFactory<>("playerName"));


                TableColumn<ClientInfo, String> column2 =
                        new TableColumn<>("Победы");

                column2.setCellValueFactory(
                        new PropertyValueFactory<>("wins"));

                tableView.getColumns().add(column1);
                tableView.getColumns().add(column2);

                m.getEntitiesList().forEach(tableView.getItems()::add);

                VBox vbox = new VBox(tableView);
                Scene scene = new Scene(vbox);
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.setTitle("Таблица лидеров");
                stage.show();
            }
        });
    }

    private void checkWinner() {
        if (m.getWinner() != null) {
            Platform.runLater(() -> {
                double x = gamePane.getScene().getWindow().getX();
                double y = gamePane.getScene().getWindow().getY();
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setX(x);
                alert.setY(y);
                alert.setTitle("У нас есть победитель!");
                alert.setHeaderText("У нас есть победитель!");
                alert.setContentText("Победитель : " + ((m.getWinner()).equals(this.playerName) ? "Вы" : m.getWinner()) + "!");
                alert.showAndWait();
            });

        }
    }



    private void updateCircles(ArrayList<MyPoint> a) {
        if (a == null || a.size() == 0) return;
        Platform.runLater(() -> {
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
        });
    }
    private void updateArrows(ArrayList<MyPoint> a) {
        if (a == null || a.size() == 0) return;
        Platform.runLater(() -> {
            arrows.forEach(arrow -> gamePane.getChildren().remove(arrow));
            for (MyPoint myPoint : a) {
                Arrow arr = new Arrow(myPoint.getX(), myPoint.getY(), myPoint.getR());
                arrows.add(arr);
                gamePane.getChildren().add(arr);

            }
        });

    }

    private void updatePlayersInfo(ArrayList<ClientInfo> a) {
        if (a == null || a.size() == 0) return;
        Platform.runLater(() -> {
            for (int i = 0; i < a.size(); i++) {
                if (i >= players.size()) {
                    VBox vb = PlayerInfoBox.createVbox(a.get(i));
                    playersInfo.add(vb);
                    infoBox.getChildren().add(vb);
                } else {
                    PlayerInfoBox.setPlayerName(playersInfo.get(i), a.get(i).getPlayerName());
                    PlayerInfoBox.setPlayerShots(playersInfo.get(i), a.get(i).getArrowsShoot());
                    PlayerInfoBox.setPlayerPoints(playersInfo.get(i), a.get(i).getPointsEarned());
                }
            }
        });

    }

    private void updatePlayers(ArrayList<ClientInfo> a) {
        if (a == null || a.size() == 0 || players.size() == a.size()) return;
        Platform.runLater(() -> {
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
        });


    }


}
