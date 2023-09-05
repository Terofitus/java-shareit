package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class UserServiceIntegrationTest {
    private final UserService userService;
    private final EasyRandom generator = new EasyRandom();

    @BeforeEach
    void setUp() {
        User user1 = generator.nextObject(User.class);
        user1.setId(1);
        User user2 = generator.nextObject(User.class);
        user2.setId(2);

        userService.addUser(user1);
        userService.addUser(user2);
    }

    @Test
    void test_getAllUsers_whenUsersExist_shouldReturnListOfUser() {
        List<User> users = userService.getAllUsers();

        assertEquals(users.size(), 2);
    }

    @Test
    void test_getUserById_whenUserExist_shouldReturnUser() {
        User user = userService.getUserById(1);

        assertNotNull(user);
    }

    @Test
    void test_addUser_whenUserNotAdded_shouldSaveAndReturnUser() {
        User user = new User(null, "asdasd", "ADadsasda@mail.ru");

        User userFromDb = userService.addUser(user);

        assertEquals(user, userFromDb);
        assertNotNull(userFromDb.getId());
    }
}