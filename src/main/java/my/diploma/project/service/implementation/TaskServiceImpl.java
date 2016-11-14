package my.diploma.project.service.implementation;

import my.diploma.project.entity.SubTask;
import my.diploma.project.entity.Task;
import my.diploma.project.exception.TaskNotFound;
import my.diploma.project.repository.TaskRepository;
import my.diploma.project.service.TaskService;
import my.diploma.project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Created by Евгений on 17.11.2015.
 */
@Service
public class TaskServiceImpl implements TaskService {

    //В этом хэше храним список задач, которые были удалены за время работы сервера (обнуляется при перезапуске)
    //Сделан для того, чтобы каждый раз не опрашивать базу на предмет наличия списка ID задач в базе
    private static HashSet<Long> deletedTasksSet = new HashSet<Long>();

    @Resource
    private TaskRepository taskRepository;

    @Autowired
    private UserService userService;


    //поиск всех задач текущего автора и временем обновления больше переданного,
    //также формирование списка задач, которых больше нет в базе

    @Override
    @Transactional(readOnly = true)
    public void findAllTaskByAuthorAndUpdateTimeAddingDeletedTasks(long lastUpdateTime, long[] checkForExist,
                                                                         Map<Object, Object> responseMap, HttpServletRequest request) {
        //Проверяем, какие задачи были удалены
        for (long id: checkForExist){
            //если ID есть в списке, значит, задача была удалена
            if (deletedTasksSet.contains(id)){
                responseMap.put(id, -1);
            }
        }

        //Получаем с сессии логин пользователя
        String userLogin = (String) request.getSession().getAttribute("auth");
        //Получаем список задач пользователя согласно параметрам запроса
        List<Task> taskList = taskRepository.findAllTaskByAuthorAndUpdateTime(userLogin, lastUpdateTime);
        //если длина списка больше 0, то каждую задачу добавляем в МАП в виде ID - Task
        if (taskList.size() > 0){
            for (Task task: taskList){
                responseMap.put(task.getId(), task);
            }
        }

    }

    //создание новой задачи
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Task create(Task task, HttpServletRequest request, BindingResult result) {
        //устанавливаем дату создания
        task.setCreationDate(new Date().getTime());
        //устанавливаем автора
        task.setAuthor(userService.findByLogin((String) request.getSession().getAttribute("auth")));

        //если краткое описание пустое, заполняем его из полного (если полное длиннее, чем макс длина краткого,
        // то заполняем на макс длину, длину берем из параметров обьекта)
        if (task.getShortDescription().isEmpty()) {
            if (task.getTaskDescription().length() > Task.maxShortDescriptionLength) {
                task.setShortDescription(task.getTaskDescription().substring(0, Task.maxShortDescriptionLength - 3) + "...");
            } else {
                task.setShortDescription(task.getTaskDescription());
            }
        }

        //каждой подзадаче присваиваем родителем создаваемую задачу
        if (task.isGroupOfTasks() && task.getSubTaskList() != null) {
            for (SubTask subTask : task.getSubTaskList()) {
                subTask.setParentTask(task);
            }
        } else {
            //если не группа подзадач, присваиваем списку подзадач пустой массив
            task.setSubTaskList(new ArrayList<SubTask>());
        }

        //Пишем в базу и возвращаем записанный обьект.
        return taskRepository.saveAndFlush(task);
    }

    //обновление существующей задачи
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Task update(Task task, HttpServletRequest request, BindingResult result) throws TaskNotFound {
        //получаем задачу с базы
        Task taskFromDB = this.findById(task.getId());

        //Из базы обновляемой задаче устанавливаем время создания и автора
        task.setAuthor(userService.findByLogin((String) request.getSession().getAttribute("auth")));
        task.setCreationDate(taskFromDB.getCreationDate());

        //если краткое описание пустое, заполняем его из полного (если полное длиннее, чем макс длина краткого,
        // то заполняем на макс длину, длину берем из параметров обьекта)
        if (task.getShortDescription().isEmpty()) {
            if (task.getTaskDescription().length() > Task.maxShortDescriptionLength) {
                task.setShortDescription(task.getTaskDescription().substring(0, Task.maxShortDescriptionLength - 3) + "...");
            } else {
                task.setShortDescription(task.getTaskDescription());
            }
        }

        //каждой подзадаче присваиваем родителем создаваемую задачу
        if (task.isGroupOfTasks() && task.getSubTaskList() != null) {
            for (SubTask subTask : task.getSubTaskList()) {
                subTask.setParentTask(task);
            }
        } else {
            task.setSubTaskList(new ArrayList<SubTask>());
        }

        //Пишем данніе в БД и возвращаем обьект контроллеру
        return taskRepository.saveAndFlush(task);
    }


    //удаление задачи (в корзину или полностью)
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Task delete(long id) throws TaskNotFound {
        Task taskToDelete = taskRepository.findOne(id);
        if (taskToDelete == null) {
            throw new TaskNotFound();
        }

        taskRepository.delete(taskToDelete);

        //Добавляем ID удаленной задачи в хэшсет
        deletedTasksSet.add(id);
        return taskToDelete;
    }


//-----------------------

    @Override
    public Task findById(long id) {
        return taskRepository.findOne(id);
    }

    @Override
    public List<Task> findAll() {
        return taskRepository.findAll();
    }

    @Override
    public Page<Task> findAll(Pageable pageable) {
        return taskRepository.findAll(pageable);
    }


}
