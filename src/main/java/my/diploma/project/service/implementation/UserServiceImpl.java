package my.diploma.project.service.implementation;

import my.diploma.project.entity.User;
import my.diploma.project.exception.UserNotFound;
import my.diploma.project.repository.UserRepository;
import my.diploma.project.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by Евгений on 17.11.2015.
 */
@Service
public class UserServiceImpl implements UserService {
    @Resource
    private UserRepository userRepository;

    @Override
    public User create(User user) {
        User userToCreate = userRepository.saveAndFlush(user);
        return userToCreate;
    }

    @Override
    public User update(User user) throws UserNotFound {
        User userToUpdate = userRepository.findOne(user.getLogin());
        if (userToUpdate == null){
            throw new UserNotFound();
        }
        user.setLogin(userToUpdate.getLogin());
        return userRepository.saveAndFlush(user);
    }

    @Override
    public User delete(String login) throws UserNotFound {
        User userToDelete = userRepository.findOne(login);
        if (userToDelete == null){
            throw new UserNotFound();
        }
        userRepository.delete(userToDelete);
        return userToDelete;
    }

    @Override
    public User findByLogin(String login) {
        return userRepository.findOne(login);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public boolean exists(String login) {
        return userRepository.exists(login);
    }
}
