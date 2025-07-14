package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.State;

import java.util.Collection;

import static ru.practicum.shareit.item.ItemController.X_SHARER_USER_ID;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @GetMapping("/{bookingId}")
    public BookingDtoOut getById(
            @RequestHeader(value = X_SHARER_USER_ID) Long userId,
            @PathVariable Long bookingId) {
        return bookingService.findById(bookingId, userId);
    }

   @GetMapping
    public Collection<BookingDtoOut> getByState(
            @RequestHeader(value = X_SHARER_USER_ID) Long userId,
            @RequestParam(name = "state", defaultValue = "ALL") State state) {
        return bookingService.findByState(userId, state);
    }

    @GetMapping("/owner")
    public Collection<BookingDtoOut> getByOwner(
            @RequestHeader(X_SHARER_USER_ID) Long ownerId,
            @RequestParam(name = "state", defaultValue = "ALL") State state) {
        return bookingService.findByOwner(ownerId, state);
    }

    @PostMapping
    public BookingDtoOut add(
            @RequestHeader(value = X_SHARER_USER_ID) Long userId,
            @RequestBody BookingDto bookingDto) {
        return bookingService.add(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoOut update(
            @RequestHeader(value = X_SHARER_USER_ID) Long userId,
            @PathVariable Long bookingId,
            @RequestParam(name = "approved") boolean approved) {
        return bookingService.update(userId, bookingId, approved);
    }
}
