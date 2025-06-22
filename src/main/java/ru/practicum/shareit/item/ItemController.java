package ru.practicum.shareit.item;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.validation.Create;
import ru.practicum.shareit.user.validation.Update;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    public static final String X_SHARER_USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ItemDto add(@RequestHeader(X_SHARER_USER_ID) long ownerId,
                       @Validated({Create.class}) @RequestBody ItemDto itemDto) {
        ItemDto added = itemService.add(itemDto, ownerId);
        log.info("Item was added: {}", added);
        return added;
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
    public ItemDto getById(@PathVariable @Min(1) Long itemId) {
        return itemService.getById(itemId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(
            @PathVariable @Min(1) Long itemId,
            @RequestHeader(X_SHARER_USER_ID) long ownerId,
            @Validated({Update.class}) @RequestBody ItemDto itemDto) {
        ItemDto updated = itemService.update(itemId, ownerId, itemDto);
        log.info("Item was updated: {}", updated);
        return updated;
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchItems(@RequestHeader(X_SHARER_USER_ID) Long ownerId,
                                     @RequestParam(name = "text") String text) {
        return itemService.search(ownerId, text);
    }
}
