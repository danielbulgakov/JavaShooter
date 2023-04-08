package com.example.javashooter.connection.responses;

public class ClientReq {
    final ClientActions clientActions;
    public ClientReq(ClientActions clientActions) {
        this.clientActions = clientActions;
    }
    public ClientActions getClientActions() {
        return clientActions;
    }
}
