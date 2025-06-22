package ru.practicum.shareit.exception;

public class OwnerNotMatchException extends RuntimeException {
    public OwnerNotMatchException(String message) {
        super(message);
    }
}
