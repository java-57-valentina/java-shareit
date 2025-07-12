package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemDtoOut {

    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long ownerId;
}
