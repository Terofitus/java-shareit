package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingDtoForGet {
    private int id;
    private LocalDateTime start;
    private LocalDateTime end;
    private ItemShortDto item;
    private UserShortDto booker;
    private BookingStatus status;

    @Data
    @AllArgsConstructor
    public static class ItemShortDto {
        private int id;
        private String name;
    }

    @Data
    @AllArgsConstructor
    public static class UserShortDto {
        private int id;
    }
}
