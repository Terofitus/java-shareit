package ru.practicum.shareit.user.service;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.booking.exception.EmailAlreadyTakenException;
import ru.practicum.shareit.booking.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @InjectMocks
    UserServiceImpl userService;
    @Mock
    private UserRepository userRepository;
    private final EasyRandom generator = new EasyRandom();

    @Test
    void test_getAllUsers_shouldCallMethodFindAllOfUserRepository() {
        userService.getAllUsers();

        Mockito.verify(userRepository, Mockito.times(1)).findAll();
    }

    @Test
    void test_getUserById_whenUserFounded_shouldReturnUser() {
        User user = generator.nextObject(User.class);

        Mockito.when(userRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(user));

        User userFromService = userService.getUserById(1);

        assertEquals(userFromService, user);
        Mockito.verify(userRepository, Mockito.times(1)).findById(Mockito.anyInt());
    }

    @Test
    void test_getUserById_whenUserNotFound_shouldThrowException() {
        Mockito.when(userRepository.findById(Mockito.anyInt())).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userService.getUserById(1));
        assertEquals("Пользователь с id=1 не найден", exception.getMessage());
        Mockito.verify(userRepository, Mockito.times(1)).findById(Mockito.anyInt());
    }

    @Test
    void test_addUser_whenUserCorrect_shouldReturnUser() {
        User user = generator.nextObject(User.class);

        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user);

        User userFromService = userService.addUser(user);

        assertEquals(userFromService, user);
        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any(User.class));
    }

    @Test
    void test_addUser_whenUserNotSaved_shouldReturnException() {
        User user = generator.nextObject(User.class);

        Mockito.when(userRepository.save(Mockito.any(User.class))).thenThrow(DataIntegrityViolationException.class);

        EmailAlreadyTakenException exception = assertThrows(EmailAlreadyTakenException.class,
                () -> userService.addUser(user));
        assertEquals("Данный email=" + user.getEmail() + " уже занят", exception.getMessage());
    }

    @Test
    void test_updateUser_shouldCallMethodSaveOfUserRepository() {
        User user = generator.nextObject(User.class);
        user.setName("asd");
        User userFromDb = generator.nextObject(User.class);
        userFromDb.setName("asc");

        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(userFromDb);
        Mockito.when(userRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(user));
        User userFromService = userService.updateUser(user);

        assertEquals(userFromService, user);
        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any(User.class));
    }

    @Test
    void test_deleteAllUsers_shouldCallMethodDeleteAllOfUserRepository() {
        userService.deleteAllUsers();

        Mockito.verify(userRepository, Mockito.times(1)).deleteAll();
    }

    @Test
    void test_deleteUser_shouldCallMethodDeleteByIdOfUserRepository() {
        userService.deleteUser(1);

        Mockito.verify(userRepository, Mockito.times(1)).deleteById(Mockito.anyInt());
    }
}