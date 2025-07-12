package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.validation.Update;

@Validated
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemClient itemClient;
    public static final String X_SHARER_USER_ID = "X-Sharer-User-Id";

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> add(@RequestHeader(X_SHARER_USER_ID) long userId,
                                      @Validated({Create.class}) @RequestBody ItemDto itemDto) {
        return itemClient.add(itemDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAll(
            @RequestHeader(value = X_SHARER_USER_ID, required = false) Long ownerId) {
        if (ownerId != null) {
            return itemClient.getByOwner(ownerId);
        } else {
            return itemClient.getAll();
        }
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getById(
            @PathVariable @Min(1) Long itemId,
            @RequestHeader(value = X_SHARER_USER_ID, required = false) Long userId) {
        return itemClient.getById(itemId, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(
            @PathVariable @Min(1) Long itemId,
            @RequestHeader(X_SHARER_USER_ID) long userId,
            @Validated({Update.class}) @RequestBody ItemDto itemDto) {
        return itemClient.update(itemId, userId, itemDto);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(
            @RequestHeader(X_SHARER_USER_ID) Long userId,
            @RequestParam(name = "text") String text) {
        return itemClient.search(userId, text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(
            @PathVariable @Min(1) Long itemId,
            @RequestHeader(X_SHARER_USER_ID) long userId,
            @Valid @RequestBody CommentDto comment) {
        return itemClient.addComment(userId, itemId, comment);
    }

}
