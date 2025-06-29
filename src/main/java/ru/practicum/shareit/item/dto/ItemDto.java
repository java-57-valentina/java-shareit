package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.validation.Create;

@Data
@AllArgsConstructor
public class ItemDto {

    private Long id;

    @NotBlank (groups = Create.class)
    private String name;

    @NotBlank (groups = Create.class)
    private String description;

    @NotNull (groups = Create.class)
    private Boolean available;

    private Long ownerId;


    public Item toItem() {
        return Item.builder()
                .id(getId())
                .name(getName())
                .description(getDescription())
                .available(getAvailable())
                .ownerId(getOwnerId())
                .build();
    }

    public static ItemDto fromItem(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwnerId()
        );
    }
}
