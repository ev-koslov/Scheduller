package my.diploma.project.web.controller;

import my.diploma.project.component.FormValidator;
import my.diploma.project.entity.User;
import my.diploma.project.exception.UserNotFound;
import my.diploma.project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Евгений on 24.11.2015.
 */
@Controller
@RequestMapping(value = "/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private FormValidator formValidator;


    @Transactional(readOnly = true)
    @RequestMapping(value = "/view/{login}", method = RequestMethod.GET) //страница профиля пользователя.
    public ModelAndView openUserProfile(@PathVariable(value = "login") String login, HttpServletRequest request,
                                        final RedirectAttributes redirectAttributes) {
        User user = userService.findByLogin(login.toLowerCase()); //поиск пользователя в БД (логин только в нижнем регистре).
        ModelAndView modelAndView = new ModelAndView();
        if (user == null) { //если пользователя нет, переадресовуем на страницу с ошибкой
            modelAndView.setViewName("redirect:/error");
            redirectAttributes.addFlashAttribute("error", "user.error.notFound");
            return modelAndView;
        }
        //сравниваем логин запрошенного пользователя и логин авторизованного в данной сессии пользователя,
        //если совпадают, отдаем страницу редактирования своего профиля, если нет - страницу просмотра запрошенного профиля
        if (user.getLogin().equalsIgnoreCase((String) request.getSession().getAttribute("auth"))) {
            modelAndView.setViewName("user/edit"); //страница редактирования
        } else {
            modelAndView.setViewName("user/view"); //страница просмотра
        }
        //добавляем в модель обьект пользователя
        modelAndView.addObject("user", user);
        return modelAndView;
    }

    //(ГОТОВ)
    @Transactional
    @RequestMapping(value = "/edit", method = RequestMethod.POST) //сохранение изменений своего профиля.
    public ModelAndView editMyProfile(@ModelAttribute User user, BindingResult result,
                                      final RedirectAttributes redirectAttributes) throws UserNotFound {
        ModelAndView modelAndView = new ModelAndView();
        //тут валидатору надо явно указать, что это обьект пришел из метода "редактирование пользователя".
        //поле "Пароль" необязательно к заполнению, но требуется ввод страрого пароля.
        user.setNewUser(false);
        formValidator.validate(user, result);
        //если проверка не пройдена, возвращаем ошибки и форму клиенту
        if (result.hasErrors()) {
            modelAndView.setViewName("user/edit"); //ссылка на страницу пользователя
            return modelAndView;
        }

        User userFromDb = userService.findByLogin(user.getLogin()); //получаем пользователя с БД и сравниваем пароль
        if (!userFromDb.getPassword().equals(user.getOldPassword())) { //изменения сохраняем только если пользователь правильно ввел свой пароль
            result.addError(new FieldError("user", "oldPassword", "user.error.wrongPassword"));
            modelAndView.setViewName("user/edit");
            return modelAndView;
        }

        if (user.getPassword().isEmpty()) { //если в форме не был указан новый пароль, то пароль не меняем (берем его с БД)
            user.setPassword(userFromDb.getPassword());
        }
        //в этом блоке проводим необходимые преобразования, если необходимо

        //
        userService.update(user);
        modelAndView.setViewName("redirect:/index");
        redirectAttributes.addFlashAttribute("message", "user.message.update");
        return modelAndView;
    }
}
