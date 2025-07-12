package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.TinyBookingDtoOut;

import java.util.Collection;

@Data
@Builder
public class ItemDtoExtOut {

    private Long id;
    private String name;
    private String description;
    private Long ownerId;
    private Boolean available;
    private TinyBookingDtoOut lastBooking;
    private TinyBookingDtoOut nextBooking;
    private Collection<CommentDtoOut> comments;
}
