
<%--МЕНЮ УПРАВЛЕНИЯ ЗАДАЧАМИ--%>

<%--
  Created by IntelliJ IDEA.
  User: Евгений
  Date: 28.12.2015
  Time: 10:02
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>


<div class="row">
  <div class="col-md-12">
    <nav class="navbar navbar-default navbar-fixed-bottom" role="navigation">
      <div class="navbar-header">

        <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#bs-example-navbar-collapse-bottom">
          <span class="sr-only">Toggle navigation</span><span class="icon-bar"></span><span class="icon-bar"></span><span class="icon-bar"></span>
        </button> <a class="navbar-brand" href="#">Действия: задачи</a>
      </div>

      <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-bottom">

        <!--Создать задачу-->
        <form class="navbar-form navbar-left">
          <div class="form-group">
            <input type="button" class="btn btn-default" value="Создать задачу" onclick="script_task_block_common.createNewTask()">
          </div>
        </form>

        <!--ФИЛЬТР-->
        <form class="navbar-form navbar-left">
          <div class="input-group">
            <span class="input-group-addon">Фильтр по содержанию</span>
            <input class="form-control" placeholder="" id="filter_task_text_content" type="text" onkeyup="script_task_block_common.performFilters()">
          </div>
        </form>

        <!--ОТБОР ПО ДАТЕ-->
        <form class="navbar-form navbar-left">
          <input type="date" class="form-control" id="filter_task_to_do_date" value="" onblur="script_task_block_common.performFilters()"/>
        </form>

      </div>
    </nav>
  </div>
</div>
