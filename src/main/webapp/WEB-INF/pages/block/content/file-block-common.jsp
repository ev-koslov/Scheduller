<%--БЛОК СПИСКА ФАЙЛОВ--%>

<%--
  Created by IntelliJ IDEA.
  User: Евгений
  Date: 28.12.2015
  Time: 11:09
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<div id="block-files-inner" style="display: flex; flex-flow: wrap">
<ul id="listFiles">

</ul>
  <%----%>
  <div id="dropZone">ПЕРЕТАЩИТЕ ФАЙЛЫ СЮДА</div>

  <%----%>

</div>


<%----%>

<script>
  $(document).ready(function() {
    var dropZone = $('#dropZone'),
            maxFileSize = 100000000; // максимальный размер файла - 1 мб.
    var list = $("#listFiles");
    if (typeof(window.FileReader) == 'undefined') {
      dropZone.text('Не поддерживается браузером!');
      dropZone.addClass('error');
    }

    dropZone[0].ondragover = function() {
      dropZone.addClass('hover');
      return false;
    };

    dropZone[0].ondragleave = function() {
      dropZone.removeClass('hover');
      return false;
    };

    dropZone[0].ondrop = function(event) {
      event.preventDefault();
      dropZone.removeClass('hover');
      dropZone.addClass('drop');

      var files = new Array();
      var size = 0;
      $.each(event.dataTransfer.files, function(i, file){
        files.push(file);
        size += file.size;
      });

      if (size > maxFileSize) {
        dropZone.text('Файл слишком большой!');
        dropZone.addClass('error');
        return false;
      }
      $.each(files, function(i, file){
        $(list).append('<li>'+file.name+' '+file.size+'</li>');
      });
    };


  });

</script>

<style>
  #dropZone {
    color: #555;
    font-size: 18px;
    text-align: center;

    width: 400px;
    padding: 50px 0;
    margin: 50px auto;

    background: #eee;
    border: 1px solid #ccc;

    -webkit-border-radius: 5px;
    -moz-border-radius: 5px;
    border-radius: 5px;
  }

  #dropZone.hover {
    background: #ddd;
    border-color: #aaa;
  }

  #dropZone.error {
    background: #faa;
    border-color: #f00;
  }

  #dropZone.drop {
    background: #afa;
    border-color: #0f0;
  }
</style>

<%----%>

<%--Код елемента отображения файлов--%>
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