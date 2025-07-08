package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.user.validation.Create;

@Data
@AllArgsConstructor
public class ItemDto {

    private Long id;

    private Long requestId;

    @NotBlank (groups = Create.class)
    private String name;

    @NotBlank (groups = Create.class)
    private String description;

    @NotNull (groups = Create.class)
    private Boolean available;
}
