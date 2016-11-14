
<%--ГЛАВНОЕ МЕНЮ--%>

<%--
  Created by IntelliJ IDEA.
  User: Евгений
  Date: 28.12.2015
  Time: 10:01
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<div class="row">
  <div class="col-md-12">
    <nav class="navbar navbar-default navbar-fixed-top" role="navigation">
      <div class="navbar-header">
        <button type="button" class="navbar-toggle" data-toggle="collapse"
                data-target="#bs-example-navbar-collapse-top">
          <span class="sr-only">Toggle navigation</span><span class="icon-bar"></span><span
                class="icon-bar"></span><span class="icon-bar"></span>
        </button>
        <a class="navbar-brand" href="#">@lfa 1.0</a>
      </div>

      <!--Контейнер меню навигации. Вставлять внутрь этого DIV-->
      <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-top">

        <!--Кнопки управления кабинетом-->
        <ul class="nav navbar-nav navbar-left" id = 'index-nav-block'>
          <li>
            <a onclick="script_index.loadTasks()" id="index-nav-block-tasks">Задачи</a>
          </li>
        </ul>


        <!--Кнопки справа на панели навигации-->

        <ul class="nav navbar-nav navbar-right">
          <li>
            <a href="#">Мой профиль</a>
          </li>
          <li>
            <a href="/logoff">Выход</a>
          </li>
          <li class="hidden-xs hidden-sm">
            &nbsp;&nbsp;&nbsp;
          </li>
        </ul>
      </div>

    </nav>
  </div>
</div>
