package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item getItemById(int id);

    List<Item> getAllItemsOfUser(int userId);

    Item addItem(int userId, Item item);

    Item updateItem(Item item, int userId);

    void deleteAllItemsByUserId(int userId);

    void deleteItemById(int userId, int itemId);

    List<Item> searchItemsByDescription(String text);
}
