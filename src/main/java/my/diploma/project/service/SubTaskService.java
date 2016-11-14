package my.diploma.project.service;

import my.diploma.project.entity.SubTask;
import my.diploma.project.exception.SubTaskNotFound;

import java.util.List;

/**
 * Created by Евгений on 17.11.2015.
 */
public interface SubTaskService {
    SubTask create(SubTask subTask);
    SubTask update(SubTask subTask) throws SubTaskNotFound;
    SubTask delete(long id) throws SubTaskNotFound;
    SubTask findById(long id);
    List<SubTask> findAll();
}
