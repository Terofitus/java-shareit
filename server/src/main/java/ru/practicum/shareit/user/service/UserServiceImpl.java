package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.exception.EmailAlreadyTakenException;
import ru.practicum.shareit.booking.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    @Override
    public List<User> getAllUsers() {
        log.info("Запрошены все пользователи");
        return (List<User>) userRepository.findAll();
    }

    @Transactional(readOnly = true)
    @Override
    public User getUserById(int id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new UserNotFoundException(String.format("Пользователь с id=%d не найден", id));
        }
        log.info("Запрошен пользователь с id={}", id);
        return user.get();
    }

    @Transactional
    @Override
    public User addUser(User user) {
        User userFromDb;
        try {
            userFromDb = userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new EmailAlreadyTakenException("Данный email=" + user.getEmail() + " уже занят");
        }
        log.info("Добавлен пользователь с email={}", user.getEmail());
        return userFromDb;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Override
    public User updateUser(final User user) {
        User userFromDb = getUserById(user.getId());
        prepareUserForUpdate(user, userFromDb);
        userRepository.save(userFromDb);
        log.info("Пользователь id={} был обновлен", user.getId());
        return userFromDb;
    }

    @Override
    public void deleteAllUsers() {
        userRepository.deleteAll();
        log.info("Все пользователи были удаленны");
    }

    @Override
    public void deleteUser(int id) {
        userRepository.deleteById(id);
        log.info("Пользователь с id={} был удален", id);
    }

    private void prepareUserForUpdate(User userForUpdate, User userFromDb) {
        if (userForUpdate.getName() != null) {
            userFromDb.setName(userForUpdate.getName());
        }
        if (userForUpdate.getEmail() != null) {
            userFromDb.setEmail(userForUpdate.getEmail());
        }
    }
}
