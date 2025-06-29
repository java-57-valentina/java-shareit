package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.user.validation.Create;
import ru.practicum.shareit.user.validation.Update;

import java.util.Collection;

@Validated
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    public static final String X_SHARER_USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ItemDto add(@RequestHeader(X_SHARER_USER_ID) long userId,
                       @Validated({Create.class}) @RequestBody ItemDto itemDto) {
        return itemService.add(itemDto, userId);
    }

    @GetMapping
    public Collection<ItemDto> getAll(
            @RequestHeader(value = X_SHARER_USER_ID, required = false) Long ownerId) {
        if (ownerId != null) {
            return itemService.getByOwner(ownerId);
        } else {
            return itemService.getAll();
        }
    }

    @GetMapping("/{itemId}")
    public ItemResponseDto getById(
            @PathVariable @Min(1) Long itemId,
            @RequestHeader(value = X_SHARER_USER_ID, required = false) Long userId) {
        return itemService.getById(itemId, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(
            @PathVariable @Min(1) Long itemId,
            @RequestHeader(X_SHARER_USER_ID) long userId,
            @Validated({Update.class}) @RequestBody ItemDto itemDto) {
        return itemService.update(itemId, userId, itemDto);
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchItems(
            @RequestHeader(X_SHARER_USER_ID) Long userId,
            @RequestParam(name = "text") String text) {
        return itemService.search(userId, text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentResponseDto addComment(
            @PathVariable @Min(1) Long itemId,
            @RequestHeader(X_SHARER_USER_ID) long userId,
            @Valid @RequestBody CommentRequestDto comment) {
        return itemService.addComment(userId, itemId, comment);
    }

}
