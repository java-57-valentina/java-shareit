package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.State;

import static ru.practicum.shareit.Constants.X_SHARER_USER_ID;

@Validated
@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingClient bookingClient;

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getById(
            @RequestHeader(value = X_SHARER_USER_ID) Long userId,
            @PathVariable @Min(1) Long bookingId) {
        return bookingClient.findById(bookingId, userId);
    }

   @GetMapping
    public ResponseEntity<Object> getByState(
            @RequestHeader(value = X_SHARER_USER_ID) Long userId,
            @RequestParam(name = "state", defaultValue = "ALL") State state) {
        return bookingClient.findByState(userId, state);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getByOwner(
            @RequestHeader(X_SHARER_USER_ID) Long ownerId,
            @RequestParam(name = "state", defaultValue = "ALL") State state) {
        return bookingClient.findByOwner(ownerId, state);
    }

    @PostMapping
    public ResponseEntity<Object> add(
            @RequestHeader(value = X_SHARER_USER_ID) Long userId,
            @Valid @RequestBody BookingDto bookingDto) {
        return bookingClient.add(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> update(
            @RequestHeader(value = X_SHARER_USER_ID) Long userId,
            @PathVariable @Min(1) Long bookingId,
            @RequestParam(name = "approved") boolean approved) {
        return bookingClient.update(userId, bookingId, approved);
    }
}
