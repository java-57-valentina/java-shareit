package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class BookingRequestDto {

    private Long id;

    @NotNull
    private Long itemId;

    @NotNull
    private LocalDateTime start;

    @NotNull
    private LocalDateTime end;
    private Status status;

    public Booking toBooking() {
        Booking booking = new Booking();
        booking.setId(id);
        booking.setStatus(status);
        booking.setStart(start);
        booking.setEnd(end);
        return booking;
    }
}
