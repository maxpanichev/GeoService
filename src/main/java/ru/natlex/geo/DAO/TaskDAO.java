package ru.natlex.geo.DAO;


import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.natlex.geo.entity.TaskRequest;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;


@Repository
@Transactional
public class TaskDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    SessionFactory sessionFactory;

    public void add(TaskRequest taskRequest) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            session.save(taskRequest);
            transaction.commit();
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

    public void insert(TaskRequest taskRequest) {
        jdbcTemplate.update(
                "INSERT INTO task_requests (type, status) VALUES (?, ?)",
                taskRequest.getType(), taskRequest.getStatus().index);
    }

    /** For use in @Async methods */
    public void update(TaskRequest taskRequest) {
        jdbcTemplate.update(
                "UPDATE task_requests SET status = ?, attachment = ?, error = ? where id = ?",
                taskRequest.getStatus().index,
                taskRequest.getAttachment(),
                taskRequest.getError(),
                taskRequest.getId());
    }

    public TaskRequest get(long id, int type) {
        Session session = null;
        try {
            session = sessionFactory.openSession();

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<TaskRequest> criteria = builder.createQuery(TaskRequest.class);

            Root<TaskRequest> root = criteria.from(TaskRequest.class);
            Predicate idPred = builder.equal(root.get("id"), id);
            Predicate typePred = builder.equal(root.get("type"), type);
            criteria.select(root);
            criteria.where(idPred, typePred);
            TypedQuery<TaskRequest> query = session.createQuery(criteria.select(root));
            return query.getSingleResult();
        } catch (NoResultException e) {
            System.out.println("Failed to find task with id " + id + ", type " + type);
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        } finally {
            if (session != null && session.isOpen())
                session.close();
        }
    }

//    public TaskRequest get(long id) {
//        return this.jdbcTemplate.queryForObject(
//                "select id, type, status from task_requests where id = ?",
//                new Object[]{1212L},
//                new RowMapper<TaskRequest>() {
//                    public TaskRequest mapRow(ResultSet rs, int rowNum) throws SQLException {
//                        TaskRequest taskRequest = new TaskRequest();
//                        taskRequest.setId(rs.getLong("id"));
//                        taskRequest.setType(rs.getInt("type"));
//                        taskRequest.setStatus(TaskStatus.valueOf(rs.getInt("status")));
//                        return taskRequest;
//                    }
//                });
//    }

}

