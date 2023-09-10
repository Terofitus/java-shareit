package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.exception.ItemNotFoundException;
import ru.practicum.shareit.item.dto.CommentDtoForCreate;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.dto.ItemDtoWithoutBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.ItemMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class ItemServiceIntegrationTest {
    private final UserService userService;
    private final ItemService itemService;
    private final EasyRandom generator = new EasyRandom();

    @BeforeEach
    void setUp() {
        User user1 = generator.nextObject(User.class);
        user1.setId(1);
        User user2 = generator.nextObject(User.class);
        user2.setId(2);
        User user3 = generator.nextObject(User.class);
        user3.setId(3);

        Item item1 = generator.nextObject(Item.class);
        item1.setOwner(user1);
        item1.setRequest(null);
        Item item2 = generator.nextObject(Item.class);
        item2.setOwner(user2);
        item2.setRequest(null);
        Item item3 = generator.nextObject(Item.class);
        item3.setOwner(user2);
        item3.setRequest(null);

        userService.addUser(user1);
        userService.addUser(user2);
        userService.addUser(user3);
        itemService.addItem(1, ItemMapper.toItemDtoWithoutBooking(item1, null));
        itemService.addItem(2, ItemMapper.toItemDtoWithoutBooking(item2, null));
        itemService.addItem(2, ItemMapper.toItemDtoWithoutBooking(item3, null));
    }

    @Test
    void test_getItemDtoById_whenItemExist_shouldReturnItemDto() {
        ItemDtoWithBooking itemDto = (ItemDtoWithBooking) itemService.getItemDtoById(1, 2);

        assertNotNull(itemDto);
        assertEquals(itemDto.getId(), 1);
    }

    @Test
    void test_getItemById_whenItemNotExist_shouldThrowException() {
        ItemNotFoundException exception = assertThrows(ItemNotFoundException.class,
                () -> itemService.getItemById(10));
        assertEquals("Предмет с id=10 не найден", exception.getMessage());
    }

    @Test
    void test_getAllItemsOfUser_whenArgumentsCorrect_shouldReturnListOfItemDto() {
        List<ItemDto> itemsDto = itemService.getAllItemsOfUser(2, 0, 20);

        assertEquals(itemsDto.size(), 2);
    }

    @Test
    void test_addItem_whenArgumentsCorrect_shouldSaveAndReturnItem() {
        ItemDtoWithoutBooking itemDto = generator.nextObject(ItemDtoWithoutBooking.class);
        itemDto.setId(null);
        itemDto.setRequestId(null);

        Item item = itemService.addItem(1, itemDto);

        assertEquals(item.getId(), 4);
    }

    @Test
    void test_addCommentToItem_whenNoBookingsInPast_shouldThrowException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> itemService.addCommentToItem(1, generator.nextObject(CommentDtoForCreate.class), 2));
        assertEquals("Добавлять отзывы к предмету могут только пользователи" +
                " бравшие его в аренду.", exception.getMessage());
    }

    @Test
    void test_updateItem_whenArgumentsCorrect_shouldReturnUpdatedItem() {
        Item item = generator.nextObject(Item.class);
        item.setId(2);

        Item itemFromDb = itemService.updateItem(item, 2);

        assertEquals(itemFromDb, item);
    }

    @Test
    void test_searchItemsByDescription_whenTextContainedInAnyDescription_shouldReturnItem() {
        ItemDtoWithoutBooking itemDto = generator.nextObject(ItemDtoWithoutBooking.class);
        itemDto.setDescription("Мясокомбинат");
        itemDto.setRequestId(null);
        itemDto.setAvailable(true);

        itemService.addItem(2, itemDto);
        List<Item> items = itemService.searchItemsByDescription("КОМ", 0, 20);

        assertEquals(items.size(), 1);
        assertEquals(items.get(0).getId(), 4);
    }
}