package com.example.javashooter.connection.responses;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketStringMsg {
    Socket socket;
    BufferedReader in = null;
    PrintWriter out = null;
    public SocketStringMsg(Socket socket) {
        this.socket = socket;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public String getMessage() {

        try {
            String word = in.readLine();
            return word;
        } catch (IOException ignored) {}
        return null;
    }
    public void sendMessage(String str){
        out.println(str);
    }


}
