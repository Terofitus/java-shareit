package ru.practicum.shareit.item.contoroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDtoForCreate;
import ru.practicum.shareit.item.dto.CommentDtoForGet;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithoutBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.util.ItemMapper;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/{id}")
    public ItemDto getItemById(@PathVariable Integer id,
                               @RequestHeader(name = "X-Sharer-User-Id", required = false) Integer userId) {
        return itemService.getItemDtoById(id, userId);
    }

    @GetMapping
    public List<ItemDto> getAllItemsOfUser(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemService.getAllItemsOfUser(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItemsByDescription(@RequestParam(required = false) String text) {
        if (text.isEmpty()) return new ArrayList<>();
        List<Item> items = itemService.searchItemsByDescription(text);
        return items.stream().map((Item item) -> ItemMapper.toItemDtoWithoutBooking(item, null))
                        .collect(Collectors.toList());
    }

    @PostMapping
    public ItemDto addItem(@RequestHeader("X-Sharer-User-Id") Integer userId,
                           @Valid @RequestBody ItemDtoWithoutBooking itemDto) {
        Item item = ItemMapper.toItem(itemDto);
        return ItemMapper.toItemDtoWithoutBooking(itemService.addItem(userId, item), null);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDtoForGet addCommentToItem(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                             @Valid @RequestBody CommentDtoForCreate comment,
                                             @PathVariable Integer itemId) {
        return ItemMapper.toCommentDtoForGet(itemService.addCommentToItem(userId, comment, itemId));
    }

    @PatchMapping("/{id}")
    public ItemDto updateItem(@PathVariable(name = "id") Integer itemId,
                              @RequestHeader("X-Sharer-User-Id") Integer userId,
                              @RequestBody Map<String, Object> dataOfItem) {
        Item item = ItemMapper.toItemFromMap(itemId, dataOfItem);
        return ItemMapper.toItemDtoWithoutBooking(itemService.updateItem(item, userId), null);
    }

    @DeleteMapping
    public void deleteAllItemsOfUser(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        itemService.deleteAllItemsByUserId(userId);
    }

    @DeleteMapping("/{id}")
    public void deleteItemById(@RequestHeader("X-Sharer-User-Id") Integer userId,
                               @PathVariable(name = "id") Integer itemId) {
        itemService.deleteItemById(userId, itemId);
    }
}
