package com.example.javashooter.connection.database;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;

public class DataBaseHibernate implements IDataBase {

    SessionFactory sessionFactory = HibernateSessionFactory.getSessionFactory();

    public DataBaseHibernate() {}

    @Override
    public void addPlayer(PlayersEntity entity) {
        Session session = sessionFactory.openSession();
        Transaction txAdd = session.beginTransaction();
        session.persist(entity);
        txAdd.commit();
        session.close();
    }

    @Override
    public PlayersEntity getPlayerWins(String name) {
        Session session = sessionFactory.openSession();
        Transaction txAdd = session.beginTransaction();
        PlayersEntity foundEntity = session.get(PlayersEntity.class, name);
        txAdd.commit();
        session.close();
        return foundEntity;
    }

    @Override
    public void setPlayerWins(PlayersEntity entity) {
        Session session = sessionFactory.openSession();
        Transaction txAdd = session.beginTransaction();
        session.merge(entity);
        txAdd.commit();
        session.close();
    }

    @Override
    public ArrayList<PlayersEntity> getAllPlayers() {
//        Session session = sessionFactory.openSession();
//        CriteriaBuilder cb = session.getCriteriaBuilder();
//        CriteriaQuery<PlayersEntity> cq = cb.createQuery(PlayersEntity.class);
//        Root<PlayersEntity> rootEntry = cq.from(PlayersEntity.class);
//        CriteriaQuery<PlayersEntity> all = cq.select(rootEntry);
//
//        TypedQuery<PlayersEntity> allQuery = session.createQuery(all);
//        return (ArrayList<PlayersEntity>) allQuery.getResultList();
        return null;
    }
}
