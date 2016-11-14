package my.diploma.project.component;

import my.diploma.project.entity.SubTask;
import my.diploma.project.entity.Task;
import my.diploma.project.entity.User;
import my.diploma.project.service.UserService;
import my.diploma.project.web.form.LoginForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.Date;
import java.util.List;

/**
 * класс, осуществляющий проверку корректности заполнения формы при приеме её от клиента.
 *
 * @author Евгений Козлов
 *
 */



@Component
public class FormValidator {

    @Autowired
    private UserService userService;

    /**
     * Основной метод валидатора. Получает класс обьекта формы и вызывает необходимый валидатор
     * @param object обьект формы для валидации
     * @param result обьект для регистрации ошибок валидации формы
     */
    public void validate(Object object, BindingResult result) {
        //выбираем валидатор, который применим для данного класса.
        Class objClass = object.getClass();
        if (LoginForm.class.isAssignableFrom(objClass)) {
            this.validateLoginForm(object, result);
            return;
        }
        if (User.class.isAssignableFrom(objClass)) {
            this.validateUser(object, result);
            return;
        }
        if (Task.class.isAssignableFrom(objClass)) {
            this.validateTask(object, result);
            return;
        }
        if (SubTask.class.isAssignableFrom(objClass)) {
            this.validateSubTask(object, result);
            return;
        }
    }

    /**валидация формы авторизации
     *
     * @param object
     * @param result
     */
    private void validateLoginForm(Object object, BindingResult result) {
        LoginForm loginForm = (LoginForm) object;
        //проверяем, введены ли какие либо данные в поля
        if (loginForm.getLogin().isEmpty() || loginForm.getLogin() == null) {
            result.addError(new FieldError("loginForm", "login", "user.error.blankLogin"));
            return;
        }
        if (loginForm.getPassword().isEmpty() || loginForm.getPassword() == null) {
            result.addError(new FieldError("loginForm", "password", "user.error.blankPassword"));
            return;
        }
        //делаем логин в нижнем регистре
        loginForm.setLogin(loginForm.getLogin().toLowerCase());
        //проверяем длину полей (параметры берем из статических полей сущностей)
        if (loginForm.getLogin().length() > User.maxLoginLength || loginForm.getLogin().length() < User.minLoginLength) {
            result.addError(new FieldError("loginForm", "login", "user.error.loginLengthError"));
            return;
        }
        //длину пароля не проверяем. Поскольку это в случае чего сужает интервал подбора :)
    }


    /**валидация формы пользователя (поле пароля и поля Transient полей проверяем отдельно в контроллере - не всем методам нужны эти поля)
     *
     * @param object
     * @param result
     */
    @SuppressWarnings("deprecated")
    private void validateUser(Object object, BindingResult result) {
        User user = (User) object;
        //проверяем корректность заполнения логина (на длину строки и на пустую строку)
        if (user.getLogin().isEmpty() || user.getLogin() == null) {
            result.addError(new FieldError("user", "login", "user.error.blankLogin"));
            return;
        }
        //проверяем поле логина на длину
        if (user.getLogin().length() > User.maxLoginLength || user.getLogin().length() < User.minLoginLength) {
            result.addError(new FieldError("user", "login", "user.error.loginLengthError"));
            return;
        }
        //если стоит флаг "NewUser", значит обьект пришел из формы регистрации.
        if (user.isNewUser()) {
            //пароль должен быть заполнен при регистрации
            if (user.getPassword().isEmpty() || user.getPassword() == null) { //поле пароля пустое или null
                result.addError(new FieldError("user", "password", "user.error.blankPassword"));
                return;
            }
        } else {
            //если флаг не установлен, значит обьект пришел из формы изменения профиля. Там требуется заполненное поле oldPassword
            if (user.getOldPassword().isEmpty() || user.getOldPassword() == null) {
                result.addError(new FieldError("user", "oldPassword", "user.error.wrongPassword"));
                return;
            }
            //если поле пароля не пустое, значит проверяем длину пароля.
        }

        if (!user.getPassword().isEmpty()) {
            if (user.getPassword().length() > User.maxPasswordLength || user.getPassword().length() < User.minPasswordLength) {
                result.addError(new FieldError("user", "password", "user.error.passwordLengthError"));
                return;
            }
        }

        //поля PasswordConfirmation и Password должны совпадать в любом случае
        if (!user.getPasswordConfirmation().equals(user.getPassword())) {
            result.addError(new FieldError("user", "passwordConfirmation", "user.error.passwordAndConfirmationNotEquals"));
            return;
        }

        //проверяем корректность ввода даты рождения
        //пока проверяем только на null и на то, что дата рождения не может быть позже 1 января 2015


        if ((user.getBirthday() == null) || user.getBirthday().after(new Date(2015,1,1))) {
            result.addError(new FieldError("user", "birthday", "user.error.birthdayInvalid"));
            return;
        }

        if (user.getName().length()>User.maxNameLength){
            result.addError(new FieldError("user", "name", "user.error.nameLengthError"));
            return;
        }

        if (user.getSurname().length()>User.maxSurnameLength){
            result.addError(new FieldError("user", "name", "user.error.surnameLengthError"));
            return;
        }

        //делаем логин в нижнем регистре
        user.setLogin(user.getLogin().toLowerCase());

        //прописать дальнейшие провекри сущности User тут!
    }

    /**
     * Валидация обьекта "задача"
     * @param object
     * @param result
     */
    private void validateTask(Object object, BindingResult result) {
        Task task = (Task) object;

        //полное описание задачи не может быть пустым. Требуем заполнить.
        if (task.getTaskDescription().isEmpty()) {
            result.addError(new FieldError("task", "taskDescription", "task.error.emptyDescFields"));
        }

        if (task.getToDoDate() == null) {
            result.addError(new FieldError("task", "toDoDate", "task.error.wrongNotificationTime"));
        }

        //если стоит флаг "Уведомить", проверяем, корректно введена ли дата.
        if (task.isNotificationNeeded()) {
            if ((task.getNotifyDate() == null)) {
                result.addError(new FieldError("task", "notifyDate", "task.error.wrongNotificationTime"));
            }
            if (task.getNotifyDate() < new Date().getTime()){
                result.addError(new FieldError("task", "notifyDate", "task.error.pastNotificationTime"));
            }
        }

        if (task.isGroupOfTasks()) { //если флаг "группа подзадач" установлен, но
            if (task.getSubTaskList() == null || task.getSubTaskList().isEmpty()) { //если список пуст или null, бросаем ошибку
                result.addError(new FieldError("task", "groupOfTasks", "task.error.subTasksListIsEmpty"));
            } else { //если не пуст, перебираем все подзадачи на предмет ошибок
                List<SubTask> tempSubTaskList = task.getSubTaskList();
                for (int i=0; i<tempSubTaskList.size(); i++) { //пробегаемся по всему списку подзадач
                    if ((tempSubTaskList.get(i).getText() == null)|| tempSubTaskList.get(i).getText().isEmpty()) { //если текст пуст, FieldError
                        result.addError(new FieldError("task", "subTaskList["+i+"].text", "task.error.subTasksTextEmpty"));
                    }
                }
            }
        }

        //если у задачи установлен флаг "Выполнена", необходимо, чтобы все подзадачи тоже были выполнены
        if (task.isCompleted()){
            if (task.isGroupOfTasks()){
                for (SubTask subTask: task.getSubTaskList()){
                    if (!subTask.isCompleted()){
                        result.addError(new FieldError("task","completed","task.error.haveUncompletedSubTasks"));
                    }
                }
            }
        }
    }

    /**
     *
     * @param object
     * @param result
     */
    private void validateSubTask(Object object, BindingResult result) {

    }
}
