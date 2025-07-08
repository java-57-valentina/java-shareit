package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.TinyBookingResponseDto;

import java.util.Collection;

@Data
@Builder
public class ItemDtoOut {

    private Long id;
    private String name;
    private String description;
    private Long ownerId;
    private Boolean available;
    private TinyBookingResponseDto lastBooking;
    private TinyBookingResponseDto nextBooking;
    private Collection<CommentResponseDto> comments;
}
