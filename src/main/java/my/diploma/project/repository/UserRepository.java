package my.diploma.project.repository;

import my.diploma.project.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Евгений on 17.11.2015.
 */
@Transactional
public interface UserRepository extends JpaRepository<User, String>{

}
