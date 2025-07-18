package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ItemDto {

    private Long requestId;
    private String name;
    private String description;
    private Boolean available;
}
