package my.diploma.project.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Сущность "задача"
 *
 * @author Евгений Козлов
 */
@Entity
@Table(name = "tasks")
public class Task implements Serializable {

    public Task() {

    }


    @Transient
    public final static int maxShortDescriptionLength = 30;
    @Transient
    public final static int maxTaskDescriprionLength = 1000;

    @Id
    @GeneratedValue
    @Column(name = "task_id")
    private long id; //идентификатор (автоматически)
    @ManyToOne
    @JoinColumn(name = "login")
    private User author; //автор
    @Column(name = "task_deskription", nullable = false, length = Task.maxTaskDescriprionLength)
    private String taskDescription;     //полное описание
    @Column(name = "short_description", nullable = false, length = Task.maxShortDescriptionLength)
    private String shortDescription;        // краткое описание, длина 30 символов
    @Column(name = "creation_date", nullable = false)
    private Long creationDate;      //дата создания
    @Column(name = "todo_date")
    private Long toDoDate; //день, на который назначена задача
    @Column(name = "notification_needed")
    private boolean notificationNeeded;         //нужно ли уведомление
    @Column(name = "notify_date")
    private Long notifyDate;        //когда уведомить
    @Column(name = "is_group_of_tasks")
    private boolean groupOfTasks;  //является ли списком подзадач
    @OneToMany(mappedBy = "parentTask", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<SubTask> subTaskList; //список подзадач (один-ко-многим)

    @Column(name = "is_completed")
    private boolean completed;
    @Column(name = "last_update")
    private long lastUpdate;
    @Column(name = "deleted") //перемещена в корзину
    private boolean deleted;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public Long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Long creationDate) {
        this.creationDate = creationDate;
    }

    public Long getToDoDate() {
        return toDoDate;
    }

    public void setToDoDate(Long toDoDate) {
        this.toDoDate = toDoDate;
    }

    public boolean isNotificationNeeded() {
        return notificationNeeded;
    }

    public void setNotificationNeeded(boolean notificationNeeded) {
        this.notificationNeeded = notificationNeeded;
    }

    public Long getNotifyDate() {
        return notifyDate;
    }

    public void setNotifyDate(Long notifyDate) {
        this.notifyDate = notifyDate;
    }

    public boolean isGroupOfTasks() {
        return groupOfTasks;
    }

    public void setGroupOfTasks(boolean groupOfTasks) {
        this.groupOfTasks = groupOfTasks;
    }

    public List<SubTask> getSubTaskList() {
        return subTaskList;
    }

    public void setSubTaskList(List<SubTask> subTaskList) {
        this.subTaskList = subTaskList;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    /**
     * записываем время последнего изменения в задаче перед сохранением в БД
     */
    @PreUpdate
    @PrePersist
    public void setLastUpdateOnSave(){
        this.lastUpdate = new Date().getTime();
    }
}
