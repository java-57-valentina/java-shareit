package ru.practicum.shareit.request.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestExtDtoOut;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;

@UtilityClass
public class RequestMapper {
    public static ItemRequestDtoOut toResponseDto(ItemRequest request) {
        ItemRequestDtoOut responseDto = new ItemRequestDtoOut();
        responseDto.setId(request.getId());
        responseDto.setUserId(request.getRequester().getId());
        responseDto.setCreatedAt(request.getCreatedAt());
        responseDto.setDescription(request.getDescription());
        return responseDto;
    }

    public static ItemRequestExtDtoOut toResponseExtDto(ItemRequest request) {
        ItemRequestExtDtoOut out = new ItemRequestExtDtoOut();
        out.setId(request.getId());
        out.setUserId(request.getRequester().getId());
        out.setCreatedAt(request.getCreatedAt());
        out.setDescription(request.getDescription());
        out.setItems(request.getItems().stream().map(ItemMapper::toTinyDto).toList());
        return out;
    }
}
