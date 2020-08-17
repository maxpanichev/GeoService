package ru.natlex.geo.DAO;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.natlex.geo.entity.TaskRequest;
import ru.natlex.geo.entity.User;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

@Repository
@Transactional
public class UserDAO {
    @Autowired
    SessionFactory sessionFactory;

    public User getUserByUsername(String username) {
        Session session = null;
        try {
            session = sessionFactory.openSession();

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<User> criteria = builder.createQuery(User.class);
            Root<User> root = criteria.from(User.class);
            Predicate namePred = builder.equal(root.get("username"), username);
            criteria.select(root);
            criteria.where(namePred);
            TypedQuery<User> query = session.createQuery(criteria.select(root));
            return query.getSingleResult();
        } catch (NoResultException e) {
            System.out.println("No such user " + username);
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        } finally {
            if (session != null && session.isOpen())
                session.close();
        }

    }
}
