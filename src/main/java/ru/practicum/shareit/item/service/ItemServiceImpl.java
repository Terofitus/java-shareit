package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.repository.BookingRepositoryForCustomMethod;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.NoAccessRightsException;
import ru.practicum.shareit.item.dto.CommentDtoForCreate;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.util.ItemMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final BookingRepositoryForCustomMethod bRForCustomMethod;
    private final CommentRepository commentRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserService userService,
                           BookingRepositoryForCustomMethod bRForCustomMethod, CommentRepository commentRepository) {
        this.itemRepository = itemRepository;
        this.userService = userService;
        this.bRForCustomMethod = bRForCustomMethod;
        this.commentRepository = commentRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public ItemDto getItemDtoById(int id, Integer userId) {
        Optional<Item> item = itemRepository.findById(id);
        if (item.isEmpty()) {
            throw new ItemNotFoundException(String.format("Предмет с id=%d не найден", id));
        }
        Item itemFromDb = item.get();
        BookingShortDto nextBooking = null;
        BookingShortDto lastBooking = null;
        List<Comment> comments = commentRepository.findAllByItemId(itemFromDb.getId());
        if (userId != null && Objects.equals(item.get().getOwner().getId(), userId)) {
            nextBooking = bRForCustomMethod.getNextBooking(id);
            lastBooking = bRForCustomMethod.getLastBooking(id);
        }
        log.info("Запрошен предмет с id={}", id);
        return ItemMapper.toItemDtoWithBooking(item.get(), nextBooking, lastBooking, comments);
    }

    @Transactional(readOnly = true)
    @Override
    public Item getItemById(int id) {
        Optional<Item> item = itemRepository.findById(id);
        if (item.isEmpty()) {
            throw new ItemNotFoundException(String.format("Предмет с id=%d не найден", id));
        }
        Item itemFromDb = item.get();

        log.info("Запрошен предмет с id={}", id);
        return itemFromDb;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemDto> getAllItemsOfUser(int userId) {
        User user = userService.getUserById(userId);
        log.info("Запрошенны все предметы пользователя с id={}", userId);

        List<Item> items = itemRepository.findAllByOwner(user);
        return items.stream().map(item -> {
            BookingShortDto nextBooking = bRForCustomMethod.getNextBooking(item.getId());
            BookingShortDto lastBooking = bRForCustomMethod.getLastBooking(item.getId());
            List<Comment> comments = commentRepository.findAllByItemId(item.getId());
            return ItemMapper.toItemDtoWithBooking(item, nextBooking, lastBooking, comments);
        }).collect(Collectors.toList());
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Override
    public Item addItem(int userId, Item item) {
        User owner = userService.getUserById(userId);
        item.setOwner(owner);
        item = itemRepository.save(item);
        log.info("Добавлен предмет с id={}", item.getId());
        return item;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Override
    public Comment addCommentToItem(int userId, CommentDtoForCreate commentDto, int itemId) {
        if (!bRForCustomMethod.checkPastBookings(itemId, userId)) {
            throw new IllegalArgumentException("Добавлять отзывы к предмету могут только пользователи" +
                    " бравшие его в аренду.");
        }
        User user = userService.getUserById(userId);
        Item item = getItemById(itemId);
        Comment comment = new Comment(null, commentDto.getText(), item, user, LocalDateTime.now());
        Comment commentFromDb = commentRepository.save(comment);
        log.info("Добавлен комментарий с id={} к предмету с id={}", userId, itemId);
        return commentFromDb;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Override
    public Item updateItem(Item item, int userId) {
        Optional<Item> itemFromDbOpt = itemRepository.findById(item.getId());
        User user = userService.getUserById(userId);
        if (itemFromDbOpt.isEmpty()) {
            throw new ItemNotFoundException(String.format("Предмет с id=%d не найден", item.getId()));
        }
        Item itemFromDb = itemFromDbOpt.get();
        checkingAccessRightsOfUserToItem(itemFromDb, user);
        prepareItemForUpdate(item, itemFromDb);
        itemFromDb = itemRepository.save(itemFromDb);
        log.info("Предмет с id={} был обновлен", itemFromDb.getId());
        return itemFromDb;
    }

    @Override
    public void deleteAllItemsByUserId(int userId) {
        itemRepository.deleteAllByOwnerId(userId);
        log.info("Удалены все предметы пользователя с id={}", userId);
    }

    @Transactional
    @Override
    public void deleteItemById(int userId, int itemId) {
        Optional<Item> itemFromDbOpt = itemRepository.findById(itemId);
        User user = userService.getUserById(userId);
        if (itemFromDbOpt.isEmpty()) {
            throw new ItemNotFoundException(String.format("Предмет с id=%d не найден", itemId));
        }
        Item itemFromDb = itemFromDbOpt.get();
        checkingAccessRightsOfUserToItem(itemFromDb, user);
        itemRepository.deleteById(itemId);
        log.info("Предмет с id={} был удален", itemId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Item> searchItemsByDescription(String text) {
        return itemRepository.findAllByNameOrDescriptionContainsIgnoreCase(text);
    }

    private void checkingAccessRightsOfUserToItem(Item item, User user) {
        if (!Objects.equals(user.getId(), item.getOwner().getId())) {
            throw new NoAccessRightsException("Только владелец может изменять предмет");
        }
    }

    private void prepareItemForUpdate(Item itemForUpdate, Item itemFromDb) {
        if (itemForUpdate.getName() != null) {
            itemFromDb.setName(itemForUpdate.getName());
        }
        if (itemForUpdate.getDescription() != null) {
            itemFromDb.setDescription(itemForUpdate.getDescription());
        }
        if (itemForUpdate.getAvailable() != null) {
            itemFromDb.setAvailable(itemForUpdate.getAvailable());
        }
    }
}
