package ru.practicum.shareit.item.util;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.dto.ItemDtoWithoutBooking;
import ru.practicum.shareit.item.model.Item;

import java.util.Map;

@UtilityClass
public class ItemMapper {

    public ItemDtoWithBooking toItemDtoWithBooking(Item item, BookingShortDto nextBooking, BookingShortDto lastBooking) {
        BookingShortDto nextBookingDto = null;
        BookingShortDto lastBookingDto = null;
        if(nextBooking != null) {
            nextBookingDto = new BookingShortDto(nextBooking.getId(), nextBooking.getBookerId());
        }
        if(lastBooking != null) {
            lastBookingDto = new BookingShortDto(lastBooking.getId(), lastBooking.getBookerId());
        }
        return new ItemDtoWithBooking(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                nextBookingDto,
                lastBookingDto
        );
    }

    public ItemDtoWithoutBooking toItemDtoWithoutBooking(Item item) {
        return new ItemDtoWithoutBooking(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable());
    }

    public Item toItem(ItemDtoWithoutBooking itemDto) {
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                null,
                null
        );
    }

    public Item toItemFromMap(Integer id, Map<String, Object> dataOfItem) {
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
