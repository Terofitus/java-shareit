package ru.practicum.shareit.util;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.dto.CommentDtoForGet;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.dto.ItemDtoWithoutBooking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@UtilityClass
public class ItemMapper {

    public ItemDtoWithBooking toItemDtoWithBooking(Item item, BookingShortDto nextBooking,
                                                   BookingShortDto lastBooking, List<Comment> comments) {
        BookingShortDto nextBookingDto = null;
        BookingShortDto lastBookingDto = null;
        if (nextBooking != null) {
            nextBookingDto = new BookingShortDto(nextBooking.getId(), nextBooking.getBookerId());
        }
        if (lastBooking != null) {
            lastBookingDto = new BookingShortDto(lastBooking.getId(), lastBooking.getBookerId());
        }
        List<CommentDtoForGet> commentsDto = new ArrayList<>();
        if (comments != null && !comments.isEmpty()) {
            commentsDto = comments.stream().map(ItemMapper::toCommentDtoForGet)
                    .collect(Collectors.toList());
        }
        return new ItemDtoWithBooking(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                nextBookingDto,
                lastBookingDto,
                commentsDto
        );
    }

    public ItemDtoWithoutBooking toItemDtoWithoutBooking(Item item, List<Comment> comments) {
        List<CommentDtoForGet> commentsDto = new ArrayList<>();
        if (comments != null && !comments.isEmpty()) {
            commentsDto = comments.stream().map(ItemMapper::toCommentDtoForGet).collect(Collectors.toList());
        }
        return new ItemDtoWithoutBooking(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                commentsDto,
                item.getRequest() == null ? null : item.getRequest().getId());
    }

    public Item toItem(ItemDtoWithoutBooking itemDto) {
        return new Item(
                itemDto.getId() == null ? null : itemDto.getId(),
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

    public CommentDtoForGet toCommentDtoForGet(Comment comment) {
        return new CommentDtoForGet(comment.getId(), comment.getText(),
                comment.getAuthor().getName(), comment.getCreated());
    }
}
