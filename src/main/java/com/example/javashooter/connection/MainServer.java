package com.example.javashooter.connection;

import com.example.javashooter.connection.responses.SocketStringMsg;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.net.ServerSocket;

public class MainServer {
    int port = 3124;
    InetAddress ip = null;
    ExecutorService service = Executors.newFixedThreadPool(4);
    ArrayList<Client> clientArrayList = new ArrayList<>();
    SocketStringMsg socketStringMsg;

    Model model = ModelBuilder.build();

    public void bcast(){
        clientArrayList.forEach(Client::sendInfoToClient);
    }
    public void serverStart(){
        ServerSocket ss;
        try {
            ip = InetAddress.getLocalHost();
            ss = new ServerSocket(port, 2, ip);
            System.out.append("Server start\n");
            model.init();

            while(true)
            {
                Socket cs;
                cs = ss.accept();
                socketStringMsg = new SocketStringMsg(cs);
                String respName = socketStringMsg.getMessage();

                if (tryAddClient(cs, respName)) {
                    System.out.println(respName + " Connected");
                } else {
                    cs.close();
                }
            }

        } catch (IOException ignored) {}
    }

    private boolean tryAddClient(Socket sock, String name) {
         if (clientArrayList.size() >= 4) {
             socketStringMsg.sendMessage("Превышено максимальное число подключений");
             return false;
         }
         if (clientArrayList.isEmpty() ||
                 clientArrayList.stream()
                .filter(client -> client.getPlayerName().equals(name))
                .findFirst()
                .orElse(null) == null) {
             socketStringMsg.sendMessage("ACCEPT");
             Client c = new Client(sock, this, name);
             clientArrayList.add(c);
             service.submit(c);
             System.out.println("RESPONSE ACCEPT");
             return true;
         }
        socketStringMsg.sendMessage("Уже имеется игрок с таким именем");
        System.out.println("RESPONSE DECLINE");
        return false;
    }



    public static void main(String[] args) {
        new MainServer().serverStart();
    }

}
