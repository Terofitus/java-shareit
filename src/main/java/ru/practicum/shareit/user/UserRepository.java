package ru.practicum.shareit.user;


import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {
    List<User> getAllUsers();

    User getUserById(int id);

    User addUser(User user);

    User updateUser(User user);

    void deleteAllUsers();

    void deleteUser(int id);
}
