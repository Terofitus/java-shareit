package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/{id}")
    public ItemDto getItemById(@PathVariable Integer id) {
        return ItemMapper.toItemDto(itemService.getItemById(id));
    }

    @GetMapping
    public List<ItemDto> getAllItemsOfUser(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        List<Item> items = itemService.getAllItemsOfUser(userId);
        return items.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam(required = false) String text) {
        if (text.isEmpty()) return new ArrayList<>();
        List<Item> items = itemService.searchItems(text);
        return items.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @PostMapping
    public ItemDto addItem(@RequestHeader("X-Sharer-User-Id") Integer userId, @Valid @RequestBody ItemDto itemDto) {
        Item item = ItemMapper.toItem(itemDto);
        return ItemMapper.toItemDto(itemService.addItem(userId, item));
    }

    @PatchMapping("/{id}")
    public ItemDto updateItem(@PathVariable(name = "id") Integer itemId,
                              @RequestHeader("X-Sharer-User-Id") Integer userId,
                              @RequestBody Map<String, Object> dataOfItem) {
        Item item = ItemMapper.toItemFromMap(itemId, dataOfItem);
        return ItemMapper.toItemDto(itemService.updateItem(item, userId));
    }

    @DeleteMapping
    public void deleteAllItemsOfUser(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        itemService.deleteAllItemsOfUser(userId);
    }

    @DeleteMapping("/{id}")
    public void deleteItemById(@RequestHeader("X-Sharer-User-Id") Integer userId,
                               @PathVariable(name = "id") Integer itemId) {
        itemService.deleteItemById(userId, itemId);
    }
}
