package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.repository.BookingRepositoryForCustomMethod;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.NoAccessRightsException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.util.ItemMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final BookingRepositoryForCustomMethod bRForCustomMethod;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserService userService,
                           BookingRepository bookingRepository, BookingRepositoryForCustomMethod bRForCustomMethod) {
        this.itemRepository = itemRepository;
        this.userService = userService;
        this.bookingRepository = bookingRepository;
        this.bRForCustomMethod = bRForCustomMethod;
    }

    @Override
    public ItemDto getItemDtoById(int id, Integer userId) {
        Optional<Item> item = itemRepository.findById(id);
        if (item.isEmpty()) {
            throw new ItemNotFoundException(String.format("Предмет с id=%d не найден", id));
        }
        Item itemFromDb = item.get();
        BookingShortDto nextBooking = null;
        BookingShortDto lastBooking = null;
        if (userId != null && item.get().getOwner().getId() == userId) {
            nextBooking = bRForCustomMethod.getNextBooking(id);
            lastBooking = bRForCustomMethod.getLastBooking(id);

        }
        log.info("Запрошен предмет с id={}", id);
        return ItemMapper.toItemDtoWithBooking(item.get(), nextBooking, lastBooking);
    }

    @Override
    public Item getItemById(int id, Integer userId) {
        Optional<Item> item = itemRepository.findById(id);
        if (item.isEmpty()) {
            throw new ItemNotFoundException(String.format("Предмет с id=%d не найден", id));
        }
        Item itemFromDb = item.get();

        log.info("Запрошен предмет с id={}", id);
        return itemFromDb;
    }

    @Override
    public List<ItemDto> getAllItemsOfUser(int userId) {
        User user = userService.getUserById(userId);
        log.info("Запрошенны все предметы пользователя с id={}", userId);

        List<Item> items = itemRepository.findAllByOwner(user);
        return items.stream().map(item -> {
            BookingShortDto nextBooking = bRForCustomMethod.getNextBooking(item.getId());
            BookingShortDto lastBooking = bRForCustomMethod.getLastBooking(item.getId());
            return ItemMapper.toItemDtoWithBooking(item, nextBooking, lastBooking);
        }).collect(Collectors.toList());
    }

    @Override
    public Item addItem(int userId, Item item) {
        User owner = userService.getUserById(userId);
        item.setOwner(owner);
        item = itemRepository.save(item);
        log.info("Добавлен предмет с id={}", item.getId());
        return item;
    }

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
        log.info("Удалены все предметы пользователя с id={}",userId);
    }

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
        log.info("Предмет с id={} был удален",itemId);
    }

    @Override
    public List<Item> searchItemsByDescription(String text) {
        return itemRepository.findAllByNameOrDescriptionContainsIgnoreCase(text);
    }

    private void checkingAccessRightsOfUserToItem(Item item, User user) {
        if (user.getId() != item.getOwner().getId()) {
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
