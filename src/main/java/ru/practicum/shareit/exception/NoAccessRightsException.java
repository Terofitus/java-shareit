package ru.practicum.shareit.exception;

public class NoAccessRightsException extends RuntimeException {
    public NoAccessRightsException(String message) {
        super(message);
    }
}
