package com.example.javashooter.connection.responses;


import com.example.javashooter.connection.ClientInfo;
import com.example.javashooter.connection.database_hibernate.PlayerEntity;
import com.example.javashooter.myobjects.MyPoint;


import java.util.ArrayList;

public class ServerResp {
    public ArrayList<ClientInfo> clientArrayList;
    public ArrayList<MyPoint> circleArrayList;
    public ArrayList<MyPoint> targetArrayList;

    public String theWinnerIs;
    public ArrayList<ClientInfo> playersEntities;
}
