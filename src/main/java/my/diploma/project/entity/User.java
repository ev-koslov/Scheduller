package my.diploma.project.entity;


import my.diploma.project.entity.file.FsFile;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;


/**
 * Created by Евгений on 29.09.2015.
 */
@Entity
@Table(name = "users")
public class User implements Serializable {

    public User() {

    }

    @Transient
    public final static int maxLoginLength = 30; // max длина логина. Потом перенести в конфиг
    @Transient
    public final static int maxPasswordLength = 50; //max длина пароля
    @Transient
    public final static int minLoginLength = 4; // min длина логина.
    @Transient
    public final static int minPasswordLength = 4; //min длина пароля
    @Transient
    public final static int maxNameLength = 100; // max длина имени.
    @Transient
    public final static int maxSurnameLength = 100; //max длина фамилии

    @Id
    @Column(unique = true, nullable = false, length = maxLoginLength)
    private String login;
    @Column(length = maxPasswordLength)
    private String password;
    @Column(length = maxNameLength)
    private String name; //имя
    @Column(length = maxSurnameLength)
    private String surname; //фамилия
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Temporal(value = TemporalType.DATE)
    private Date birthday; //день рождения

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Task> taskList; //список задач

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FsFile> fileList; //список файлов

    //поля для взаимодействия с формами
    @Transient
    private String passwordConfirmation;
    @Transient
    private String oldPassword;

    //поля для валидатора
    @Transient
    private boolean newUser; //поле, говорящее валидатору, что создаем новый обьект.



    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public List<Task> getTaskList() {
        return taskList;
    }

    public void setTaskList(List<Task> taskList) {
        this.taskList = taskList;
    }

    public String getPasswordConfirmation() {
        return passwordConfirmation;
    }

    public void setPasswordConfirmation(String passwordConfirmation) {
        this.passwordConfirmation = passwordConfirmation;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public boolean isNewUser() {
        return newUser;
    }

    public void setNewUser(boolean newUser) {
        this.newUser = newUser;
    }

    public List<FsFile> getFileList() {
        return fileList;
    }

    public void setFileList(List<FsFile> fileList) {
        this.fileList = fileList;
    }
}
