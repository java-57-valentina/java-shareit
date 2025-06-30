package ru.practicum.shareit.booking.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
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
}
