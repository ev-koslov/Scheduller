package my.diploma.project.json.serializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import my.diploma.project.entity.SubTask;

import java.lang.reflect.Type;

/**
 * Created by Евгений on 06.01.2016.
 */
public class SubTaskSerializer implements JsonSerializer<SubTask> {
    @Override
    public JsonElement serialize(SubTask subTask, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", subTask.getId());
        jsonObject.addProperty("text", subTask.getText());
        jsonObject.addProperty("parentTask", subTask.getParentTask().getId());
        jsonObject.addProperty("completed", subTask.isCompleted());
        return jsonObject;
    }
}
