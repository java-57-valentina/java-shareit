package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.item.dto.TinyItemDtoOut;

import java.time.LocalDateTime;
import java.util.Collection;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestExtDtoOut {

    private Long id;
    private String description;
    private Long userId;

    @JsonProperty(value = "created")
    private LocalDateTime createdAt;

    Collection<TinyItemDtoOut> items;
}
