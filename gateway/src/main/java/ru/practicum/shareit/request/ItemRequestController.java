package ru.practicum.shareit.request;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import static ru.practicum.shareit.item.ItemController.X_SHARER_USER_ID;

@Validated
@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @GetMapping("/all")
    public ResponseEntity<Object> getAll() {
        return itemRequestClient.getAll();
    }

    @GetMapping
    public ResponseEntity<Object> getByRequester(
            @RequestHeader(X_SHARER_USER_ID) Long requesterId) {
        return itemRequestClient.getByRequester(requesterId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(
            @PathVariable @Min(1) Long id) {
        return itemRequestClient.getById(id);
    }

    @PostMapping
    public ResponseEntity<Object> add(
            @RequestHeader(X_SHARER_USER_ID) Long userId,
            @RequestBody ItemRequestDto requestDto) {
        return itemRequestClient.add(userId, requestDto);
    }
}
