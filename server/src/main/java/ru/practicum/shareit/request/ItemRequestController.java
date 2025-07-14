package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.request.dto.ItemRequestExtDtoOut;

import java.util.Collection;

import static ru.practicum.shareit.item.ItemController.X_SHARER_USER_ID;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService requestService;

    @GetMapping("/all")
    public Collection<ItemRequestDtoOut> getAll() {
        return requestService.getAll();
    }

    @GetMapping
    public Collection<ItemRequestExtDtoOut> getByRequester(
            @RequestHeader(X_SHARER_USER_ID) Long requesterId) {
        return requestService.getByRequester(requesterId);
    }

    @GetMapping("/{id}")
    public ItemRequestExtDtoOut getById(
            @PathVariable Long id) {
        return requestService.getById(id);
    }

    @PostMapping
    public ItemRequestDtoOut add(
            @RequestHeader(X_SHARER_USER_ID) Long userId,
            @RequestBody ItemRequestDto requestDto) {
        return requestService.add(userId, requestDto);
    }
}
