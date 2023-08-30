package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.booking.dto.BookingShortDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class ItemDtoWithBooking implements ItemDto {
    private int id;
    private String name;
    private String description;
    private boolean available;
    private BookingShortDto nextBooking;
    private BookingShortDto lastBooking;
}
