/**
 * Created by Евгений on 11.01.2016.
 */

//БЛОК УПРАВЛЕНИЯ ЗАДАЧАМИ

var script_task_block_common = {

    //ПЕРЕМЕННЫЕ
    variables: {

        initialized: false,

        autoUpdateTimer: null,

        //елементы, которые показаны на странице
        shownElements: null,
    },

    //-------------------------------------------------------

    //Инициализация переменных для блока TaskBlockCommon
    initTaskBlockCommon: function () {
        //при инициализации переменных смотрим флаг "ИНИЦИАЛИЗИРОВАН"
        //если он true, сначала запускаем деструктор, а потом инициализацию
        //таким образом избегаем наложения данных (многократной инициализации)
        if (!script_task_block_common.variables.initialized) {
            script_task_block_common.variables.shownElements = new Array(); //создание обьекта массива елементов
            script_task_block_common.performFilters(); //выполнение заполнения поля
            //установка таймера на проверку обновлений
            script_task_block_common.variables.autoUpdateTimer = setInterval(function () {
                script_task_block_common.performFilters()
            }, 20000);
            //установка флага "инициализирован"
            script_task_block_common.variables.initialized = true;
        } else {
            script_task_block_common.destroyTaskBlockCommon();
            script_task_block_common.initTaskBlockCommon();
        }
    },


//-----------------------------------------------------------


//Уничтожение (null) переменных для блока TaskBlockCommon
    destroyTaskBlockCommon: function () {
        script_task_block_common.variables.shownElements = null; //обнуление массива показанных елементов
        clearInterval(script_task_block_common.variables.autoUpdateTimer); //сброс интервала проверки обновлений

        //сброс флага "инициализирован"
        script_task_block_common.variables.initialized = false;
    },

//-----------------------------------------------------------

    //грузим в рабочее окно форму создания новой задачи
    createNewTask: function () {
        script_task_block_common.destroyTaskBlockCommon(); //деструктор главного окна задач (обнуляет переменные)
        script_index.loadBlock("/get&type=content&block=task-block-create", "#block-content", script_task_block_edit.initPageTask);
    },

    //открытие задачи на редактирование
//отдаем POST запрос
//принимаем HTML код и JSON с задачей. Заполняем скриптом
    openTask: function (array, targetBlock) {
        var request = "";
        for (var key in array) {
            request += key + "=" + array[key] + "&";
        }
        $.ajax({ //отправляем через AJAX
            url: "/task/open", //адрес сервлета
            type: "post", //метод запроса
            data: request, //сериализированные данные формы
        }).done(function (response, status) {
            if (status == "success") { //если запрос успешен
                //записываем в блок HTML код (он приходит в ответе вместе с обьектом задачи)
                if (response.task != null) {
                    $(targetBlock).html(response.html);

                    //передаем обьект задачи для заполнения полей
                    //инициализируем сткрипты на странице
                    script_task_block_edit.initPageTask(response.task);

                    //запускаем деструктор
                    script_task_block_common.destroyTaskBlockCommon();
                } else {
                    alert("NO TASK IN RESPONSE");
                }

            } else {
                alert("REQUEST_ERROR");
            }

        });
    },


//-----------------------------------------------------------
//Метод, отрабатывающий обновление задач
//Считывает состояние всех фильтров, на основе фильтров из глобальной переменной выбирает подходящие значения
//и формирует массив filteredTasks - задачи, которые подошли по текущим критериям
//После, просматривает массив выведенных елементов (shownElements) и смотрит, какие надо обновить/удалить/добавить

    performFilters: function () {
        //Задачи, которые необходимо вывести на экран
        var filteredTasks = new Array(); //обнуляем переменную filteredTasks
        //опц - занимаем mainTasksMap

        var filters = $("[id^='filter_']"); //получаем все обьекты фильтра
        $.each(filters, function (element_index, element) { //проходим по каждому и выставляем атрибут readonly, чтобы не мешал скрипту
            $(element).prop("readonly", true);
        });

        //перебираем все задачи в глобальной переменной и для каждой применяем фильтры, значения у которых заполнены
        $.each(script_index.variables.mainTasksMap.tasks, function (task_index, task) {
            var accept = true; //устанавливаем признак, что задача прошла фильтр на true

            $.each(filters, function (element_index, filter) { //проходим по всем фильтрам

                //если значение поля фильтра задано и его длина больше 0, передаем управление фильтру
                if ($(filter).prop("value") != undefined && $(filter).prop("value").length > 0) {

                    if (!script_task_block_common.taskFilters[$(filter).prop("id")]($(filter).prop("value"), task)) { //если фильтр вернул false
                        accept = false; //то ставим задаче признак false (не подходит),
                        return false; // переходим к след итерации
                    }

                }
            });

            //если задача подходит
            if (accept) {
                filteredTasks.push(task); //добавляем ее в массив для вывода пользователю
            }
        });

        //на выходе получаем массив задач, которые прошли фильтр
        //передаем данный массив методу, отвечающему за отображение

        this.showTasks(filteredTasks);
        $.each(filters, function (element_index, element) { //после выполнения скрипта снимаем атрибут readonly
            $(element).prop("readonly", false);
        });
    },

//---------------------------------------------------------------------------------

    //Вывод уже готового списка задач на страницу
    showTasks: function (arrayToRender) {
        //очищаем содержимое поля и массив показанных елементов
        $("#block-tasks-inner").html("");

        script_task_block_common.variables.shownElements.length = 0;

        //проходим по всему сформированному с учетом фильтров и сортировки списку задач
        $.each(arrayToRender, function (number, task) {
            var elementHTML = $("#taskElement").html(); //получаем код елемента для отображения задачи
            var element = $(elementHTML); //на основе кода елемента создаем DOM елемент
            //Вешаем слушатель на Клик. При клике открываем задачу
            $(element).on('click', function () {
                script_task_block_common.openTask({"id": task.id}, "#block-content");
            });
            //Заполняем поля внутри DOM елемента
            $(element).find("[data-field='shortDescription']").append(task.shortDescription);
            $(element).find("[data-field='toDoDate']").append($('<span class="glyphicon glyphicon-calendar"></span>'));
            $(element).find("[data-field='toDoDate']").append(new Date(task.toDoDate).toLocaleDateString());
            if (task.notificationNeeded == true) {
                $(element).find("[data-field='icons']").append($('<span class="glyphicon glyphicon-bell"></span>'));
            }
            if (task.groupOfTasks == true) {
                $(element).find("[data-field='icons']").append($('<span class="glyphicon glyphicon-list-alt"></span>'));
            }
            if ($(element).find("[data-field='icons']").children().length > 0){
                $(element).find("[data-field='toDoDate']").css("border-right", "3px solid darkgray");
            }
            //
            //закрепляем сформированный, но пока еще скрытый елемент на странице
            $("#block-tasks-inner").append(element);
            //отображаем елемент с зажержкой относительно предыдущего
            //для каждого елемента создаем задержку запуска анимации == позиции в массиве * 50 мс
            setTimeout(function () {
                $(element).slideDown(500);
            }, number * 30);
            script_task_block_common.variables.shownElements.push(element);
        });
    },


    //Представляем фильтры как массив функций.
//Обьявлять функции в контексте window - правило дурного тона :)
    taskFilters: {

        //Фильтр текстовый
        filter_task_text_content: function (keyword, task) {
            if (keyword.length == 0) { //если блина запроса меньше 3 символов, фильтр выходит давая положительный результат
                return true;
            }
            //если искомый текст есть в кратком или полном описании, выходим с результатом true
            if (task.shortDescription.toLowerCase().indexOf(keyword.toLowerCase()) != -1 || task.taskDescription.toLowerCase().indexOf(keyword.toLowerCase()) != -1) {
                return true;
            }
            //если в задаче есть список подзадач, то пробегаем и но текстовым полям подзадач
            if (task.groupOfTasks == true) {
                $.each(task.subTaskList, function (index, subTask) { //для каждой подзадачи
                    if (subTask.text.toLowerCase().indexOf(keyword.toLowerCase())) { //если текст подзадачи содержит искомое, выходим с true
                        return true;
                    }
                });
            }
            //если нигде не найдено, возвращаем false
            return false;
        },

        //--------------------------------------------------------------------------------

        //Фильтр по дате исполнения
        filter_task_to_do_date: function (to_do_date, task) {
            var toDoDateMillis = new Date(to_do_date).getTime();
            if (task.toDoDate == toDoDateMillis) {
                return true;
            } else {
                return false;
            }
        }

    },


    //----------------------------------------------------------------------

};




