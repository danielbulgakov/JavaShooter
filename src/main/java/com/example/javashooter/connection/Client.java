package com.example.javashooter.connection;

import com.example.javashooter.connection.responses.ClientActions;
import com.example.javashooter.connection.responses.ClientReq;
import com.example.javashooter.connection.responses.ServerResp;
import com.example.javashooter.connection.responses.SocketMesWrapper;
import com.google.gson.Gson;

import java.io.*;
import java.net.Socket;

public class Client implements Runnable{

    MainServer mainServer;
    SocketMesWrapper socketMesWrapper;
    Gson gson = new Gson();
    Model model = ModelBuilder.build();
    ClientInfo clientData;

    public Client(SocketMesWrapper socketMesWrapper, MainServer mainServer, String playerName)  {
        this.socketMesWrapper = socketMesWrapper;
        this.mainServer = mainServer;
        clientData = new ClientInfo(playerName);
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
            serverResp.theWinnerIs = model.getWinner();

            socketMesWrapper.writeData(gson.toJson(serverResp));
        } catch (IOException ex) {
        }
    }



    @Override
    public void run() {
        try {

            System.out.println("Cilent thread " + clientData.getPlayerName() + " started");

            // Broadcast new player added
            model.addClient(clientData);
            mainServer.bcast();

            while(true)
            {
                String s = socketMesWrapper.getData();
                System.out.println("Msg: " + s);


                ClientReq msg = gson.fromJson(s, ClientReq.class);

                if(msg.getClientActions() == ClientActions.READY)
                {
                    System.out.println("READY " + getPlayerName());
                    model.ready(mainServer, this.getPlayerName());
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
    public ClientInfo getClientData() {
        return clientData;
    }

}
