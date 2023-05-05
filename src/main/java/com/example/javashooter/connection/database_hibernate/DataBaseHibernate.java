package com.example.javashooter.connection.database_hibernate;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;


public class DataBaseHibernate implements IDataBase {

    SessionFactory sessionFactory = HibernateSessionFactory.getSessionFactory();

    public DataBaseHibernate() {}

    @Override
    public void addPlayer(PlayerEntity entity) {
        Session session = sessionFactory.openSession();
        Transaction txAdd = session.beginTransaction();
        session.persist(entity);
        txAdd.commit();
        session.close();
    }

    @Override
    public PlayerEntity getPlayerWins(String name) {
        Session session = sessionFactory.openSession();
        Transaction txAdd = session.beginTransaction();
        PlayerEntity foundEntity = session.get(PlayerEntity.class, name);
        txAdd.commit();
        session.close();
        return foundEntity;
    }

    @Override
    public void setPlayerWins(PlayerEntity entity) {
        Session session = sessionFactory.openSession();
        Transaction txAdd = session.beginTransaction();
        session.merge(entity);
        txAdd.commit();
        session.close();
    }

    @Override
    public void incrementPlayerWins(PlayerEntity entity) {

    }

    @Override
    public ArrayList<PlayerEntity> getAllPlayers() {

        List<PlayerEntity> list = sessionFactory.openSession().
                createQuery("From com.example.javashooter.connection.database_hibernate.PlayerEntity", PlayerEntity.class).list();

        return new ArrayList<>(list);
    }
}
