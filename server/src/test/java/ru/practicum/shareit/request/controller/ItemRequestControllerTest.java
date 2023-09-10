package ru.practicum.shareit.request.controller;

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
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.ArrayList;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {ItemRequestController.class})
class ItemRequestControllerTest {
    private final EasyRandom generator = new EasyRandom();
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    @Autowired
    MockMvc mockMvc;
    @MockBean
    private ItemRequestService itemRequestService;

    @Test
    void test_addItemRequest_whenArgumentsCorrect_shouldReturnStatus200AndJson() throws Exception {
        ItemRequestDto itemDto = generator.nextObject(ItemRequestDto.class);
        itemDto.setId(null);
        itemDto.setDescription("asdasda");
        itemDto.setItems(null);
        String json = mapper.writeValueAsString(itemDto);

        Mockito.when(itemRequestService.addItemRequest(Mockito.anyInt(), Mockito.any(ItemRequestDto.class)))
                .thenReturn(itemDto);

        mockMvc.perform(post("/requests").contentType(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1).content(json)).andExpectAll(
                status().isOk(), content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void test_getAllItemRequestsByOwnerId_whenHeaderWithUserIdNotSend_shouldReturnStatus500AndErrorResponse()
            throws Exception {
        mockMvc.perform(get("/requests")).andExpectAll(status().isInternalServerError(),
                content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void test_getAllItemRequests_whenArgumentsCorrect_shouldReturnStatus200AndJson() throws Exception {
        Mockito.when(itemRequestService.getAllItemRequests(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(new ArrayList<>());

        mockMvc.perform(get("/requests/1?from=0&size=20")
                .header("X-Sharer-User-Id", 1)
                .accept(MediaType.APPLICATION_JSON)).andExpectAll(status().isOk());
    }

    @Test
    void test_getItemRequests_whenArgumentsCorrect_shouldReturnStatus200AndJson() throws Exception {
        ItemRequestDto itemRequestDto = generator.nextObject(ItemRequestDto.class);

        Mockito.when(itemRequestService.getItemRequest(Mockito.anyInt(), Mockito.anyInt())).thenReturn(itemRequestDto);

        mockMvc.perform(get("/requests/1").header("X-Sharer-User-Id", 1)).andExpectAll(
                status().isOk(), content().contentType(MediaType.APPLICATION_JSON));
    }
}