
<%--Редактирование ЗАДАЧИ--%>

<%--
  Created by IntelliJ IDEA.
  User: Евгений
  Date: 29.12.2015
  Time: 11:01
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<form class="form-horizontal" action="/task/edit" method="post" acceptCharset="UTF-8" id = "form">
  <fieldset>

    <!-- change col-sm-N to reflect how you would like your column spacing (http://getbootstrap.com/css/#forms-control-sizes) -->

    <!-- Form Name -->
    <legend>Редактирование задачи</legend>


    <input type="hidden" name="id" class="form-control" id="id"/>

    <!-- Краткое описание -->
    <div class="form-group">
      <label class="control-label col-sm-2">Описание</label>
      <div class="col-sm-10">
        <input type="text" name="shortDescription" class="form-control" id="shortDescription"/>
      </div>
    </div>

    <!-- Полное описание -->
    <div class="form-group">
      <label class="control-label col-sm-2" for="taskDescription">Полное описание</label>
      <div class="col-sm-10">
        <textarea name="taskDescription" wrap = 'hard' cols="30" rows="5" class="form-control" id="taskDescription"/>
      </div>
    </div>

    <!-- Планировать на -->
    <div class="form-group">
      <label class="control-label col-sm-2">Планировать на</label>
      <div class="col-sm-10">
        <input type="date" name="toDoDate" id = "toDoDate" class="form-control"/>
      </div>
    </div>

    <%--ФЛАГ Уведомить. Тут же поле "Уведомить в"--%>
    <div class="form-group">
      <label class="control-label col-sm-2">Уведомить</label>
      <div class="col-sm-1">
        <input type="checkbox" style="margin-top: 10px" id="notificationNeeded" name="notificationNeeded" onchange="script_task_block_edit.localToggleNotifyDate(null)"/>
      </div>
      <div class="col-sm-9">
        <%--Когда уведомить. добавляем скриптом, если установлен флаг notificationNeeded--%>
        <div id="notify-date">
        </div>
      </div>
    </div>

    <%--ФЛАГ Группа подзадач?? --%>
    <div class="form-group">
      <label class="control-label col-sm-2">Группа подзадач</label>
      <div class="col-sm-10">
        <input type="checkbox" style="margin-top: 10px" id="groupOfTasks" name="groupOfTasks" onchange='script_task_block_edit.localToggleSubTaskBlock(false)'/>
      </div>
    </div>

    <%-- Группа подзадач. добавляем скриптом, если установлен флаг groupOfTasks--%>
    <div id="sub-tasks-block">

    </div>

    <div class="form-group">
      <label class="control-label col-sm-2">Выполнена</label>
      <div class="col-sm-1">
        <input type="checkbox" style="margin-top: 10px" id="completed" name="completed"/>
      </div>
    </div>

    <%--КНОПКИ ВЗАИМОДЕЙСТВИЯ С ФОРМОЙ--%>
    <div class="form-group">

      <label class="control-label col-sm-8"></label>
      <div class="text-right col-sm-4">
        <div id="Group" class="btn-group" role="group" aria-label="">
          <button type="button" name="" class="btn btn-success" aria-label="Создать" onclick="script_task_block_edit.submitFormTask()">Сохранить изменения</button>
          <button type="button" name="" class="btn btn-warning" aria-label="Назад" onclick="script_index.loadTasks()">Назад к задачам</button>
        </div>

      </div>
    </div>

  </fieldset>

</form>


<%--HTML код блока "Уведомить в"--%>
<div id="notifyDateHTML" style="display: none;">
  <div class='form-group' style='margin: 0px'><label class='control-label col-sm-2'>Уведомить в</label>
    <div class='col-sm-10'><input name='notifyDate' id='notifyDate' type='datetime-local' class='form-control'/></div>
  </div>
</div>

<%--HTML код блока "управление подзадачами"--%>
<div id="subTasksBlockHTML" style="display: none">
  <div class='row'>
    <div class='col-md-2 text-right'><input type='button' class='btn btn-info btn-sm' aria-label=''
                                            style='width: 110px; margin: 3px' value='Добавить'
                                            onclick='script_task_block_edit.localAddSubTask(null)'/><input
            type='button' class='btn btn-info btn-sm' aria-label='' style='width: 110px; margin: 3px'
            value='Очистить пустые' onclick='script_task_block_edit.localClearEmpty()'/><input type='button'
                                                                                               class='btn btn-info btn-sm'
                                                                                               aria-label=''
                                                                                               style='width: 110px; margin: 3px'
                                                                                               value='Удалить все'
                                                                                               onclick='script_task_block_edit.localClearAll()'/>
    </div>
    <div class='col-md-10' id='sub-tasks-list'></div>
  </div>
</div>

<%--HTML код поля "подзадача"--%>
<div id="subTaskElementHTML" style="display: none;">
  <div class='row' style='padding-bottom: 5px;'><input type='hidden' name='subTaskList[0].id' value='0'>

    <div class='col-md-1'><input type='checkbox' name='subTaskList[0].completed'></div>
    <div class='col-md-8'><input type='text' class='form-control' name='subTaskList[0].text'
                                 placeholder='Текст подзадачи' value=''></div>
    <div class='col-md-3'><input type='button' class='btn btn-info btn-sm' aria-label=''
                                 style='width: 110px; margin-top: 1px' value='Удалить'
                                 onclick='script_task_block_edit.localDeleteSubTask(this)'/></div>
  </div>
</div>

