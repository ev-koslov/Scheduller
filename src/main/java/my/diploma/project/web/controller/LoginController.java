package my.diploma.project.web.controller;

import my.diploma.project.component.FormValidator;
import my.diploma.project.entity.User;
import my.diploma.project.service.UserService;
import my.diploma.project.web.form.LoginForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;

/**
 * Контроллер авторизации
 *
 * @author Евгений Козлов
 */
@Controller
public class LoginController {

    @Autowired
    private UserService userService; //сервис взаимодействия с сущностью User

    @Autowired
    private FormValidator formValidator;

    /**
     * Возвращает страницу авторизации
     */
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView loginPage() {
        //возвращаем страницу логина и пустой обьект LoginForm
        return new ModelAndView("login", "loginForm", new LoginForm());
    }

    /**
     * Выполняет обработку формы авторизации инепосредственно авторизацию пользователя
     */
    @Transactional(readOnly = true)
    @RequestMapping(value = "/login", method = RequestMethod.POST) //выполнение авторизации
    public ModelAndView doLogin(HttpServletRequest request, @ModelAttribute("loginForm") LoginForm loginForm,
                                BindingResult result, final RedirectAttributes redirectAttributes) {
        ModelAndView modelAndView = new ModelAndView();
        //валидация принятой формы
        formValidator.validate(loginForm, result);
        //проверка результатов валидации формы. Если есть ошибки, дальнейшие действия бессмысленны, возвращаем страницу и ошибки
        //если ошибок нет, продолжаем
        if (result.hasErrors()) {
            modelAndView.setViewName("/login");
            return modelAndView;
        }
        //проверяем наличие пользователя в БД
        User userToLogin = userService.findByLogin(loginForm.getLogin());
        if (userToLogin == null) { //если пользователь не найден, добавляем ошибку и возвращаем форму клиенту
            result.addError(new FieldError("loginForm", "login", "user.error.notFound"));
            modelAndView.setViewName("/login");
            return modelAndView;
        }
        //проверяем, соответствует ли введенный пароль паролю в БД, если нет - возврат ошибки и формы клиенту
        if (!userToLogin.getPassword().equals(loginForm.getPassword())) {
            result.addError(new FieldError("loginForm", "password", "user.error.wrongPassword"));
            modelAndView.setViewName("/login");
            return modelAndView;
        }
        //Если ошибок нет, выполненяем авторизацию путем добавления к сессии аттрибута auth="логин пользователя" в нижнем регистре
        request.getSession().setAttribute("auth", userToLogin.getLogin());
        modelAndView.setViewName("redirect:/index"); //переадресация на главную страницу с сообщением.
        redirectAttributes.addFlashAttribute("message", "user.message.welcome");
        return modelAndView;
    }

    /**
     * Возвращает страницу регистрации
     */
    @RequestMapping(value = "/register", method = RequestMethod.GET) //страница регистрации пользователя
    public ModelAndView registerPage() {
        //отдаем модель и вьюшку с обьектом User
        return new ModelAndView("register", "user", new User());
    }

    /**
     * Метод выполняет проверку заполнения формы пользователя. В зависимости от результатов выполняет регистрацию
     * пользователя, либо возвращает страницу с ошибками
     */
    @Transactional
    @RequestMapping(value = "/register", method = RequestMethod.POST) //выполнение регистрации пользователя
    public ModelAndView doRegister(@ModelAttribute("user") User user, BindingResult result,
                                   final RedirectAttributes redirectAttributes) throws ParseException {
        ModelAndView modelAndView = new ModelAndView();
        //пароль нового пользователя не может быть пустым! (поэтому проставляем в обьекте флаг newUser на true)
        //по этому флагу валидатор будет знать, что данные пришли с формы регистрации.
        user.setNewUser(true);
        //проводим валидацию принятого обьекта
        formValidator.validate(user, result);
        if (result.hasErrors()) { //форма не прошла валидацию, возвращаем ошибку клиенту (требуем исправить ошибки)
            modelAndView.setViewName("register");
            return modelAndView;
        }
        //делаем логин в нижнем регистре
        user.setLogin(user.getLogin().toLowerCase());

        //записываем пользователя в БД
        user = userService.create(user);
        modelAndView.setViewName("redirect:/login"); //переадресация на главную страницу
        redirectAttributes.addFlashAttribute("message", "user.message.created" + user.getLogin()); //добавляем аттрибуты переадресации
        return modelAndView;
    }

    /**
     * Выход пользователя из системы
     */
    @RequestMapping(value = "/logoff", method = RequestMethod.GET) //выход из системы
    public ModelAndView doLogOff(HttpServletRequest request, final RedirectAttributes redirectAttributes) {
        request.getSession().invalidate(); //обнуляем сессию пользователя
        ModelAndView modelAndView = new ModelAndView("redirect:/login"); //переадресация на страницу логина (обращение к контроллеру пути /login)
        redirectAttributes.addFlashAttribute("message", "user.message.loggedOff"); //добавление сообщения
        return modelAndView;
    }
}
