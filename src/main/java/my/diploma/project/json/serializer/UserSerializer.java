package my.diploma.project.json.serializer;

import com.google.gson.*;
import my.diploma.project.entity.Task;
import my.diploma.project.entity.User;

import java.lang.reflect.Type;

/**
 * Created by Евгений on 06.01.2016.
 */

//Сериализация обьекта User в Google Gson
    //сериализируем все поля полностью,но без рекурсии
public class UserSerializer implements JsonSerializer<User>{
    @Override
    public JsonElement serialize(User src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("login", src.getLogin());
        jsonObject.addProperty("name", src.getName());
        jsonObject.addProperty("surname", src.getSurname());
        jsonObject.addProperty("birthday", src.getBirthday().getTime());

        if (src.getTaskList() != null && !src.getTaskList().isEmpty()) { //проверяем не пустое ли поле Задачи
            JsonArray jsonTasksArray = new JsonArray(); //создаем массив из JsonElement
            for (Task task: src.getTaskList()){ //для каждой задачи пользователя
                jsonTasksArray.add(context.serialize(task)); // вызываем касмомный сериализатор и добавляем обьект в массив
            }
            jsonObject.add("taskList", jsonTasksArray); //добавляем массив в финальный обьект
        }

        return jsonObject;
    }
}
