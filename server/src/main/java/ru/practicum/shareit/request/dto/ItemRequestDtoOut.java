package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestDtoOut {

    private Long id;
    private String description;
    private Long userId;

    @JsonProperty(value = "created")
    private LocalDateTime createdAt;
}
