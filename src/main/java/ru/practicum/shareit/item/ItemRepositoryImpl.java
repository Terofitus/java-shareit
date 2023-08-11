package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class ItemRepositoryImpl implements ItemRepository {

    private final HashMap<Integer, Item> items = new HashMap<>();
    private int generatedId = 1;

    @Override
    public Item getItemById(int id) {
        Item item = items.get(id);
        if (item == null) {
            log.info("Запрошен недобавленный предмет с id={}.", id);
            throw new ItemNotFoundException(String.format("Нет предмета с id=%d.", id));
        }
        log.info("Запрошен предмет с id={}.", id);
        return item;
    }

    @Override
    public List<Item> getAllItemsOfUser(int userId) {
        List<Item> itemsFromRepository = items.values().stream()
                .filter(item -> item.getOwner().getId() == userId).collect(Collectors.toList());
        log.info("Запрошены предметы пользователя с id={}.", userId);
        if (itemsFromRepository.isEmpty()) {
            throw new ItemNotFoundException(String.format("Не найдено предметов пользователя с id=%d.", userId));
        }
        return itemsFromRepository;
    }

    @Override
    public Item addItem(Item item) {
        item.setId(generatedId++);
        items.put(item.getId(), item);
        log.info("Добавлен предмет с id={}.", item.getId());
        return item;
    }

    @Override
    public Item updateItem(Item item) {
        items.put(item.getId(), item);
        log.info("Обновленны данные предмета с id={}.", item.getId());
        return item;
    }

    @Override
    public void deleteAllItemsOfUser(int userId) {
        items.values().stream()
                .filter(item -> item.getOwner().getId() == userId).map(Item::getId).forEach(items::remove);
        log.info("Удалены все предметы пользователя с id={}.", userId);
    }

    @Override
    public void deleteItemById(int itemId) {
        items.remove(itemId);
        log.info("Удален предмет с id={}.", itemId);
    }

    @Override
    public List<Item> searchItems(String text) {
        log.info("Запрошенны предметы, содержащие в названии или имени \"{}\".", text);
        String textLC = text.toLowerCase();
        return items.values().stream()
                .filter(item -> item.getDescription().toLowerCase().contains(textLC) ||
                        item.getName().toLowerCase().contains(textLC))
                .filter(Item::getAvailable).collect(Collectors.toList());
    }
}
