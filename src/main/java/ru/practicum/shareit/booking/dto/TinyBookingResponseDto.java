package ru.practicum.shareit.booking.dto;

import jakarta.annotation.Nullable;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class TinyBookingResponseDto {

    private Long id;

    private Long bookerId;
    private Long itemId;
    private Status status;

    private LocalDateTime start;
    private LocalDateTime end;

    public static TinyBookingResponseDto from(@Nullable Booking booking) {
        if (booking == null)
            return null;

        TinyBookingResponseDto bookingDto = new TinyBookingResponseDto();
        bookingDto.setId(booking.getId());
        bookingDto.setItemId(booking.getItem().getId());
        bookingDto.setBookerId(booking.getBooker().getId());
        bookingDto.setStatus(booking.getStatus());
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());
        return bookingDto;
    }
}
