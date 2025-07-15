package ru.practicum.shareit.booking.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class BookingDto {

    private Long itemId;
    private LocalDateTime start;
    private LocalDateTime end;
    private Status status;
}
