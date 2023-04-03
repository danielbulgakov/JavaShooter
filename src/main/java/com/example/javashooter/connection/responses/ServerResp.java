package com.example.javashooter.connection.responses;


import com.example.javashooter.connection.ClientDataManager;
import com.example.javashooter.myobjects.MyPoint;


import java.util.ArrayList;

public class ServerResp {
    public ArrayList<ClientDataManager> clientArrayList;
    public ArrayList<MyPoint> circleArrayList;
    public ArrayList<MyPoint> targetArrayList;
}
