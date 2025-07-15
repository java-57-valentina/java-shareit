package ru.practicum.shareit.item.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoExtOut;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.dto.TinyItemDtoOut;
import ru.practicum.shareit.item.model.Item;

@UtilityClass
public class ItemMapper {

    public Item toItem(ItemDto dto) {
        return Item.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .available(dto.getAvailable())
                .build();
    }

    public ItemDtoOut toDto(Item item) {
        return ItemDtoOut.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .ownerId(item.getOwnerId())
                .build();
    }

    public ItemDtoExtOut toExtDto(Item item) {
        return ItemDtoExtOut.builder()
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
