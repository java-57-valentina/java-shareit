package ru.practicum.shareit.booking.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class BookingResponseDto {

    private Long id;
    private UserDto booker;
    private ItemDtoOut item;
    private Status status;
    private LocalDateTime start;
    private LocalDateTime end;
}
