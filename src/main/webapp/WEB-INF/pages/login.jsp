<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%--
  Created by IntelliJ IDEA.
  User: Евгений
  Date: 28.12.2015
  Time: 9:16
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">

  <title>Логин</title>

  <link href="/resources/bootstrap/css/bootstrap.min.css" rel="stylesheet">
  <link href="/resources/bootstrap/css/style.css" rel="stylesheet">

  <script src="/resources/bootstrap/js/jquery.min.js"></script>
  <script src="/resources/bootstrap/js/bootstrap.min.js"></script>
  <script src="/resources/bootstrap/js/scripts.js"></script>

</head>
<body>

<div class="container-fluid">
  <!--ВЕРХНЕЕ МЕНЮ-->
  <div id="main-menu">
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

          <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-top">
            <ul class="nav navbar-nav navbar-right">
              <li style="padding-right: 50px">
                <a href="/register">Регистрация</a>
              </li>
              <li class="hidden-xs hidden-sm">
                &nbsp;&nbsp;&nbsp;
              </li>
            </ul>
          </div>

        </nav>
      </div>
    </div>
  </div>

  <!--ОСНОВНОЙ БЛОК-->
  <div class="row">
    <div class="hidden-xs hidden-sm col-md-3">
    </div>
    <!--ЦЕНТРАЛЬНЫЙ БЛОК В ГЛАВНОМ ОКНЕ. В нем форма регистрации. Ширина 8-->
    <div class="col-md-6">
      <br><br><br>
      <form:form modelAttribute="loginForm" action="/login" method="post" id="form">
        <fieldset>
          <!-- ЗАГОЛОВОК ФОРМЫ -->
          <legend>Авторизация</legend>

          <!-- ЛОГИН -->
          <div class="form-group">
            <div class="input-group">
              <form:input path="login" class="form-control"/>
              <span class="input-group-addon">Логин</span>
            </div>
          </div>

          <!-- Пароль -->
          <div class="form-group">
            <div class="input-group">
              <form:password path="password" class="form-control"/>
              <span class="input-group-addon">Пароль</span>
            </div>
          </div>

          <div class="row">
            <div class="col-md-6">
              <form:errors path="login" cssStyle="color: red"/>
              <form:errors path="password" cssStyle="color: red"/>
            </div>
          </div>

          <!-- Кнопки -->

          <div class="form-group">
            <div class="text-right">
              <div id="Group" class="btn-group" role="group" aria-label="">
                <input type="submit" name="" class="btn btn-success" aria-label="Логин" value="Логин">
                <input type="reset" name="" class="btn btn-danger" aria-label="Сброс" value="Сброс">
              </div>
            </div>
          </div>

        </fieldset>
      </form:form>


    </div>
    <div class="hidden-xs hidden-sm col-md-3">
    </div>
  </div>
</div>

</body>

</html>
