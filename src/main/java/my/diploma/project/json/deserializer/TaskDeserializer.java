package my.diploma.project.json.deserializer;

import com.google.gson.*;
import my.diploma.project.entity.SubTask;
import my.diploma.project.entity.Task;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Десериализатор обьекта Task
 *
 * @author Евгений Козлов
 */
public class TaskDeserializer implements JsonDeserializer<Task> {
    @Override
    public Task deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject taskJson = json.getAsJsonObject();
        Task task = new Task();
        task.setId(taskJson.get("id").getAsLong());
        task.setTaskDescription(taskJson.get("taskDescription").getAsString());
        task.setShortDescription(taskJson.get("shortDescription").getAsString());
        task.setToDoDate(taskJson.get("toDoDate").getAsLong());
        if (taskJson.get("notificationNeeded") != null){
            task.setNotificationNeeded(taskJson.get("notificationNeeded").getAsBoolean());
        }

        if (task.isNotificationNeeded()) {
            task.setNotifyDate(taskJson.get("notifyDate").getAsLong());
        }
        if (taskJson.get("groupOfTasks") != null){
            task.setGroupOfTasks(taskJson.get("groupOfTasks").getAsBoolean());
        }
        //если группа подзадач
        if (task.isGroupOfTasks()) {
            SubTask[] subTaskArray = context.deserialize(taskJson.get("subTaskList"), SubTask[].class);
            List<SubTask> subTaskList = new ArrayList<SubTask>();
            for (SubTask subTask: subTaskArray){
                subTaskList.add(subTask);
            }
            task.setSubTaskList(subTaskList);
        }
        task.setCompleted(taskJson.get("completed").getAsBoolean());
        task.setDeleted(taskJson.get("deleted").getAsBoolean());
        return task;
    }
}
