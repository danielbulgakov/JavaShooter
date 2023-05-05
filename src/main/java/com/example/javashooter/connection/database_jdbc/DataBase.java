package com.example.javashooter.connection.database_jdbc;

import com.example.javashooter.connection.ClientInfo;
import com.example.javashooter.connection.Model;
import com.example.javashooter.connection.database_hibernate.IDataBase;
import com.example.javashooter.connection.database_hibernate.PlayerEntity;

import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DataBase {
    Connection c;
    void connect()
    {
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:player_stats.db");
            System.out.println("Opened database successfully");
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    public DataBase() {
        connect();
    }
    public void addPlayer(ClientInfo entity) {
        try {
            PreparedStatement pst =
                    c.prepareStatement("INSERT OR IGNORE INTO players(name, wins) VALUES (?,?)");
            pst.setString(1, entity.getPlayerName());
            pst.setInt(2, entity.getWins());
            pst.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public int getPlayerWins(ClientInfo entity) {
        try {
            PreparedStatement pst = c.prepareStatement("select * from players" +
                    "WHERE name = ?");
            pst.setString(2, entity.getPlayerName());

            ResultSet r= pst.executeQuery();

            r.next();
            return (r.getInt("wins"));


        } catch (SQLException ex) {
            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public void setPlayerWins(ClientInfo entity) {
        try {
            PreparedStatement pst =
                    c.prepareStatement("UPDATE players " +
                            "SET wins = ?" +
                            "WHERE name = ?");
            pst.setInt(1, entity.getWins());
            pst.setString(2, entity.getPlayerName());
            pst.executeUpdate();


        } catch (SQLException ex) {
            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public ArrayList<ClientInfo> getAllPlayers() {
        ArrayList<ClientInfo> res = new ArrayList<>();

        try {
            Statement st = c.createStatement();
            ResultSet r= st.executeQuery("select * from players ORDER BY wins DESC");

            while(r.next())
            {
                var client = new ClientInfo(r.getString("name"));
                client.setWins(r.getInt("wins"));
                res.add(client);
            }

        } catch (SQLException ex) {
            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
        }

        return res;
    }
}
