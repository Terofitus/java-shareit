package ru.practicum.shareit.booking.model;

import lombok.Getter;

@Getter
public enum BookingStatus {
    WAITING("Бронирование ожидает подтверждения."),
    APPROVED("Бронирование подтверждено владельцем."),
    REJECTED("Бронирование отклонено владельцем."),
    CANCELED("Бронирование отменено создателем.");

    private final String message;

    BookingStatus(String message) {
        this.message = message;
    }
}
