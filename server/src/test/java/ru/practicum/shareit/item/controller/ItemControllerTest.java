package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.core.type.TypeReference;
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
import org.springframework.test.web.servlet.MvcResult;
import ru.practicum.shareit.item.dto.CommentDtoForCreate;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithoutBooking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.ItemMapper;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {ItemController.class})
class ItemControllerTest {
    private final EasyRandom generator = new EasyRandom();
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    @Autowired
    MockMvc mockMvc;
    @MockBean
    private ItemService itemService;

    @Test
    void test_getItemById_whenItemFound_ShouldReturnJsonAndStatus200() throws Exception {
        Item item = generator.nextObject(Item.class);
        ItemDto itemDto = ItemMapper.toItemDtoWithoutBooking(item, null);

        Mockito.when(itemService.getItemDtoById(Mockito.anyInt(), Mockito.anyInt())).thenReturn(itemDto);

        mockMvc.perform(get("/items/1").header("X-Sharer-User-Id", 1)
                .accept(MediaType.APPLICATION_JSON)).andExpectAll(status().isOk(), content().json(
                mapper.writeValueAsString(itemDto)));
    }

    @Test
    void test_getAllItemsOfUser_whenArgumentsCorrect_shouldReturnJsonAndStatus200() throws Exception {
        Item item1 = generator.nextObject(Item.class);
        Item item2 = generator.nextObject(Item.class);
        List<ItemDto> items = Stream.of(item1, item2).map(item ->
                ItemMapper.toItemDtoWithoutBooking(item, null)).collect(Collectors.toList());

        Mockito.when(itemService.getAllItemsOfUser(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(items);

        mockMvc.perform(get("/items?from=0&size=20").contentType(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1)).andExpectAll(status().isOk(), content().contentType(
                MediaType.APPLICATION_JSON));
    }

    @Test
    void test_searchItemsByDescription_whenTextIsNull_shouldReturnEmptyListAndStatus200() throws Exception {
        MvcResult result = mockMvc.perform(get("/items/search")).andExpectAll(status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON)).andReturn();

        List<ItemDto> items = mapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<List<ItemDto>>() {
                });

        assertTrue(items.isEmpty());
    }

    @Test
    void test_searchItemsByDescription_whenArgumentsCorrect_shouldReturnStatus200() throws Exception {
        Mockito.when(itemService.searchItemsByDescription(Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(Collections.emptyList());

        MvcResult result = mockMvc.perform(get("/items/search?text=12221")).andExpectAll(status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON)).andReturn();

        List<ItemDto> items = mapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<List<ItemDto>>() {
                });

        assertTrue(items.isEmpty());
    }

    @Test
    void test_addItem_whenItemDtoNoCorrect_shouldReturnStatus5400AndErrorResponse() throws Exception {
        ItemDtoWithoutBooking item = generator.nextObject(ItemDtoWithoutBooking.class);
        item.setName(null);
        String json = mapper.writeValueAsString(item);

        mockMvc.perform(post("/items").contentType(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1).content(json)).andExpectAll(status()
                .isInternalServerError(), content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void test_addItem_whenItemDtoCorrect_shouldReturnStatus200AndJson() throws Exception {
        ItemDtoWithoutBooking item = generator.nextObject(ItemDtoWithoutBooking.class);
        String json = mapper.writeValueAsString(item);

        Mockito.when(itemService.addItem(Mockito.anyInt(), Mockito.any(ItemDtoWithoutBooking.class)))
                .thenReturn(generator.nextObject(Item.class));

        mockMvc.perform(post("/items").contentType(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1).content(json)).andExpectAll(status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void test_addCommentToItem_whenArgumentsCorrect_shouldReturnStatus200AndJson() throws Exception {
        CommentDtoForCreate commentDtoForCreate = generator.nextObject(CommentDtoForCreate.class);
        commentDtoForCreate.setText("asdasd");
        Comment commentForCreate = new Comment(null, commentDtoForCreate.getText(),
                generator.nextObject(Item.class), generator.nextObject(User.class), LocalDateTime.now());
        String json = mapper.writeValueAsString(commentDtoForCreate);

        Mockito.when(itemService.addCommentToItem(Mockito.anyInt(), Mockito.any(CommentDtoForCreate.class),
                Mockito.anyInt())).thenReturn(commentForCreate);

        mockMvc.perform(post("/items/1/comment").contentType(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1).content(json)).andExpectAll(status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void test_updateItem_whenHeaderWithUserIdNotSend_shouldReturnStatus500() throws Exception {
        mockMvc.perform(patch("/items/1")).andExpect(status().isInternalServerError());
    }

    @Test
    void test_updateItem_whenArgumentsCorrect_shouldReturnStatus200AndJson() throws Exception {
        Map<String, Object> dataOfItem = new HashMap<>();
        dataOfItem.put("name", "asdada");
        dataOfItem.put("description", "asdasxcv");
        dataOfItem.put("available", true);
        String json = mapper.writeValueAsString(dataOfItem);
        Item item = generator.nextObject(Item.class);

        Mockito.when(itemService.updateItem(Mockito.any(Item.class), Mockito.anyInt())).thenReturn(item);

        mockMvc.perform(patch("/items/1").contentType(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1).content(json)).andExpectAll(status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void test_deleteAllItemsOfUser_whenCalled_shouldReturnStatus200() throws Exception {
        mockMvc.perform(delete("/items").header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());
    }

    @Test
    void test_deleteItemById_whenCalled_shouldReturnStatus200() throws Exception {
        mockMvc.perform(delete("/items/1").header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());
    }
}