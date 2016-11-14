<%--
  Created by IntelliJ IDEA.
  User: Евгений
  Date: 28.12.2015
  Time: 10:04
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">


    <%--ЗАПРЕТ КЕШИРОВАНИЯ СКРИПТОВ--%>

    <meta http-equiv="Cache-control" content="no-cache">
    <meta http-equiv="Pragma" content="no-cache">
    <meta http-equiv="Expires" content="-1">

    <%----%>
    <title>@lfa</title>

    <link href="/resources/public/favicon.ico" rel="icon">

    <link href="/resources/bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <%--<link href="/resources/css/my-bootstrap.css" rel="stylesheet">--%>
    <link href="/resources/css/my-style.css" rel="stylesheet">

    <%--Общие скрипты BOOTSTRAP--%>

    <script type="text/javascript" src="/resources/bootstrap/js/jquery.min.js"></script>
    <script type="text/javascript" src="/resources/bootstrap/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="/resources/bootstrap/js/scripts.js"></script>

    <%----%>

    <%--СКРИПТЫ БЛОКОВ и ГЛАВНОЙ СТРАНИЦЫ--%>

    <script type="text/javascript" src="/resources/js/script_index.js"></script>

    <script type="text/javascript" src="/resources/js/block/script_task-block-common.js"></script>

    <script type="text/javascript" src="/resources/js/block/script_task-block-edit.js"></script>
    <%----%>

    <%--ДИНАМИЧЕСКИЕ СКРИПТЫ MAIN_BLOCK--%>

    <%----%>

</head>
<body>

<div class="container-fluid">
    <!--ВЕРХНЕЕ МЕНЮ-->
    <div id="menu-main">

    </div>


    <!--НИЖНЕЕ МЕНЮ (ИНСТРУМЕНТЫ)-->
    <!---------------------------------------------------------------->

    <div id="menu-controls">

    </div>

    <!---------------------------------------------------------------->

    <!--ОСНОВНОЙ БЛОК-->
    <br><br><br>

    <!--КОНТЕНТ-->
    <div class="row">
        <div class="col-md-1 hidden-sm hidden-xs"></div>
        <div class="col-md-10" id='block-content'>

        </div>
        <div class="col-md-1 hidden-sm hidden-xs"></div>
    </div>


    <br><br>

</div>

<div id="modal-window" class="my-modal-window my-hidden" onclick="$(this).addClass('my-hidden')">
    <div id =  "modal-header"></div>
    <div id =  "modal-data"></div>
    <div id =  "modal-footer"></div>
</div>

<%--При загрузке страницы выполняем базовое заполнение блоков--%>
<script type="text/javascript">
    $(document).ready(function () {
        script_index.initIndex();
    });
</script>

</body>
</html>
