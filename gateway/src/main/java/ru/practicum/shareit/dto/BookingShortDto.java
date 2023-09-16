package ru.practicum.shareit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BookingShortDto {
    private int id;
    private int bookerId;
}
