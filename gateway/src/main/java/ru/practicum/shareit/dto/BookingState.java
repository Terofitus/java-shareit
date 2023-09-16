package ru.practicum.shareit.dto;

import lombok.Getter;

import java.util.Optional;

@Getter
public enum BookingState {
    ALL, CURRENT, PAST, FUTURE, WAITING, REJECTED, UNKNOWN;

    public static Optional<BookingState> from(String stringState) {
        for (BookingState state : values()) {
            if (state.name().equalsIgnoreCase(stringState)) {
                return Optional.of(state);
            }
        }
        return Optional.empty();
    }
}
