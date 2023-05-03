package com.example.javashooter;

import com.example.javashooter.connection.Model;
import com.example.javashooter.connection.ModelBuilder;
import com.example.javashooter.connection.responses.ServerResp;
import com.example.javashooter.connection.responses.SocketMesWrapper;
import com.google.gson.Gson;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class ClientStartFrame {
    Socket socket;
    final int port = 3124;
    InetAddress ip = null;

    SocketMesWrapper socketMesWrapper;
    @FXML
    TextField nameField;

    final Model m = ModelBuilder.build();
    public void onConnect(MouseEvent mouseEvent) {
        try {
            ip = InetAddress.getLocalHost();
            socket = new Socket(ip, port);
            socketMesWrapper = new SocketMesWrapper(socket);
            socketMesWrapper.sendMessage(nameField.getText().trim());
            String response = socketMesWrapper.getMessage();
            if (response.equals("ACCEPT")) {
                new Thread(
                        ()->
                        {
                            try {
                                while (true) {
                                    String s = socketMesWrapper.getData();
                                    System.out.println("Res: " + s);
                                    Gson gson = new Gson();
                                    ServerResp ra = gson.fromJson(s, ServerResp.class);
                                    m.setTargetArrayList(ra.circleArrayList);
                                    m.setClientArrayList(ra.clientArrayList);
                                    m.setArrowArrayList(ra.targetArrayList);
                                    m.setWinner(ra.theWinnerIs);
                                    m.setEntitiesList(ra.playersEntities);
                                    m.update();
                                }

                            } catch (IOException ignored) {

                            }

                        }
                ).start();
                openGamePage(mouseEvent);
            } else {
                double x = ((Node)(mouseEvent.getSource())).getScene().getWindow().getX();
                double y = ((Node)(mouseEvent.getSource())).getScene().getWindow().getY();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setX(x);
                alert.setY(y);
                alert.setTitle("Ошибка");
                alert.setHeaderText("Ошибка");
                alert.setContentText(response);

                alert.showAndWait();
                nameField.setText("");
            }

        } catch (IOException ignored) {

        }

    }



    private void openGamePage(Event event) {

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ClientFrame.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            Scene scene = new Scene(root1, 1100, 660);
            scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
            Stage stage = new Stage();
            stage.setResizable(false);
            stage.setTitle("Java Shooter Game.");
            stage.setScene(scene);
            stage.show();

            ((Node)(event.getSource())).getScene().getWindow().hide();

            ClientFrame clientFrame = fxmlLoader.getController();
            clientFrame.dataInit(socketMesWrapper, nameField.getText().trim());
            m.update();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }
}
