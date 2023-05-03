package com.example.javashooter.connection;

import com.example.javashooter.connection.responses.ClientActions;
import com.example.javashooter.connection.responses.ClientReq;
import com.example.javashooter.connection.responses.ServerResp;
import com.example.javashooter.connection.responses.SocketMesWrapper;
import com.google.gson.Gson;

import java.io.*;

public class Client implements Runnable{

    final MainServer mainServer;
    final SocketMesWrapper socketMesWrapper;
    final Gson gson = new Gson();
    final Model model = ModelBuilder.build();
    final ClientInfo clientData;

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
            serverResp.playersEntities = model.getEntitiesList();
            serverResp.theWinnerIs = model.getWinner();

            socketMesWrapper.writeData(gson.toJson(serverResp));
        } catch (IOException ignored) {
        }
    }



    @Override
    public void run() {
        try {

            System.out.println("Client thread " + clientData.getPlayerName() + " started");

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
                if (msg.getClientActions() == ClientActions.SHOOT)
                {
                    model.requestShoot(getPlayerName());
                }
                if (msg.getClientActions() == ClientActions.SCORE_TABLE)
                {
                    model.updateScoreTable();
                }


            }
        } catch (IOException ignored) {

        }
    }
    public ClientInfo getClientData() {
        return clientData;
    }

}
