package ru.practicum.shareit.booking.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class BookingResponseDto {

    private Long id;

    private UserDto booker;
    private ItemDto item;
    private Status status;

    private LocalDateTime start;
    private LocalDateTime end;
}
