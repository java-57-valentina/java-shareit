package ru.practicum.shareit.responce.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.TinyItemDtoOut;
import ru.practicum.shareit.responce.model.ItemResponse;

@UtilityClass
public class ItemResponseMapper {
    public TinyItemDtoOut mapToTinyItemDtoOut(ItemResponse response) {
        return TinyItemDtoOut.builder()
                .id(response.getItem().getId())
                .name(response.getItem().getName())
                .ownerId(response.getItem().getOwnerId()).build();
    }
}
