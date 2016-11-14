package my.diploma.project.web.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Евгений on 19.11.2015.
 */
@WebFilter(filterName = "AuthFilter") //фильтр инициализирован в web.xml
public class AuthentificationFilter implements Filter {
    private String[] publicLinks = {"/resources/public/", "/resources/bootstrap/"}; //исключенные из фильтра ресурсы
    private String[] publicServlets = {"/login", "/register"};

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        RequestDispatcher requestDispatcher;
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        String servletPath = httpServletRequest.getServletPath();

        for (String link : publicLinks){ //проверка, разрешен ли неавторизованный доступ к ресурсу
            if (servletPath.startsWith(link)){
                requestDispatcher = request.getRequestDispatcher(servletPath);
                requestDispatcher.forward(request, response);
                return; //если префикс есть в исключениях, разрешаем запрос и выходим из фильтра
            }
        }

        if (httpServletRequest.getSession().getAttribute("auth") != null) {
            //если в сессии есть запись с логином пользователя, значит, он уже авторизован
            for (String link : publicServlets) { //если попытка обратиться к логину или регистрации уже авторизованного пользователя
                if (servletPath.equalsIgnoreCase(link)) { //если да,
                    request.getRequestDispatcher("/index").forward(request, response); //переадресация на index
                    return;
                }
            }
            request.getRequestDispatcher(servletPath).forward(request, response);
            return;
        } else {
            boolean inList = false; //путь сервлета в списке разрешенных
            for (String link : publicServlets) { //если не авторизован, проверяем, к какому сервлету хочет обратиться пользователь
                inList = inList | servletPath.equalsIgnoreCase(link); //меняем флаг на true
            }
            if (!inList) { //если не в списке, redirect на login
                if (servletPath.equals("/index") || servletPath.equals("/")){
                    request.getRequestDispatcher("/login").forward(request, response);
                } else {
                    httpServletResponse.sendError(403, "Not authorised");
                }
            } else {
                request.getRequestDispatcher(servletPath).forward(request, response);
            }
        }
    }

    @Override
    public void destroy() {

    }
}
