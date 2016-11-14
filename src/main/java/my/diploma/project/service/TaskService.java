package my.diploma.project.service;

import my.diploma.project.entity.Task;
import my.diploma.project.exception.TaskNotFound;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.BindingResult;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * Created by Евгений on 17.11.2015.
 */
public interface TaskService {
    //ниже идут сервисы, забранные с контроллеров
    Task create(Task task, HttpServletRequest request, BindingResult result); //создание новой задачи
    Task update(Task task, HttpServletRequest request, BindingResult result) throws TaskNotFound;
    Task delete(long id) throws TaskNotFound;
    Task findById(long id);
    List<Task> findAll();
    Page<Task> findAll(Pageable pageable);

    //для нового интерфейса
    void findAllTaskByAuthorAndUpdateTimeAddingDeletedTasks(long lastUpdateTime, long[] checkForExist,
                                                                  Map<Object, Object> responseMap, HttpServletRequest request);


}
