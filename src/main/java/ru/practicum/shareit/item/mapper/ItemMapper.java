package ru.practicum.shareit.item.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.dto.TinyItemDtoOut;
import ru.practicum.shareit.item.model.Item;

@UtilityClass
public class ItemMapper {

    public Item toItem(ItemDto dto) {
        return Item.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .available(dto.getAvailable())
                // .ownerId(dto.getOwnerId())
                .build();
    }

    public ItemDtoOut toDto(Item item) {
        return ItemDtoOut.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .ownerId(item.getOwnerId()).build();
    }

    public ItemDtoOut toResponceDto(Item item) {
        return ItemDtoOut.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .ownerId(item.getOwnerId())
                .build();
    }

    public TinyItemDtoOut toTinyDto(Item item) {
        return TinyItemDtoOut.builder()
                .id(item.getId())
                .name(item.getName())
                .ownerId(item.getOwnerId())
                .build();
    }
}
