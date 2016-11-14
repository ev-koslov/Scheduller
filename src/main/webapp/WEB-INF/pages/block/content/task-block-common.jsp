<%--БЛОК СПИСКА ЗАДАЧ--%>

<%--
  Created by IntelliJ IDEA.
  User: Евгений
  Date: 28.12.2015
  Time: 11:09
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<div id="block-tasks-inner" style="display: flex; flex-flow: wrap">

</div>

<%--Код елемента отображения задачи--%>
<div id="taskElement" style="display: none;">
  <div class='my-task-block'>
    <div style='display: flex; flex-direction: column'>
      <div data-field='shortDescription' style="border-bottom: 3px solid darkgray; display: inline-flex; padding: 4px"></div>
      <div style='display: flex; flex-direction: row;'>
        <div style="display: inline-flex"></div>
        <div data-field='toDoDate' style="display: inline-flex; padding: 4px"></div>
        <div data-field='icons' style="display: inline-flex; padding: 4px"></div>
      </div>
    </div>
  </div>
</div>
</div>