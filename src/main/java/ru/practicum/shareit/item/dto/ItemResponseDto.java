package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.TinyBookingResponseDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

@Data
@Builder
public class ItemResponseDto {

    private Long id;
    private String name;
    private String description;
    private Long ownerId;
    private Boolean available;
    private TinyBookingResponseDto lastBooking;
    private TinyBookingResponseDto nextBooking;
    private Collection<CommentResponseDto> comments;

    public static ItemResponseDto fromItem(Item item) {
        return ItemResponseDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .ownerId(item.getOwnerId())
                .build();
    }
}
