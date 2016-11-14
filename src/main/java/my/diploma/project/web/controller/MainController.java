package my.diploma.project.web.controller;

import my.diploma.project.entity.Task;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * контроллер главной страницы
 *
 * @author Евгений Козлов
 */

@Controller
public class MainController {

    @Transactional(readOnly = true)
    @RequestMapping(value = {"/", "/index"}, method = RequestMethod.GET) //главная страница
    public ModelAndView mainPage() {
        return new ModelAndView("index");
    }

    //отдаем блок по запросу скрипта (главную страницу грузим поблочно).
    @RequestMapping(value = "/get&type={type}&block={blockName}", method = RequestMethod.GET)
    public ModelAndView getBlock(@PathVariable("type") String type, @PathVariable("blockName") String blockName, HttpServletRequest request){
        ModelAndView modelAndView = new ModelAndView("block/"+type+"/"+blockName);
        modelAndView.addObject("task", new Task());
        return modelAndView;
    }



    //страница ошибки
    @RequestMapping(value = "/error", method = RequestMethod.GET)
    public ModelAndView errorPage(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("error");
        return modelAndView;
    }
}
