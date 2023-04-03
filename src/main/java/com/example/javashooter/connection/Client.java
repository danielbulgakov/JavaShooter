package com.example.javashooter.connection;

import com.example.javashooter.connection.responses.ClientActions;
import com.example.javashooter.connection.responses.ClientReq;
import com.example.javashooter.connection.responses.ServerResp;
import com.google.gson.Gson;

import java.io.*;
import java.net.Socket;

public class Client implements Runnable{
    Socket socket;
    MainServer mainServer;
    InputStream inputStream;
    OutputStream outputStream;
    DataInputStream dataInputStream;
    DataOutputStream dataOutputStream;
    Gson gson = new Gson();
    Model model = ModelBuilder.build();
    ClientDataManager clientData;
    boolean isReady = false;

    public Client(Socket socket, MainServer mainServer, String playerName)  {
        this.socket = socket;
        this.mainServer = mainServer;
        clientData = new ClientDataManager(playerName);
        try {
            outputStream = socket.getOutputStream();
            dataOutputStream = new DataOutputStream(outputStream);
        } catch (IOException ignored) { }
    }
    public String getPlayerName() {
        return clientData.getPlayerName();
    }

    public void sendInfoToClient() {
        try {
            ServerResp serverResp = new ServerResp();
            serverResp.clientArrayList = model.getClientArrayList();
            serverResp.targetArrayList = model.getArrowsArrayList();
            serverResp.circleArrayList = model.getTargetArrayList();

            String s = gson.toJson(serverResp);
            dataOutputStream.writeUTF(s);
        } catch (IOException ex) {
        }
    }



    @Override
    public void run() {
        try {
            inputStream = socket.getInputStream();
            dataInputStream = new DataInputStream(inputStream);

            System.out.println("Cilent thread " + clientData.getPlayerName() + " started");

            // Broadcast new player added
            model.addClient(clientData);
            mainServer.bcast();

            while(true)
            {
                String s = dataInputStream.readUTF();
                System.out.println("Msg: " + s);


                ClientReq msg = gson.fromJson(s, ClientReq.class);

                if(msg.getClientActions() == ClientActions.READY)
                {
                    isReady = !isReady;
                    if (isReady) model.ready(mainServer);
                    else model.notReady();
                }

                if(msg.getClientActions() == ClientActions.STOP)
                {
                    model.requestPause(getPlayerName());
                }
                if (msg.getClientActions() == ClientActions.SHOOT) {
                    model.requestShoot(getPlayerName());
                }


            }
        } catch (IOException ignored) {

        }
    }
    public ClientDataManager getClientData() {
        return clientData;
    }

}
