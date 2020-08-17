package ru.natlex.geo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ru.natlex.geo.TaskStatus;
import javax.persistence.*;


@Entity
@Table(name="task_requests")
public class TaskRequest {
    public final static int EXPORT = 0;
    public final static int IMPORT = 1;

    private long id;
    private int type ;
    private TaskStatus status;
    private byte[] attachment;
    private String error;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    @JsonIgnore
    public byte[] getAttachment() {
        return attachment;
    }

    public void setAttachment(byte[] attachment) {
        this.attachment = attachment;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

}
