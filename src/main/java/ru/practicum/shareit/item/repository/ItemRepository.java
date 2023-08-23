package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    Item getItemById(int id);

    List<Item> getAllItemsOfUser(int userId);

    Item addItem(Item item);

    Item updateItem(Item item);

    void deleteAllItemsOfUserById(int userId);

    void deleteItemById(int itemId);

    List<Item> searchItemsByDescription(String text);
}
