package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentDtoOut {

    private Long id;
    private String text;
    private String authorName;
    private Long itemId;
    private LocalDateTime created;
}
