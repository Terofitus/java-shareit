package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDtoForCreate;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemDto getItemDtoById(int id, Integer userId);

    Item getItemById(int id);

    List<ItemDto> getAllItemsOfUser(int userId);

    Item addItem(int userId, Item item);

    Comment addCommentToItem(int userId, CommentDtoForCreate commentDto, int itemId);

    Item updateItem(Item item, int userId);

    void deleteAllItemsByUserId(int userId);

    void deleteItemById(int userId, int itemId);

    List<Item> searchItemsByDescription(String text);
}
