/**
 * Created by Евгений on 11.01.2016.
 */

//БЛОК РЕДАКТИРОВАНИЯ ЗАДАЧИ

var script_task_block_edit = {

    variables: {

        initialized: false,

//ПЕРЕМЕННЫЕ ДЛЯ СТРАНИЦЫ "ИЗМЕНЕНИЕ ЗАДАЧИ"
        detachedSubTasks: null, //отсоединенный обьект ГруппаПодзадач

    },

//инициализцая скриптов страницы для изменения задачи
    initPageTask: function (task) {
        //очистка блока меню (снизу). В задачах оно не нужно
        $("#menu-controls").empty();
        script_task_block_edit.variables.detachedSubTasks = null;
        if (task != null) {
            script_task_block_edit.fillTaskFromJson(task);
        }
    },


//заполнение полей, которые пришли в JSON
    fillTaskFromJson: function (task) {
        var inputElement;
        var inputElementType;

        //если у задачи установлен флаг "Нужно уведомление", ставим в форме флаг и проставляем время уведомл.
        if (task.notificationNeeded == true) {
            $("#form").find("[name='notificationNeeded']").prop("checked", true);
            script_task_block_edit.localToggleNotifyDate(task.notifyDate);
        }

        //если у задачи установлен флаг "Группа подзадач", ставим в форме флаг и перебираем все подзадачи
        if (task.groupOfTasks == true) {
            //ставим флаг
            $("#form").find("[name='groupOfTasks']").prop("checked", true);
            //проверяем, не пустой ли список подзадач?
            var isSubTaskListNotEmpty = (task.subTaskList != null) && (task.subTaskList.length > 0);

            //отображаем кнопки подзадач
            script_task_block_edit.localToggleSubTaskBlock(isSubTaskListNotEmpty);

            //если список не пустой, отображаем подазадчи
            if (isSubTaskListNotEmpty) {

                //перебираем подзадачи
                $.each(task.subTaskList, function (i, subTask) {
                    //для каждой вызываем функцию создания подзадачи, передавая параметром саму подзадачу
                    script_task_block_edit.localAddSubTask(subTask);
                });
            }
        }


        for (var fieldName in task) {
            inputElement = $("#form").find("[name='" + fieldName + "']");
            inputElementType = $(inputElement).prop("type");
            if (inputElementType == 'checkbox') {
                $(inputElement).prop("checked", task[fieldName]);
                continue;
            }

            if (inputElementType == "textarea") {
                $(inputElement).append(task[fieldName]);
                continue;
            }

            if (inputElementType == "date") {
                $(inputElement).prop("value", new Date(task[fieldName]).toISOString().substring(0, 10));
                continue;
            }

            if (inputElementType == "text") {
                $(inputElement).prop("value", task[fieldName]);
                continue;
            }

            if (inputElementType == "hidden") {
                $(inputElement).prop("value", task[fieldName]);
                continue;
            }

        }
    },


//ОБРАБОТКА ЕЛЕМЕНТОВ ФОРМЫ ------------------------------------------


//отображение/скрытие блока "Уведомить в"
//Если при пометке чекбокса переменная с отсоединенным блоком  detachedNotifyDate == null, парсим заданный HTML код (notifyDateHTML)
//и добавляем на страницу
//при снятии чекбокса, детачим всех детей выбранного ID и пишем в переменную detachedNotifyDate (сохраняем введенные данные)
    localToggleNotifyDate: function (dateTime) {

        var controlsID = "#notificationNeeded";
        var targetID = "#notify-date";

        $(controlsID).prop("readonly", true); //блокируем чекбокс.
        if ($(controlsID).prop("checked")) {
            var notifyDateHTML = $("#notifyDateHTML").html();
            var notifyDate = $(notifyDateHTML);
            var notifyDateValue;
            if (dateTime != null) { //если в в метод пришло число, то заполняем поле
                notifyDateValue = new Date(dateTime);
                notifyDateValue.setTime(notifyDateValue.getTime()+(-1)*notifyDateValue.getTimezoneOffset()*1000*60);
            } else {
                notifyDateValue = new Date();
                notifyDateValue.setTime(notifyDateValue.getTime()+60000+(-1)*notifyDateValue.getTimezoneOffset()*1000*60);
            }
            $(notifyDate).find("#notifyDate").prop("value", notifyDateValue.toISOString().substring(0, 16));
            $(targetID).append(notifyDate);
        } else {
            $(targetID).children().remove();
        }
        $(controlsID).prop("readonly", false); //снимаем блокировку чекбокса
    },


//отображение/скрытие блока "Подзадачи"
//Если при пометке чекбокса переменная с отсоединенным блоком  detachedSubTasks == null, парсим заданный HTML код (subTasksBlockHTML)
//и добавляем на страницу
//если после добавления блока "Подзадачи" в DIV #sub-tasks-list 0 полей подзадач, то парсим HTML переменной subTaskElementHTML
//и добавляем пустое поле "подазадча"
//при снятии чекбокса, детачим всех детей выбранного ID и пишем в переменную detachedSubTasks (тем самым сохраняем все введенные данные)
    localToggleSubTaskBlock: function (hasTasksToFill) {

        var controlsID = "#groupOfTasks";
        var targetID = "#sub-tasks-block";

        $(controlsID).prop("readonly", true);
        if ($(controlsID).prop("checked")) {
            if (script_task_block_edit.variables.detachedSubTasks == null) {
                var subTasksBlockHTML = $("#subTasksBlockHTML").html();
                var subTasksBlock = $(subTasksBlockHTML);
                $(targetID).append(subTasksBlock);
                //Если в блоке подзадач нет ни одной задачи (создается пустой блок), то дабавляем одну пустую подзадачу для ввода
                if (hasTasksToFill == false) {
                    //добавляем пустую подзадачу
                    script_task_block_edit.localAddSubTask(null);
                }
            } else {
                $(targetID).append($(script_task_block_edit.variables.detachedSubTasks));
            }

        } else {
            script_task_block_edit.variables.detachedSubTasks = $(targetID).children().detach();
        }
        $(controlsID).prop("readonly", false);
    },


//добавление подзадачи в начало списка
    localAddSubTask: function (subTask) {
        //целевой блок, куда вставлять подзадачу
        var targetBlock = "#sub-tasks-list";

        //берем с глобальной переменной detachedSubTaskElement вненший (outerHTML) HTML код 0 элемента,
        //парсим его и превращаем в локальный DOM обьект
        //Выполняем вставку локального DOM обьекта перед всеми существующими елементами
        var subTaskElementHTML = $("#subTaskElementHTML").html();
        var subTaskElement = $(subTaskElementHTML);

        for (var fieldName in subTask) {
            var inputField = $(subTaskElement).find("[name$='" + fieldName + "']");
            if (inputField.length > 0) {
                if ($(inputField).prop("type") == "checkbox") {
                    $(inputField).prop("checked", subTask[fieldName]);
                    continue;
                }
                $(inputField).prop("value", subTask[fieldName]);
            }
        }
        $(targetBlock).append(subTaskElement);

        script_task_block_edit.fixNumbers(); //исправляем нумерацию в ID и name, чтобы правильноданные принялись на сервере
    },


//удаление обьекта "Подзадача". В функцию принимается обьект кнопки, вызвавшей событие
    localDeleteSubTask: function (subTaskFieldToDelete) {

        var targetBlock = "#sub-tasks-list";

        //почему обращение идет через 2 родителя - см. в описании поля "Подзадача"
        //удаляем поле "Подзадача"
        $(subTaskFieldToDelete).parent().parent().remove();
        //если после удаления подзадачи, в списке не осталось полей для ввода, создаем пустое поле
        if ($(targetBlock).find("div.row").length == 0) {
            script_task_block_edit.localAddSubTask(null);
        }
        script_task_block_edit.fixNumbers(); //исправляем нумерацию в ID и name, чтобы правильноданные принялись на сервере
    },


//удаление всех пустых подзадач
    localClearEmpty: function () {
        var targetBlock = "#sub-tasks-list";
        var subTasksArray = $(targetBlock).find("div.row");
        $.each(subTasksArray, function (index, subTask) {
            $.each($(subTask).find("*"), function (i, field) {
                //если текстовое поле пустое, удаляем эту подзадачу
                if (field.name != undefined && field.name.indexOf("].text") >= 0 && field.value.length < 1) {
                    subTask.remove();
                }
            });
        });
        if ($(targetBlock).find("div.row").length == 0) {
            //если после удаления пустых подзадач, в списке не осталось полей для ввода, создаем пустое поле
            script_task_block_edit.localAddSubTask(null);
        }
        script_task_block_edit.fixNumbers(); //исправляем нумерацию в ID и name, чтобы правильноданные принялись на сервере
    },


//удаление всех подзадач и добавление одной пустой для продолжения ввода
    localClearAll: function () {
        var targetBlock = "#sub-tasks-list";
        //выполняем удаление всех подзадач методом получения всех детей блока и их удаления
        $(targetBlock).children().remove();
        //после удаления всех, добавляем пустое поле первой подзадачи (0), чтобы пользователь мог продолжить ввод данных
        script_task_block_edit.localAddSubTask(null);
        script_task_block_edit.fixNumbers(targetBlock); //исправляем нумерацию в ID и name, чтобы правильно данные принялись на сервере
    },


//исправление номерации полей в блоке подзадач.
//необходимо, чтобы елементы были приняты по порядку на сервере
    fixNumbers: function () {
        var targetBlock = "#sub-tasks-list";
        var nameMask = 'subTaskList'; //выставляем маску, по которой необходимо
        var pattern = /\[[0-9]*\]/; //шаблон для замены номера имени (регулярное выражение)
        var subTasksArray = $(targetBlock).find("div.row"); //получаем массив блоков подзадач (из targetBlock выбираем все div с классом row)

//    Подзадача выглядит так:
//  <div class="row" style="padding-bottom: 5px">
//      ТУТ СОДЕРЖИМОЕ ПОЛЯ "ПОДЗАДАЧА"
//  </div>

        //перебираем все полученные блоки "ПОДЗАДАЧА"
        for (var i = 0; i < subTasksArray.length; i++) {
            //в каждом блоке получаем как массив все елементы с tag <input> и атрибутом name, которое начинается на значение переменной nameMask
            var subTaskInputElements = $(subTasksArray[i]).find("input[name^='" + nameMask + "']");

            //Перебираем все элементы полученного массива.
            $.each(subTaskInputElements, function (index, element) {
                //Изменяем аттрибут name для того, чтобы нумерация все елементов была по порядку (0-1-2-...-n)
                element.name = element.name.replace(pattern, '[' + i + ']');
            });
        }
    },


//отправка формы и обработка результатов
//принимаем Json смотрим статус.
//Дописать обработку ошибок ввода (сделать, чтобы CSS аттрибуты каждого ошибочного обьекта хранились отдельно, как ел. массива)
    submitFormTask: function () {
        var serializedFormArray = $("#form").find("*").serializeArray(); //сериализируем форму в массив
        var requestBody = ''; //строка ответа

        $.each(serializedFormArray, function (index, field) { //проходим по всем елементам массива
            //если имя поля содержит date, значит, внутри дата. Парсим ее в обьект Date и получаем время в мс
            if (field.name.toLowerCase().indexOf('date') >= 0 && field.value.length > 0) {
                var dateToLong;
                //если тип поля == datetime-local, то преобразовуем его значение в millis UTC
                //поскольку datetime-local возвращает локальное время, а не UTC (частный случай)

                if (field.name == 'notifyDate') {
                    var date = new Date(field.value);
                    dateToLong = date.getTime() + date.getTimezoneOffset()*60*1000;
                } else {
                    dateToLong = new Date(field.value).getTime();
                }

                requestBody = requestBody + field.name + "=" + dateToLong + "&"; //добавляем пару атрибут=значение к строке запроса
                return;

            } else {
                requestBody = requestBody + field.name + "=" + field.value + "&";
            }
        });
        requestBody = requestBody.slice(0, requestBody.length - 1); //отсекаем последний символ в строке (он всегда &)

        //Формируем запрос и отправляем данные
        $.ajax({ //отправляем через AJAX
            url: $("#form").prop("action"), //адрес сервлета
            type: $("#form").prop("method"), //метод запроса
            data: requestBody, //сериализированные данные формы
        }).done(function (response, status) { //callback функция. Выполняем обработку пришедшего JSON

            if (status == "success") { //если запрос віполнен успешно
                if (response.allok == "true") { //проверяем, успешен ли запрос и создание задачи

                    //запускаем принудительное обновление задач и в callback передаем функцию загрузки представления задач
                    script_index.updateTasksListFromJSON(script_index.loadTasks);

                } else { //если переменная allok == false, значит, при обработке формы произошли ошибки
                    if (response.errors.length > 0) { //если количество ошибок больше 0

                        ////функция, выводящая ошибки (НАПИСАТЬ)
                        //script_task_block_edit.showErrors();

                        var field; //временная переменная, куда записываем обьект поля, в котором ошибка
                        var oldBorder; //аттрибут style, который ИМЕЛО ошибочное поля до обработки формы (СДЕЛАТЬ МАССИВОМ, чтобы было для каждого елемента)
                        for (var i = 0; i < response.errors.length; i++) { //проходимся по каждой ошибке
                            field = $("[name='" + response.errors[i].field + "']"); //получаем обьект по имени поля, по которому пришла ошибка
                            oldBorder = $(field).css("border"); //считываем с поля прошлые настройки CSS
                            $(field).css("border", "2px solid red"); //делаем красную рамку
                            $(field).on("focus", function () { //вешаем событие "при фокусе на обьекте", чтобы красная рамка снималась при фокусе
                                //в слушателе событий вместо field надо ставить this
                                // поскольку при срабатывании ивента в него надо передать обьект,
                                // на котором он сработа, а это this
                                $(this).css("border", oldBorder); //при событии возвращаем все на свои места
                            });
                        }
                    } else {
                        alert("EMPTY ERROR MODEL");
                    }
                }
            } else {
                alert("SERVER ERROR");
            }

        });
    },

};


//--------------------------------------------------------------------


