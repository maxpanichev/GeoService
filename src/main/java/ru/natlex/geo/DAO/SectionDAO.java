package ru.natlex.geo.DAO;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;
import ru.natlex.geo.entity.GeologicalClass;
import ru.natlex.geo.entity.Section;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.List;


@Repository
public class SectionDAO {
    @Autowired
    SessionFactory sessionFactory;

        public Section add(Section section) {
            Session session = null;
            Transaction transaction = null;
            try {
                session = sessionFactory.openSession();
                transaction = session.beginTransaction();
                session.save(section);
                transaction.commit();
                session.refresh(section);
                return section;
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

        public Section get(long id) {
            Session session = null;
            try {
                session = sessionFactory.openSession();
                return session.get(Section.class, id);
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

        public List<Section> getAll() {
            Session session = null;
            try {
                session = sessionFactory.openSession();
                CriteriaBuilder builder = session.getCriteriaBuilder();
                CriteriaQuery<Section> criteriaQuery = builder.createQuery(Section.class);
                Root<Section> sectionRoot = criteriaQuery.from(Section.class);
                TypedQuery<Section> typedQuery = session.createQuery(criteriaQuery.select(sectionRoot));
                return typedQuery.getResultList();
            } catch (Exception e) {
                e.printStackTrace();
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            } finally {
                if (session != null && session.isOpen())
                    session.close();
            }
        }

        public Section update(Section section) {
            Session session = null;
            Transaction transaction = null;
            try {
                session = sessionFactory.openSession();
                transaction = session.beginTransaction();
                session. update(section);
                transaction.commit();
                session.refresh(section);
                return section;
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

        public void delete(Section section) {
            Session session = null;
            try {
                session = sessionFactory.openSession();
                session.beginTransaction();
                session.delete(section);
                session.getTransaction().commit();
            } catch (Exception e) {
                e.printStackTrace();
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            } finally {
                if (session != null && session.isOpen())
                    session.close();
            }
        }

        public List<Section> findByGeoCode(String code) {
            Session session = null;
            try {
                session = sessionFactory.openSession();
                CriteriaBuilder builder = session.getCriteriaBuilder();
                CriteriaQuery<Section> criteria = builder.createQuery(Section.class);
                Root<Section> sectionRoot = criteria.from(Section.class);
                Join<Section, GeologicalClass> join = sectionRoot.join("geologicalClasses", JoinType.INNER);
                criteria.where(builder.equal(join.get("code"), code)).distinct(true);
                TypedQuery<Section> typedQuery = session.createQuery(criteria);
                List<Section> results = typedQuery.getResultList();
                return results;
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
    }
