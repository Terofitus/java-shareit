package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDtoForCreate;
import ru.practicum.shareit.item.dto.CommentDtoForGet;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithoutBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.util.ItemMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ItemController {
    private final ItemService itemService;

    @GetMapping("/{id}")
    public ItemDto getItemById(@PathVariable Integer id,
                               @RequestHeader(name = "X-Sharer-User-Id", required = false) Integer userId) {
        return itemService.getItemDtoById(id, userId);
    }

    @GetMapping
    public List<ItemDto> getAllItemsOfUser(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                           @RequestParam(required = false, defaultValue = "0") Integer from,
                                           @RequestParam(required = false, defaultValue = "20") Integer size) {
        return itemService.getAllItemsOfUser(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItemsByDescription(@RequestParam(required = false, value = "text") String text,
                                                  @RequestParam(required = false, defaultValue = "0") String from,
                                                  @RequestParam(required = false, defaultValue = "20") String size) {
        if (text == null || text.isEmpty()) return new ArrayList<>();
        List<Item> items = itemService.searchItemsByDescription(text, Integer.parseInt(from), Integer.parseInt(size));
        return items.stream().map((Item item) -> ItemMapper.toItemDtoWithoutBooking(item, null))
                .collect(Collectors.toList());
    }

    @PostMapping
    public ItemDto addItem(@RequestHeader("X-Sharer-User-Id") Integer userId,
                           @RequestBody ItemDtoWithoutBooking itemDto) {
        return ItemMapper.toItemDtoWithoutBooking(itemService.addItem(userId, itemDto), null);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDtoForGet addCommentToItem(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                             @RequestBody CommentDtoForCreate comment, @PathVariable Integer itemId) {
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
    public ResponseEntity<Object> deleteAllItemsOfUser(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        itemService.deleteAllItemsByUserId(userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteItemById(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                 @PathVariable(name = "id") Integer itemId) {
        itemService.deleteItemById(userId, itemId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
