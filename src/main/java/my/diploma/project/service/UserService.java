package my.diploma.project.service;

import my.diploma.project.exception.UserNotFound;
import my.diploma.project.entity.User;

import java.util.List;

/**
 * Created by Евгений on 17.11.2015.
 */
public interface UserService {
    User create(User user);
    User update(User user) throws UserNotFound;
    User delete(String login) throws UserNotFound;
    User findByLogin(String login);
    List<User> findAll();
    boolean exists(String login);
}
