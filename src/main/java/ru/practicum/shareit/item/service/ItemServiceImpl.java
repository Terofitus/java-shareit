package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.NoAccessRightsException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserService userService) {
        this.itemRepository = itemRepository;
        this.userService = userService;
    }

    @Override
    public Item getItemById(int id) {
        Optional<Item> item = itemRepository.findById(id);
        if (item.isEmpty()) {
            throw new ItemNotFoundException(String.format("Предмет с id=%d не найден", id));
        }
        log.info("Запрошен предмет с id={}", id);
        return item.get();
    }

    @Override
    public List<Item> getAllItemsOfUser(int userId) {
        User user = userService.getUserById(userId);
        log.info("Запрошенны все предметы пользователя с id={}", userId);
        return itemRepository.findAllByOwner(user);
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
        Item itemFromDb = getItemById(item.getId());
        User user = userService.getUserById(userId);
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
        Item item = getItemById(itemId);
        User user = userService.getUserById(userId);
        checkingAccessRightsOfUserToItem(item, user);
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
