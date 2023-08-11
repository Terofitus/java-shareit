package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item getItemById(int id);

    List<Item> getAllItemsOfUser(int userId);

    Item addItem(int userId, Item item);

    Item updateItem(Item item, int userId);

    void deleteAllItemsOfUser(int userId);

    void deleteItemById(int userId, int itemId);

    List<Item> searchItems(String text);
}
