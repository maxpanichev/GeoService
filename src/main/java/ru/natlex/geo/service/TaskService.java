package ru.natlex.geo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.natlex.geo.*;
import ru.natlex.geo.DAO.TaskDAO;
import ru.natlex.geo.entity.TaskRequest;

import java.io.BufferedInputStream;
import java.io.IOException;


@Service
@Transactional
public class TaskService {
    TaskDAO taskDao;
    XLSBuilder builder;
    XLSReader reader;

    @Autowired
    public void setTaskDao(TaskDAO taskDao) {
        this.taskDao = taskDao;
    }

    @Autowired
    public void setBuilder(XLSBuilder builder) {
        this.builder = builder;
    }

    @Autowired
    public void setReader(XLSReader reader) {
        this.reader = reader;
    }

    public TaskRequest runExport() {
        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setStatus(TaskStatus.IN_PROGRESS);
        taskRequest.setType(TaskRequest.EXPORT);
        taskDao.add(taskRequest);
        builder.build(taskRequest);
        return taskRequest;
    }

    public TaskRequest getTaskRequest(long id, int type) {
        return taskDao.get(id, type);
    }

    public TaskRequest runImport(MultipartFile file) throws IOException {
        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setStatus(TaskStatus.IN_PROGRESS);
        taskRequest.setType(TaskRequest.IMPORT);
        taskDao.add(taskRequest);
        reader.read(new BufferedInputStream(file.getInputStream()), taskRequest);
        return taskRequest;
    }
}
