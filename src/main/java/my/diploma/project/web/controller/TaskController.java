package my.diploma.project.web.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import my.diploma.project.component.FormValidator;
import my.diploma.project.entity.SubTask;
import my.diploma.project.entity.Task;
import my.diploma.project.exception.SubTaskNotFound;
import my.diploma.project.exception.TaskNotFound;
import my.diploma.project.json.deserializer.SubTaskDeserializer;
import my.diploma.project.json.deserializer.TaskDeserializer;
import my.diploma.project.json.serializer.SubTaskSerializer;
import my.diploma.project.json.serializer.TaskSerializer;
import my.diploma.project.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ViewResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Евгений on 19.11.2015.
 */
@Controller
@RequestMapping("/task")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private ViewResolver viewResolver;

    @Autowired
    private FormValidator formValidator;

    /**
     *     Отдает клиенту список всех задач, в которых аттрибут lastUpate > lastUpdateTime, который пришел с клиента
     * работает с измененными сериализаторами Google Gson (Для избежания рекурсии при ленивой инициализации с БД)
     *
     * @param lastUpdateTime время последнего обновления
     * @param request
     * @return строка, содержащая список измененных задач в формате JSON
     */
    @RequestMapping(value = "/get_all_json", method = RequestMethod.POST, produces = {"application/json; charset=UTF-8"})
    public @ResponseBody String getAllUserTasksWithUpdateTime(@RequestParam("lastUpdateTime") long lastUpdateTime,
                                                              HttpServletRequest request){
        //время старта метода
        Long startTime = System.currentTimeMillis();

        //создаем хэшмап для ответа
        Map<Object, Object> responseMap = new HashMap<Object, Object>();

        //Передаем принятые параметры и хэшмап в сервис для формирования массива с ответом
        taskService.findAllTaskByAuthorAndUpdateTimeAddingDeletedTasks(lastUpdateTime, new long[0], responseMap, request); //получаем список задач

        //добавляем в ответ время последнего запроса, в котором передаются измененные данные
        //если в карте есть данные, присваиваем время запуска метода
        if (responseMap.size() > 0) {
            responseMap.put("lastUpdateTime", startTime);
        }

        GsonBuilder gsonBuilder = new GsonBuilder(); //создаем фабрику Gson
        //Добавляем кастомные сериализаторы классов и разрешаем сериализировать null поля
        gsonBuilder.registerTypeAdapter(Task.class, new TaskSerializer());
        gsonBuilder.registerTypeAdapter(SubTask.class, new SubTaskSerializer());
        gsonBuilder.serializeNulls();

        Gson gson = gsonBuilder.create(); //создаем обьект из фабрики
        String responseJson = gson.toJson(responseMap); //сериализируем обьект responseMap
        return responseJson;
    }


    /**
     *  Создание новой задачи
     * @param task обьект Task
     * @param result
     * @param request
     * @return строку в формате JSON с результатом работы метода
     * @throws Exception
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST, produces = {"application/json; charset=UTF-8"}) //создание задачи
    public @ResponseBody String createNewTask(@ModelAttribute Task task, BindingResult result,
                                      HttpServletRequest request) throws Exception {

        formValidator.validate(task, result); //проверка пришедших данных на валидность

        Map<String, Object> responseMap = new HashMap<String, Object>(); //модель из которой строим JSON
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Task.class, new TaskSerializer())  //добавляем сериализаторы
                .registerTypeAdapter(SubTask.class, new SubTaskSerializer()).serializeNulls()
                .create(); //конструктор Json

        if (result.hasErrors()) { //если форма не прошла валидацию при приеме с клиента отдаем только результат (false) и список ошибок
            responseMap.put("allok", "false");
            responseMap.put("errors", result.getAllErrors());

        } else { //если все ОК, выполняем дальнейшие действия
            //выполняем необходимые преобразования и проверки (если необходимо, дописать блок)
            //-------------------------
            //передаем задачу сервису для записи в БД. Перед записью обьекту будет присвоено время создания,
            // автор(получаем с БД), заполнено поле краткого описания (если пустое) и каждой подзадаче родителем будет присвоена
            // создаваемая задача.
            task = taskService.create(task, request, result);

            //В модель записываем сообщение
            responseMap.put("message", "task.message.created" + task.getId());

            //Добавляем флаг, говорящий, что ошибок нет
            responseMap.put("allok", "true");

            //сериализируем только созданную задачу для отправки обратно клиенту
            responseMap.put("task", task);
        }

        return gson.toJson(responseMap);
    }

    /**
     * Редактирование задачи
     * @param task бьект задачи
     * @param result
     * @param request
     * @return строка в формате JSON с результатами работы метода
     * @throws TaskNotFound
     * @throws SubTaskNotFound
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST, produces = {"application/json; charset=UTF-8"})
    public @ResponseBody String doEditTask(@ModelAttribute Task task, BindingResult result, HttpServletRequest request)
                                            throws TaskNotFound, SubTaskNotFound {

        Map<String, Object> responseMap = new HashMap<String, Object>(); //модель из которой строим JSON

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Task.class, new TaskSerializer())  //добавляем сериализаторы
                .registerTypeAdapter(SubTask.class, new SubTaskSerializer()).serializeNulls()
                .create(); //конструктор Json

        formValidator.validate(task, result);

        if (result.hasErrors()) { //если форма не прошла валидацию при приеме с клиента отдаем только результат (false) и список ошибок
            responseMap.put("allok", "false");
            responseMap.put("errors", result.getAllErrors());

        } else { //если все ОК, выполняем дальнейшие действия
            //выполняем необходимые преобразования и проверки (если необходимо, дописать блок)
            //-------------------------
            //передаем задачу сервису для записи. Перед записью обьекту будет присвоено время создания,
            // автор(получаем с БД), заполнено поле краткого описания (если пустое) и каждой подзадаче родителем будет присвоена
            // создаваемая задача.
            task = taskService.update(task, request, result);

            //В модель записываем сообщение
            responseMap.put("message", "task.message.updated" + task.getId());

            //Добавляем флаг, говорящий, что ошибок нет
            responseMap.put("allok", "true");

            //сериализируем только созданную задачу для отправки обратно клиенту
            responseMap.put("task", task);
        }

    return gson.toJson(responseMap);
    }


    @RequestMapping(value = "/edit_from_json", method = RequestMethod.POST, produces = {"application/json; charset=UTF-8"}, consumes = {"application/json; charset=UTF-8"})
    public @ResponseBody
    String doEditTaskFromJson(@RequestBody String taskJson, BindingResult result, HttpServletRequest request) throws TaskNotFound {
        Map<String, Object> responseMap = new HashMap<String, Object>();
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Task.class, new TaskDeserializer())
                .registerTypeAdapter(SubTask.class, new SubTaskDeserializer())
                .create();
        Task taskFromJson = gson.fromJson(taskJson, Task.class);
        formValidator.validate(taskFromJson, result);
        if (result.hasErrors()){
            responseMap.put("allok", false);
            responseMap.put("errors", result.getAllErrors());
        } else {
            taskService.update(taskFromJson, request, result);
            responseMap.put("allok", true);
        }
        return gson.toJson(responseMap);
    }



    //Метод отдающий задачу на просмотр/редактирование. Сюда потом дописать проверку на права на просмотр задачи.
    @RequestMapping(value = "/open", method = RequestMethod.POST, produces = {"application/json; charset=UTF-8"})
    public @ResponseBody String openTask(@RequestParam(value = "id") long id,
                                         @RequestParam(value = "noHTML", required = false) boolean responseWithoutHTML,
                                         HttpServletRequest request) throws Exception {
        Map<String, Object> responseMap = new HashMap<String, Object>();
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Task.class, new TaskSerializer())
                .registerTypeAdapter(SubTask.class, new SubTaskSerializer())
                .serializeNulls()
                .create();
        String authUser = (String) request.getSession().getAttribute("auth");
        Task task = taskService.findById(id);

        //если в запросе пришел флаг "ответ без HTML", то формируем ответ без HTML данных (отдаем только задачу)

        if (responseWithoutHTML == true) {
            responseMap.put("task", task);
        } else {
            MockHttpServletResponse tempResponse = new MockHttpServletResponse();
            responseMap.put("task", new Task());
            viewResolver.resolveViewName("block/content/task-block-edit", request.getLocale()).render(responseMap, request, tempResponse);
            responseMap.put("task", task);
            responseMap.put("html", tempResponse.getContentAsString());
        }

        return gson.toJson(responseMap);
    }



    //удаление задач из базы
    @Transactional
    @RequestMapping(value = "/delete", method = RequestMethod.POST,
            consumes = {"application/json; charset=UTF-8"},
            produces = {"application/json; charset=UTF-8"})
    public @ResponseBody String doDeleteTask(@RequestBody String tasksToDeleteJson) throws TaskNotFound {
        Gson gson = new Gson();
        long[] tasksToDelete = gson.fromJson(tasksToDeleteJson, long[].class);
        for(long id: tasksToDelete){
            taskService.delete(id);
        }
        return gson.toJson(new HashMap<Object, Object>().put("allok", true));
    }

}
