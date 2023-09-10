package ru.practicum.shareit.item.service;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.exception.ItemNotFoundException;
import ru.practicum.shareit.booking.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.booking.exception.NoAccessRightsException;
import ru.practicum.shareit.booking.repository.BookingRepositoryForCustomMethod;
import ru.practicum.shareit.item.dto.CommentDtoForCreate;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.dto.ItemDtoWithoutBooking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.ItemMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    private final EasyRandom generator = new EasyRandom();
    @InjectMocks
    private ItemServiceImpl itemService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserService userService;
    @Mock
    private BookingRepositoryForCustomMethod bRForCustomMethod;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Test
    void test_getItemDtoById_whenArgumentsCorrect_shouldReturnItemDto() {
        Item item = generator.nextObject(Item.class);
        item.getOwner().setId(1);
        item.setId(1);
        List<Comment> comments = generator.objects(Comment.class, 3).collect(Collectors.toList());
        BookingShortDto nextBooking = generator.nextObject(BookingShortDto.class);
        BookingShortDto lastBooking = generator.nextObject(BookingShortDto.class);

        Mockito.when(itemRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(item));
        Mockito.when(commentRepository.findAllByItemId(Mockito.anyInt())).thenReturn(comments);
        Mockito.when(bRForCustomMethod.getNextBooking(Mockito.anyInt())).thenReturn(nextBooking);
        Mockito.when(bRForCustomMethod.getLastBooking(Mockito.anyInt())).thenReturn(lastBooking);

        ItemDtoWithBooking itemDto = (ItemDtoWithBooking) itemService.getItemDtoById(1, 1);

        assertEquals(ItemMapper.toItemDtoWithBooking(item, nextBooking, lastBooking, comments), itemDto);
        assertNotNull(itemDto.getNextBooking());
        assertNotNull(itemDto.getLastBooking());
        assertNotNull(itemDto.getComments());
    }

    @Test
    void test_getItemDtoById_whenItemNotFound_shouldThrowException() {
        Mockito.when(itemRepository.findById(Mockito.anyInt())).thenReturn(Optional.empty());

        ItemNotFoundException exception = assertThrows(ItemNotFoundException.class,
                () -> itemService.getItemDtoById(1, 1));
        assertEquals("Предмет с id=1 не найден", exception.getMessage());
    }

    @Test
    void test_getItemById_whenArgumentsCorrect_shouldReturnItem() {
        Item item = generator.nextObject(Item.class);
        Mockito.when(itemRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(item));

        Item itemFromService = itemService.getItemById(1);

        assertEquals(item, itemFromService);
    }

    @Test
    void test_getItemById_whenItemNotFound_shouldThrowException() {
        Mockito.when(itemRepository.findById(Mockito.anyInt())).thenReturn(Optional.empty());

        ItemNotFoundException exception = assertThrows(ItemNotFoundException.class,
                () -> itemService.getItemById(1));
        assertEquals("Предмет с id=1 не найден", exception.getMessage());
    }

    @Test
    void test_getAllItemsOfUser_whenArgumentsCorrect_shouldReturnListOfItems() {
        Item item1 = generator.nextObject(Item.class);
        Item item2 = generator.nextObject(Item.class);
        User user = generator.nextObject(User.class);
        List<Item> items = List.of(item1, item2);
        List<ItemDto> itemsDto = items.stream().map(item -> ItemMapper.toItemDtoWithBooking(item, null,
                null, null)).collect(Collectors.toList());

        Mockito.when(userService.getUserById(Mockito.anyInt())).thenReturn(user);
        Mockito.when(itemRepository.findAllByOwner(Mockito.any(User.class),
                Mockito.any(Pageable.class))).thenReturn(items);

        List<ItemDto> itemsFromService = itemService.getAllItemsOfUser(user.getId(), 0, 20);

        assertEquals(itemsDto.get(0), itemsFromService.get(0));
        assertEquals(itemsDto.get(1), itemsFromService.get(1));
    }

    @Test
    void test_getAllItemsOfUser_whenFromOrSizeIncorrect_shouldThrowException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> itemService.getAllItemsOfUser(1, -2, 0));

        assertEquals("Аргумент from не может быть меньше size и 0, " +
                "аргумент size не может быть равен или меньше 0.", exception.getMessage());
    }

    @Test
    void test_addItem_whenUserCorrect_shouldReturnItem() {
        User user = generator.nextObject(User.class);
        Item item = generator.nextObject(Item.class);
        ItemRequest itemRequest = generator.nextObject(ItemRequest.class);

        Mockito.when(userService.getUserById(Mockito.anyInt())).thenReturn(user);
        Mockito.when(itemRequestRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(itemRequest));
        Mockito.when(itemRepository.save(item)).thenReturn(item);

        Item itemFromService = itemService.addItem(1, ItemMapper.toItemDtoWithoutBooking(item, null));

        assertEquals(item, itemFromService);
        Mockito.verify(itemRepository, Mockito.times(1)).save(item);
    }

    @Test
    void test_addItem_whenItemRequestIdNotNullButRequestNotFound_shouldThrowException() {
        ItemDtoWithoutBooking itemDto = generator.nextObject(ItemDtoWithoutBooking.class);
        itemDto.setRequestId(1);
        User user = generator.nextObject(User.class);

        Mockito.when(itemRequestRepository.findById(1)).thenReturn(Optional.empty());
        Mockito.when(userService.getUserById(Mockito.anyInt())).thenReturn(user);

        ItemRequestNotFoundException exception = assertThrows(ItemRequestNotFoundException.class,
                () -> itemService.addItem(1, itemDto));
        assertEquals("Запрос запроса предмета с id=1 не найден.", exception.getMessage());
    }

    @Test
    void test_addCommentToItem_whenArgumentsCorrect_shouldReturnComment() {
        User user = generator.nextObject(User.class);
        Item item = generator.nextObject(Item.class);
        CommentDtoForCreate commentDto = generator.nextObject(CommentDtoForCreate.class);
        Comment comment = new Comment(null, commentDto.getText(), item, user, LocalDateTime.now());

        Mockito.when(bRForCustomMethod.checkPastBookings(Mockito.anyInt(), Mockito.anyInt())).thenReturn(true);
        Mockito.when(userService.getUserById(Mockito.anyInt())).thenReturn(user);
        Mockito.when(itemRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(item));
        Mockito.when(commentRepository.save(Mockito.any(Comment.class))).thenReturn(comment);

        Comment commentFromService = itemService.addCommentToItem(1, commentDto, 1);

        assertEquals(comment, commentFromService);
        Mockito.verify(commentRepository, Mockito.times(1)).save(Mockito.any(Comment.class));
    }

    @Test
    void test_addCommentToItem_whenNotFoundPastBookingOfUser_shouldReturnException() {
        Mockito.when(bRForCustomMethod.checkPastBookings(Mockito.anyInt(), Mockito.anyInt())).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> itemService.addCommentToItem(1, generator.nextObject(CommentDtoForCreate.class), 1));
        assertEquals("Добавлять отзывы к предмету могут только пользователи бравшие его в аренду.",
                exception.getMessage());
    }

    @Test
    void test_updateItem_whenArgumentsCorrect_shouldReturnUpdatedItem() {
        User user = generator.nextObject(User.class);
        user.setId(1);
        Item item = generator.nextObject(Item.class);
        item.getOwner().setId(1);

        Mockito.when(itemRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(item));
        Mockito.when(userService.getUserById(Mockito.anyInt())).thenReturn(user);
        Mockito.when(itemRepository.save(Mockito.any(Item.class))).thenReturn(item);

        Item itemFromService = itemService.updateItem(item, 1);

        assertEquals(item, itemFromService);
    }

    @Test
    void test_updateItem_whenItemNotFound_shouldThrowException() {
        User user = generator.nextObject(User.class);
        user.setId(1);
        Item item = generator.nextObject(Item.class);
        item.getOwner().setId(1);
        item.setId(1);

        Mockito.when(itemRepository.findById(Mockito.anyInt())).thenReturn(Optional.empty());
        Mockito.when(userService.getUserById(Mockito.anyInt())).thenReturn(user);

        ItemNotFoundException exception = assertThrows(ItemNotFoundException.class,
                () -> itemService.updateItem(item, 1));
        assertEquals("Предмет с id=1 не найден", exception.getMessage());
        Mockito.verify(itemRepository, Mockito.never()).save(Mockito.any(Item.class));
    }

    @Test
    void test_updateItem_whenUserNotOwnerOfItem_shouldThrowException() {
        User user = generator.nextObject(User.class);
        user.setId(1);
        Item item = generator.nextObject(Item.class);
        item.getOwner().setId(2);
        item.setId(1);

        Mockito.when(itemRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(item));
        Mockito.when(userService.getUserById(Mockito.anyInt())).thenReturn(user);

        NoAccessRightsException exception = assertThrows(NoAccessRightsException.class,
                () -> itemService.updateItem(item, 1));
        assertEquals("Только владелец может изменять предмет", exception.getMessage());
        Mockito.verify(itemRepository, Mockito.never()).save(Mockito.any(Item.class));
    }

    @Test
    void test_deleteAllItemsByUserId_shouldCallMethodDeleteAllItemsByOwnerIdOfItemRepository() {
        itemService.deleteAllItemsByUserId(1);

        Mockito.verify(itemRepository, Mockito.times(1)).deleteAllByOwnerId(1);
    }

    @Test
    void test_deleteItemById_whenArgumentsCorrect_shouldCallMethodDeleteByIdOfItemRepository() {
        User user = generator.nextObject(User.class);
        user.setId(1);
        Item item = generator.nextObject(Item.class);
        item.getOwner().setId(1);
        item.setId(1);

        Mockito.when(itemRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(item));
        Mockito.when(userService.getUserById(Mockito.anyInt())).thenReturn(user);

        itemService.deleteItemById(1, 1);

        Mockito.verify(itemRepository, Mockito.times(1)).deleteById(1);
    }

    @Test
    void test_deleteItemById_whenItemNotFound_shouldThrowException() {
        User user = generator.nextObject(User.class);
        user.setId(1);

        Mockito.when(itemRepository.findById(Mockito.anyInt())).thenReturn(Optional.empty());
        Mockito.when(userService.getUserById(Mockito.anyInt())).thenReturn(user);

        ItemNotFoundException exception = assertThrows(ItemNotFoundException.class,
                () -> itemService.deleteItemById(1, 1));
        assertEquals("Предмет с id=1 не найден", exception.getMessage());
        Mockito.verify(itemRepository, Mockito.never()).save(Mockito.any(Item.class));
    }

    @Test
    void test_searchItemsByDescription_whenArgumentsCorrect_shouldReturnListOfItems() {
        Item item1 = generator.nextObject(Item.class);
        Item item2 = generator.nextObject(Item.class);
        List<Item> items = List.of(item1, item2);

        Mockito.when(itemRepository.findAllByNameOrDescriptionContainsIgnoreCase(Mockito.anyString(),
                Mockito.any(Pageable.class))).thenReturn(items);

        List<Item> itemsFromService = itemService.searchItemsByDescription("asda", 0, 20);

        assertTrue(itemsFromService.contains(item1));
        assertTrue(itemsFromService.contains(item2));
    }

    @Test
    void test_searchItemsByDescription_whenArgumentsIncorrect_shouldThrowException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> itemService.searchItemsByDescription("asd", -2, 0));
        assertEquals("Аргумент from не может быть меньше size и 0, " +
                "аргумент size не может быть равен или меньше 0.", exception.getMessage());
        Mockito.verify(itemRepository, Mockito.never()).findAllByNameOrDescriptionContainsIgnoreCase(
                Mockito.anyString(),
                Mockito.any(Pageable.class));
    }
}