package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {UserController.class})
class UserControllerTest {
    @Autowired
    MockMvc mockMvc;
    @MockBean
    private UserService userService;
    private final EasyRandom generator = new EasyRandom();
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Test
    void test_getAllUsers_shouldReturnJsonAndStatus200() throws Exception {
        User user1 = generator.nextObject(User.class);
        User user2 = generator.nextObject(User.class);
        List<User> users = List.of(user1, user2);

        Mockito.when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/users").accept(MediaType.APPLICATION_JSON)).andExpectAll(status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void test_getUserById_shouldReturnStatus200AndJson() throws Exception {
        User user = generator.nextObject(User.class);

        Mockito.when(userService.getUserById(Mockito.anyInt())).thenReturn(user);

        mockMvc.perform(get("/users/1")).andExpectAll(status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void test_addUser_whenUserDtoNotValid_shouldReturnStatus500AndErrorResponse() throws Exception {
        User user = new User(1, "  ", null);
        String json = mapper.writeValueAsString(user);

        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpectAll(status().isInternalServerError(), content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void test_addUser_whenUserCorrect_shouldReturnStatus200() throws Exception {
        UserDto user = new UserDto(null, "ASdasd", "1213asdasd@mail.ru");
        String json = mapper.writeValueAsString(user);
        User userFromService = generator.nextObject(User.class);

        Mockito.when(userService.addUser(Mockito.any(User.class))).thenReturn(userFromService);

        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpectAll(status().isOk(), content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void test_updateUser_shouldReturnStatus200AndJson() throws Exception {
        Map<String, Object> data = new HashMap<>();
        data.put("name", "ASD");
        data.put("email", "ASD@list.ru");
        String json = mapper.writeValueAsString(data);
        User user = new User(1, "ASD", "ASD@list.ru");

        Mockito.when(userService.updateUser(Mockito.any(User.class))).thenReturn(user);

        mockMvc.perform(patch("/users/1").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpectAll(status().isOk(), content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void test_deleteAllUsers_shouldReturnStatus200() throws Exception {
        mockMvc.perform(delete("/users")).andExpect(status().isOk());
        Mockito.verify(userService, Mockito.times(1)).deleteAllUsers();
    }

    @Test
    void test_deleteUser_shouldReturnStatus200() throws Exception {
        mockMvc.perform(delete("/users/1")).andExpect(status().isOk());
        Mockito.verify(userService, Mockito.times(1)).deleteUser(Mockito.anyInt());
    }
}