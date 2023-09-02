package ru.practicum.shareit.booking.model;

import lombok.Getter;

@Getter
public enum BookingState {
    ALL, CURRENT, PAST, FUTURE, WAITING, REJECTED, UNKNOWN
}
