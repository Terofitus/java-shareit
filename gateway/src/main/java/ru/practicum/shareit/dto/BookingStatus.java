package ru.practicum.shareit.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BookingStatus {
    WAITING("Бронирование ожидает подтверждения."),
    APPROVED("Бронирование подтверждено владельцем."),
    REJECTED("Бронирование отклонено владельцем."),
    CANCELED("Бронирование отменено создателем.");

    private final String message;
}
