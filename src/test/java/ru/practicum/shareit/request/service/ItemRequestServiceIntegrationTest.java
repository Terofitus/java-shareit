package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.ItemMapper;
import ru.practicum.shareit.util.ItemRequestMapper;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class ItemRequestServiceIntegrationTest {
    private final ItemRequestService itemRequestService;
    private final UserService userService;
    private final ItemService itemService;
    private final EasyRandom generator = new EasyRandom();

    @BeforeEach
    void setUp() {
        User user1 = generator.nextObject(User.class);
        user1.setId(1);
        User user2 = generator.nextObject(User.class);
        user2.setId(2);

        ItemRequestDto itemRequestDto = new ItemRequestDto(null, "asd", LocalDateTime.now(), null);
        ItemRequest itemRequest = ItemRequestMapper
                .toItemRequest(itemRequestService.addItemRequest(1, itemRequestDto), user1);

        Item item1 = generator.nextObject(Item.class);
        item1.setOwner(user1);
        item1.setRequest(null);
        Item item2 = generator.nextObject(Item.class);
        item2.setOwner(user2);
        item2.setRequest(itemRequest);

        userService.addUser(user1);
        userService.addUser(user2);
        itemService.addItem(1, ItemMapper.toItemDtoWithoutBooking(item1, null));
        itemService.addItem(2, ItemMapper.toItemDtoWithoutBooking(item2, null));
    }

    @Test
    void test_addItemRequest_whenArgumentsCorrect_shouldSaveAndReturnItemRequest() {
        ItemRequestDto itemRequestDto = generator.nextObject(ItemRequestDto.class);
        itemRequestDto.setId(null);

        ItemRequestDto itemDtoFromDb = itemRequestService.addItemRequest(1, itemRequestDto);

        assertNotNull(itemDtoFromDb.getId());
    }

    @Test
    void test_getAllItemRequestsByOwnerId_whenItemRequestsExist_shouldReturnListOfItemRequestDto() {
        List<ItemRequestDto> itemRequests = itemRequestService.getAllItemRequestsByOwnerId(1);

        assertFalse(itemRequests.isEmpty());
    }

    @Test
    void test_getAllItemRequests_whenCalledOwnerOfRequests_shouldReturnListOfRequestsWithoutOwnRequests() {
        List<ItemRequestDto> itemRequestDtoList1 = itemRequestService.getAllItemRequests(1, 0, 20);
        List<ItemRequestDto> itemRequestDtoList2 = itemRequestService.getAllItemRequests(2, 0, 20);

        assertTrue(itemRequestDtoList1.isEmpty());
        assertEquals(itemRequestDtoList2.size(), 1);
    }

    @Test
    void test_getItemRequest_whenIdIncorrect_shouldThrowException() {
        ItemRequestNotFoundException exception = assertThrows(ItemRequestNotFoundException.class,
                () -> itemRequestService.getItemRequest(1, 100));
    }
}