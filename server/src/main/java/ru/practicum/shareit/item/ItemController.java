package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    public static final String X_SHARER_USER_ID = "X-Sharer-User-Id";

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDtoOut add(@RequestHeader(X_SHARER_USER_ID) Long userId,
                          @RequestBody ItemDto itemDto) {
        log.info("try add item: {}", itemDto);
        return itemService.add(itemDto, userId);
    }

    @GetMapping
    public Collection<ItemDtoOut> getAll(
            @RequestHeader(value = X_SHARER_USER_ID, required = false) Long ownerId) {
        if (ownerId != null) {
            return itemService.getByOwner(ownerId);
        } else {
            return itemService.getAll();
        }
    }

    @GetMapping("/{itemId}")
    public ItemDtoExtOut getById(
            @PathVariable Long itemId,
            @RequestHeader(value = X_SHARER_USER_ID, required = false) Long userId) {
        return itemService.getById(itemId, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDtoOut update(
            @PathVariable Long itemId,
            @RequestHeader(X_SHARER_USER_ID) Long userId,
            @RequestBody ItemDto itemDto) {
        return itemService.update(itemId, userId, itemDto);
    }

    @GetMapping("/search")
    public Collection<ItemDtoOut> searchItems(
            @RequestHeader(X_SHARER_USER_ID) Long userId,
            @RequestParam(name = "text") String text) {
        return itemService.search(userId, text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDtoOut addComment(
            @PathVariable Long itemId,
            @RequestHeader(X_SHARER_USER_ID) Long userId,
            @RequestBody CommentDto comment) {
        return itemService.addComment(userId, itemId, comment);
    }

}
