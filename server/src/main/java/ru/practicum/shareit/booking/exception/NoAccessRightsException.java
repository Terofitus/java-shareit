package ru.practicum.shareit.booking.exception;

public class NoAccessRightsException extends RuntimeException {
    public NoAccessRightsException(String message) {
        super(message);
    }
}
