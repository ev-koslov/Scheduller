package my.diploma.project.json.serializer;

import com.google.gson.*;
import my.diploma.project.entity.SubTask;
import my.diploma.project.entity.Task;

import java.lang.reflect.Type;

/**
 * Created by Евгений on 06.01.2016.
 */
public class TaskSerializer implements JsonSerializer<Task> {
    @Override
    public JsonElement serialize(Task task, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", task.getId());
        jsonObject.addProperty("author", task.getAuthor().getLogin());
        jsonObject.addProperty("taskDescription", task.getTaskDescription());
        jsonObject.addProperty("shortDescription", task.getShortDescription());
        jsonObject.addProperty("creationDate", task.getCreationDate());
        jsonObject.addProperty("toDoDate", task.getToDoDate());
        jsonObject.addProperty("notificationNeeded", task.isNotificationNeeded());

        jsonObject.addProperty("notifyDate", task.getNotifyDate());

        if (task.isGroupOfTasks()) {
            jsonObject.addProperty("groupOfTasks", task.isGroupOfTasks());

            if (task.getSubTaskList() != null && !task.getSubTaskList().isEmpty()){
                JsonArray jsonSubTasksArray = new JsonArray();
                for (SubTask subTask: task.getSubTaskList()){
                    jsonSubTasksArray.add(context.serialize(subTask));
                }
                jsonObject.add("subTaskList", jsonSubTasksArray);
            } else {
                jsonObject.add("subTaskList", null);
            }
        }


        jsonObject.addProperty("completed", task.isCompleted());
        jsonObject.addProperty("lastUpdate", task.getLastUpdate());
        jsonObject.addProperty("deleted", task.isDeleted());
        return jsonObject;
    }
}
