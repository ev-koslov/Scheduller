package my.diploma.project.service.implementation;

import my.diploma.project.entity.SubTask;
import my.diploma.project.exception.SubTaskNotFound;
import my.diploma.project.repository.SubTaskRepository;
import my.diploma.project.service.SubTaskService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by Евгений on 17.11.2015.
 */
@Service
public class SubTaskServiceImpl implements SubTaskService {
    @Resource
    private SubTaskRepository subTaskRepository;

    @Override
    public SubTask create(SubTask subTask) { //создание подзадачи
        SubTask subTaskToCreate = subTaskRepository.saveAndFlush(subTask);
        return subTaskToCreate;
    }

    @Override
    public SubTask update(SubTask subTask) throws SubTaskNotFound {
        SubTask subTaskToUpdate = subTaskRepository.findOne(subTask.getId()); //находим запись
        if (subTaskToUpdate == null){ //если записи нет, бросаем исключение
            throw new SubTaskNotFound();
        }
        subTask.setId(subTaskToUpdate.getId()); //присваиваем новой подзадаче ID, который взяли из базы
        return subTaskRepository.saveAndFlush(subTask); //записываем измененную подзадачу в базу под старым ID и возвращаем ее сущность.
    }

    @Override
    public SubTask delete(long id) throws SubTaskNotFound {
        SubTask deletedSubTask = subTaskRepository.findOne(id); //изем в базе запись, которую надо удалить
        if (deletedSubTask == null){
            throw new SubTaskNotFound(); //если записи нет в базе, бросаем исключение
        }
        subTaskRepository.delete(deletedSubTask); //удаляем запись
        return deletedSubTask; //возвращаем обьект удаленной записи
    }

    @Override
    public SubTask findById(long id) {
        return subTaskRepository.findOne(id); //возвращаем обьект по его ID
    }

    @Override
    public List<SubTask> findAll() {
        return subTaskRepository.findAll(); //возвращаем все записи с таблицы.
    }
}
