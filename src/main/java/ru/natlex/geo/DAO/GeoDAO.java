package ru.natlex.geo.DAO;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;
import ru.natlex.geo.entity.GeologicalClass;

import javax.persistence.NoResultException;

@Repository
public class GeoDAO {
    @Autowired
    SessionFactory sessionFactory;

    public GeologicalClass add(GeologicalClass geo) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            session.save(geo);
            transaction.commit();
            session.refresh(geo);
            return geo;
        } catch (Exception e) {
            if (transaction != null)
                transaction.rollback();

            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        } finally {
            if (session != null && session.isOpen())
                session.close();
        }
    }

    public GeologicalClass get(long id) {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            return session.get(GeologicalClass.class, id);
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        } finally {
            if (session != null && session.isOpen())
                session.close();
        }
    }

    public GeologicalClass update(GeologicalClass geo) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            session. update(geo);
            transaction.commit();
            session.refresh(geo);
            return geo;
        } catch (Exception e) {
            if (transaction != null)
                transaction.rollback();

            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        } finally {
            if (session != null && session.isOpen())
                session.close();
        }
    }

    public void delete(GeologicalClass geo) {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            session.delete(geo);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        } finally {
            if (session != null && session.isOpen())
                session.close();
        }
    }
}
