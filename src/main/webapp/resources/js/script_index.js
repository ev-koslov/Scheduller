/**
 * Created by Евгений on 11.01.2016.
 */

//скрипты, которые могут использоваться всеми блоками или должны работать в фоновом режиме
var script_index = {

    variables: {
        updateTasksFromJSONTimer : null,

        checkForNotificationTimer: null,

        //все задачи пользователя (обновляем в фоне скриптом.)
        mainTasksMap: {
            lastUpdateTime: 0,
            tasks: {}
        },

        //переменные для уведомлений
        notification: {
            //время последнего опроса
            lastCheck: 0,

            //пропущенные уведомления
            missedNotifications: {},

            //таймеры уведомлений
            notificationTimers: {},

            //код блока всплывающего уведомления
            notificationElementHTML: "",

        },
    },

    //-----------------------------


    //общие скрипты
    common: {

    },

    //скрипты касаются задач
    tasks: {

    },

    //скрипты касаются файлов
    files: {

    },

    //обработчики, которые работают в фоновом режиме
    daemons: {

    },

//СКРИПТЫ для INDEX.jsp

    //загрузка блока HTML кода и вставка его по указанному ID.
    //принимает callback функцию, которую исполняет после загрузки блока
    loadBlock: function (url, targetSelector, callbackFunction) {
        try {
            $.get(url, function (data, status) {
                $(targetSelector).empty(); //очистка содержимого блока перед вставкой новых данных
                $(targetSelector).html(data);
                if (callbackFunction != null){
                    callbackFunction();
                }
            });
        } catch (e) {
            alert(e);
        }

    },


    //инициализация скрипта главной страницы
    initIndex: function () {
        //загружаем главное меню
        script_index.loadBlock("/get&type=menu&block=menu-main", "#menu-main", null);
        //загружаем в основной блок страницу выбора раздела
        script_index.loadBlock("/get&type=content&block=selection-block", "#block-content", null);
        //запускаем скрипт, который загрузит с сервера задачи пользователя в формате JSON в локальную переменную
        script_index.updateTasksListFromJSON(null);

        //выставляем интервал запуска скрипта updateTasksListFromJSON() == 20 сек
        script_index.variables.updateTasksFromJSONTimer = setInterval(function () {
            script_index.updateTasksListFromJSON(null);
        }, 30000);

    },

    //открытие представления "Задачи"
    loadTasks: function () {

        //загрузка меню управления для данного представления (управление задачами)
        script_index.loadBlock("/get&type=menu&block=menu-controls-tasks-common", "#menu-controls", null);
        ////загрузка блока представления "Задачи"
        script_index.loadBlock("/get&type=content&block=task-block-common", "#block-content", script_task_block_common.initTaskBlockCommon);
        $("#index-nav-block").children().removeClass("active");
        $("#index-nav-block-tasks").parent().addClass("active");

    },

    //открытие представления "Файлы". ДОПИСАТЬ ПОТОМ
    loadFiles: function () {
        script_index.loadBlock("/get&type=menu&block=menu-controls-files-common", "#menu-controls", null);
        ////загрузка блока представления "Задачи"
        script_index.loadBlock("/get&type=content&block=file-block-common", "#block-content", null);
        $("#menu-controls").empty();
        $("#block-content").empty();
        $("#index-nav-block").children().removeClass("active");
        $("#index-nav-block-files").parent().addClass("active");
    },


    //загружаем задачи авторизованного пользователя в локальную переменную
    //грузим только измененные задачи

    updateTasksListFromJSON: function (callbackFunction) {
        //формируем строку запроса из шаблона + ВремяПоследнегоЗапроса

        //Формируем URL запроса
        var url = 'task/get_all_json';
        //добавляем время последнего обновления
        var request = "lastUpdateTime" + "="+script_index.variables.mainTasksMap.lastUpdateTime+"&";
        //добаляем массив ID задач, которые есть локально, чтобы проверить, какие были удалены
        request += "checkForExist"+"="+Object.keys(script_index.variables.mainTasksMap.tasks);

        $.ajax({ //отправляем через AJAX
            url: url, //адрес сервлета
            type: "post", //метод запроса
            data: request, //тело запроса
        }).done(function (response, status) {
            if (status == "success" && Object.keys(response).length > 0) {

                //Если сервер передает измененные данные, то вместе с ними он также передает время последнего запроса,
                // результатом которого были данные. Получить переменную можно по ключу lastUpdateTime. Пишем ее значение в локлаьную переменную
                script_index.variables.mainTasksMap.lastUpdateTime = response['lastUpdateTime'];
                //и удаляем из пришедшего массива
                delete response['lastUpdateTime'];

                //проходим по массиву, который пришел в ответе (мы удалили время ответа - остались только задачи в ответе)
                for (var key in response) {

                    //если значение поля == -1, значит, задача была удалена на сервере
                    //удаляем локально
                    if (response[key] == -1) {
                        delete script_index.variables.mainTasksMap.tasks[key];

                        //если задача с этим ID есть в массиве таймеров уведомлений
                        if (key in script_index.variables.notification.notificationTimers) {
                            //отменяем уведомление
                            clearTimeout(script_index.variables.notification.notificationTimers[key]);
                            //удаляем задачу с этим ID из массива уведомлений
                            delete script_index.variables.notification.notificationTimers[key];
                        };

                        continue;
                    }

                    //если задача пришла с флагом deleted, помещаем ее в корзину
                    if (response[key].deleted == true) {
                        //удаляем ее из локального массива
                        delete script_index.variables.mainTasksMap.tasks[key];
                        // и тут идет перемещение в корзину
                        continue;
                    }

                    //если ни одно из условий выше не выполнилось, обновляем запись в локальном массиве
                    script_index.variables.mainTasksMap.tasks[key] = response[key];

                }

                script_index.checkForNotification();

                if (callbackFunction != null) {
                    callbackFunction();
                }
            }

        });
    },


    //скрипт проходит по локальному списку задач, смотрит, когда и по какой задаче надо вывести уведомление
    checkForNotification: function () {

        //время запуска скрипта (в UTC)
        var scriptStartTime = new Date().getTime();

        //если время последней проверки на уведомления раньше, чем время последнего успешного запроса с сервера
        if (script_index.variables.notification.lastCheck <= script_index.variables.mainTasksMap.lastUpdateTime) {

            //Перебираем все елементы массива mainTasksMap.tasks (все локальные задачи)
            $.each(script_index.variables.mainTasksMap.tasks, function (index, task) {

                //если по задаче требуется уведомление
                if (task.notificationNeeded) {

                    //Если время уведомления РАНЬШЕ времени запуска скрипта, значит уведомление пропущено
                    if (task.notifyDate < scriptStartTime) {

                        script_index.variables.notification.missedNotifications[task.id] = task;


                        //если задача с данным ID есть в массиве таймеров, удаляем ее оттуда
                        if (task.id in script_index.variables.notification.notificationTimers) {
                            //отменяем таймер уведомление
                            clearTimeout(script_index.variables.notification.notificationTimers[task.id]);
                            //удаляем поле этой задачи из массива
                            delete script_index.variables.notification.notificationTimers[task.id];
                        }

                        return true;
                    }

                    //Если время обновления задачи позже, чем время последней проверки на уведомления, выполняем дальнейшие действия
                    if (task.lastUpdate >= script_index.variables.notification.lastCheck) {

                        //смотрим, есть ли в массиве уведомлений старый таймер по этой задаче
                        //если да, то отменяем его
                        if (task.id in script_index.variables.notification.notificationTimers) {
                            //отменяем таймер уведомление
                            clearTimeout(script_index.variables.notification.notificationTimers[task.id]);

                            //удаляем этот id
                            delete script_index.variables.notification.notificationTimers[task.id];

                        }

                        //создаем новый таймер c ID == ID задачи как разницу времени между текущим и временем уведомления
                        script_index.variables.notification.notificationTimers[task.id] = setTimeout(function () {
                            script_index.showNotificationTask(task);
                        }, task.notifyDate - Date.now());


                    }
                } else {

                    //если по задаче не требуется уведомление, проверяем, есть ли она в массиве уведомлений
                    // (случай, когда после обновления задачи пользователь снял флаг "Уведомить")
                    if (task.id in script_index.variables.notification.notificationTimers){
                        //отменяем таймер уведомление
                        clearTimeout(script_index.variables.notification.notificationTimers[task.id]);
                        //удаляем поле этой задачи из массива
                        delete script_index.variables.notification.notificationTimers[task.id];
                        alert("TIMER ID"+task.id+" CLEAR");
                    }

                }

            });

            script_index.variables.notification.lastCheck = script_index.variables.mainTasksMap.lastUpdateTime;

        }

    },


    //ДОПИСАТЬ БЛОК
    //скрипт, отвечающий за вывод уведомления по задаче
    showNotificationTask: function(task, text){
        //ТУТ ИДЕТ ФОРМИРОВАНИЕ ТЕКСТА УВЕДОМЛЕНИЯ (БЛОКА)
        var message = "Уведомление по задаче ID: " + task.id + ". Время уведомления: " +
            new Date(task.notifyDate).toLocaleTimeString()+"\n";

        message+="Отложить уведомление? Введите количество минут и наждмите ОК. Отмена для отмены уведомления по задаче.";
        var result = prompt(message, 5);
        script_index.hideNotificationTaskAndPerformAction(task, result);

    },

    //выполнение действия при клике на елементы управления на окне уведомления
    hideNotificationTaskAndPerformAction: function(task, result){

        //получаем с сервера актуальную версию задачи по ID
        var taskFromServer = JSON.parse($.ajax({ //отправляем через AJAX
            url: "/task/open", //адрес сервлета
            type: "post", //метод запроса
            data: "id=" + task.id + "&noHTML=" + true, //тело запроса
            async: false,
        }).responseText).task;

        clearTimeout(script_index.variables.notification.notificationTimers[task.id]);
        delete script_index.variables.notification.notificationTimers[task.id];

        //если пользователь ввел число
        if (result != null && result.length != 0 && parseInt(result) >= 5) {
            taskFromServer.notifyDate = Date.now() + result*60*1000;
        } else {
            taskFromServer.notificationNeeded = false;
            taskFromServer.notifyDate = null;
        }


        //преобразование именованного массива в строку запроса
        var request  = JSON.stringify(taskFromServer);

        //отправляем запрос серверу в виде JSON с задачей
        $.ajax({ //отправляем через AJAX
            url: "/task/edit_from_json", //адрес сервлета
            type: "post", //метод запроса
            data: request, //тело запроса
            contentType: "application/json; charset=utf-8",
        }).done(function(response, status){
            if (status == "success"){
                if (response.allok == true){
                    script_index.updateTasksListFromJSON(script_index.loadTasks);
                } else {
                    alert("GOT ERROR in function: hideNotificationTaskAndPerformAction");
                }
            }
        });
    },

};

