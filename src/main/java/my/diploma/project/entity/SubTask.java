package my.diploma.project.entity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Сущность "подзадача"
 *
 * @author Евгений Козлов
 */
@Entity(name = "subtasks")
public class SubTask implements Serializable {
    public SubTask(){

    }

    /**
     *
     * @param text текст подзадачи
     * @param number номер по порядку в списке подзадач
     * @param parentTask задача-владелец
     */
    public SubTask(String text, int number, Task parentTask) {
        this.text = text;
        this.parentTask = parentTask;
    }
    @Id
    @GeneratedValue
    @Column(name = "subtask_id")
    private long id; //идентификатор (автоматически)
    private boolean completed; //выполнена ли?
    @Column(nullable = false)
    private String text; //текст
    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task parentTask;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Task getParentTask() {
        return parentTask;
    }

    public void setParentTask(Task parentTask) {
        this.parentTask = parentTask;
    }
}
