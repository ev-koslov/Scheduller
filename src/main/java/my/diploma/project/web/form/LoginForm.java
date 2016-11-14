package my.diploma.project.web.form;

/**
 * Created by Евгений on 24.11.2015.
 */

//форма авторизации. Принимает с фронтенда логин/пароль. Как и любая форма, после приема, должна пройти валидацию.

public class LoginForm {
    public LoginForm() {
    }
    //все проверки выполняем в валидаторе
    private String login;
    private String password;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login.toLowerCase();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
