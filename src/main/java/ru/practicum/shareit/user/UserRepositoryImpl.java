package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.exception.EmailAlreadyTakenException;
import ru.practicum.shareit.user.exception.UserAlreadyExistException;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
@Slf4j
public class UserRepositoryImpl implements UserRepository {

    private final HashMap<Integer, User> users = new HashMap<>();
    private int generatedId = 1;
    @Override
    public List<User> getAllUsers() {
        log.info("Запрошены все пользователи.");
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUserById(int id) {
        if (users.containsKey(id)) {
            log.info("Запрошен пользователь с id={}.", id);
            return  users.get(id);
        } else {
            log.info("Запрошен пользователь с несуществующим id={}.", id);
            throw new UserNotFoundException(String.format("Пользователя с id=%d не найден.", id));
        }
    }

    @Override
    public User addUser(User user) {
        if(isNotFreeEmail(user.getEmail())) {
            log.info("Попытка добавить пользователя с занятым email={}.", user.getEmail());
            throw new UserAlreadyExistException(String.format("Данный email=%s уже занят.", user.getEmail()));
        } else {
            user.setId(generatedId++);
            log.info("Добавлен пользователь с id={}.", user.getId());
            users.put(user.getId(), user);
            return user;
        }
    }

    @Override
    public User updateUser(final User user) {
        String name = user.getName();
        String email = user.getEmail();
        User userFromRepository = users.get(user.getId());
        if(userFromRepository == null) {
            log.info("Попытка обновить пользователя с несуществующим id={}", user.getId());
            throw new UserNotFoundException(String.format("Пользователя с id=%d не найден.", user.getId()));
        }

        if(name != null) {
            userFromRepository.setName(name);
        }

        if(email != null) {
            if(users.values().stream().
                    filter(u -> u.getId() != user.getId()).
                    anyMatch(u -> u.getEmail().equals(email))) {
                throw new EmailAlreadyTakenException(String.format("Email=%s уже занят.", email));
            }
            userFromRepository.setEmail(email);
        }

        users.put(userFromRepository.getId(), userFromRepository);
        log.info("Обновлен пользователь с id={}.", userFromRepository.getId());
        return userFromRepository;
    }

    @Override
    public void deleteAllUsers() {
        users.clear();
        log.info("Все пользователи были удалены.");
    }

    @Override
    public void deleteUser(int id) {
        if (users.containsKey(id)) {
            log.info("Пользователь с id={} удален.", id);
            users.remove(id);
        } else {
            throw new UserNotFoundException(String.format("Пользователя с id=%d не найден.", id));
        }
    }

    private boolean isNotFreeEmail(String email) {
        return users.values().stream().anyMatch(user -> user.getEmail().equals(email));
    }
}
