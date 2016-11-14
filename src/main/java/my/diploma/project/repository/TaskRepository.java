package my.diploma.project.repository;

import my.diploma.project.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Евгений on 17.11.2015.
 */
@Transactional
public interface TaskRepository extends JpaRepository<Task, Long> {

    /**
     * Поиск все задач автора,которые были изменены после заданного момента времени
     * @param login логин пользователя
     * @param lastUpdateTime время изменения задачи
     * @return список задач
     */
    @Query("select t from Task t where t.author.login = :login and t.lastUpdate >= :lastUpdateTime")
    List<Task> findAllTaskByAuthorAndUpdateTime(@Param("login") String login, @Param("lastUpdateTime") long lastUpdateTime);

}
