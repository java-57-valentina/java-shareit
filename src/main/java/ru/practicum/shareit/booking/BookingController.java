package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.State;

import java.util.Collection;

import static ru.practicum.shareit.item.ItemController.X_SHARER_USER_ID;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @GetMapping("/{bookingId}")
    public BookingResponseDto getById(
            @RequestHeader(value = X_SHARER_USER_ID) Long userId,
            @PathVariable @Min(1) Long bookingId) {
        return bookingService.findById(bookingId, userId);
    }

   @GetMapping
    public Collection<BookingResponseDto> getByState(
            @RequestHeader(value = X_SHARER_USER_ID) Long userId,
            @RequestParam(name = "state", defaultValue = "ALL") State state) {
        return bookingService.findByState(userId, state);
    }

    @GetMapping("/owner")
    public Collection<BookingResponseDto> getByOwner(
            @RequestHeader(X_SHARER_USER_ID) Long ownerId,
            @RequestParam(name = "state", defaultValue = "ALL") State state) {
        return bookingService.findByOwner(ownerId, state);
    }

    @PostMapping
    public BookingResponseDto add(
            @RequestHeader(value = X_SHARER_USER_ID) Long userId,
            @Valid @RequestBody BookingRequestDto bookingDto) {
        return bookingService.add(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto update(
            @RequestHeader(value = X_SHARER_USER_ID) Long userId,
            @PathVariable @Min(1) Long bookingId,
            @RequestParam(name = "approved") boolean approved) {
        return bookingService.update(userId, bookingId, approved);
    }
}
