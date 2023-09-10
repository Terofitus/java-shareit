package ru.practicum.shareit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"available", "nextBooking", "lastBooking", "comments"})
public class ItemDtoWithBooking implements ItemDto {
    private int id;
    private String name;
    private String description;
    private boolean available;
    private BookingShortDto nextBooking;
    private BookingShortDto lastBooking;
    private List<CommentDtoForGet> comments;
}
