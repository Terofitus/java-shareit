package ru.practicum.shareit.item.util;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Map;

public final class ItemMapper {
    private ItemMapper() {
    }

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable()
        );
    }

    public static Item toItem(ItemDto itemDto) {
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                null,
                null
        );
    }

    public static Item toItemFromMap(Integer id, Map<String, Object> dataOfItem) {
        String name = (String) dataOfItem.get("name");
        String description = (String) dataOfItem.get("description");
        Boolean available;
        if (dataOfItem.get("available") == null) {
            available = null;
        } else {
            available = (Boolean) dataOfItem.get("available");
        }
        return new Item(id,
                name,
                description,
                available,
                null,
                null);
    }
}
