package my.diploma.project.repository;

import my.diploma.project.entity.SubTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Евгений on 17.11.2015.
 *
 */
@Transactional
public interface SubTaskRepository extends JpaRepository<SubTask, Long> {

}
