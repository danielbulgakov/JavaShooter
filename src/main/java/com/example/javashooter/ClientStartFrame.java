package com.example.javashooter;

import com.example.javashooter.connection.Model;
import com.example.javashooter.connection.ModelBuilder;
import com.example.javashooter.connection.responses.ServerResp;
import com.example.javashooter.connection.responses.SocketStringMsg;
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
    int port = 3124;
    InetAddress ip = null;

    InputStream is;
    OutputStream os;
    DataInputStream dis;
    DataOutputStream dos;

    SocketStringMsg skm;
    @FXML
    TextField nameField;

    Model m = ModelBuilder.build();
    public void onConnect(MouseEvent mouseEvent) {
        try {
            ip = InetAddress.getLocalHost();
            socket = new Socket(ip, port);
            skm = new SocketStringMsg(socket);
            skm.sendMessage(nameField.getText().trim());
            String response = skm.getMessage();
            if (response.equals("ACCEPT")) {
                new Thread(
                        ()->
                        {
                            try {
                                is = socket.getInputStream();
                                dis = new DataInputStream(is);
                                while (true) {
                                    String s = dis.readUTF();
                                    System.out.println("Res: " + s);
                                    Gson gson = new Gson();
                                    ServerResp ra = gson.fromJson(s, ServerResp.class);
                                    m.setTargetArrayList(ra.circleArrayList);
                                    m.setClientArrayList(ra.clientArrayList);
                                    m.setArrowArrayList(ra.targetArrayList);
                                    m.update();
                                }

                            } catch (IOException ex) {

                            }

                        }
                ).start();
                openGamePage(mouseEvent);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
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
            stage.setResizable(true);
            stage.setTitle("Java Shooter Game.");
            stage.setScene(scene);
            stage.show();
            ((Node)(event.getSource())).getScene().getWindow().hide();

            ClientFrame clientFrame = fxmlLoader.getController();
            clientFrame.dataInit(socket, nameField.getText());
            m.update();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }
}
