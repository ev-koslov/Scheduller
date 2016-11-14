package my.diploma.project.json.deserializer;

import com.google.gson.*;
import my.diploma.project.entity.SubTask;

import java.lang.reflect.Type;

/**
 * Десереализация обьекта SubTask из JSON
 *
 * @author Евгений Козлов
 */
public class SubTaskDeserializer implements JsonDeserializer<SubTask> {
    
    @Override
    public SubTask deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject subTaskJSON  = json.getAsJsonObject();
        SubTask subTask = new SubTask();
        subTask.setId(subTaskJSON.get("id").getAsLong());
        subTask.setCompleted(subTaskJSON.get("completed").getAsBoolean());
        subTask.setText(subTaskJSON.get("text").getAsString());
        return subTask;
    }
}
