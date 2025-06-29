package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.model.Comment;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentResponseDto {

    private Long id;
    private String text;
    private String authorName;
    private Long itemId;
    private LocalDateTime created;

    public static CommentResponseDto from(Comment saved) {
        return CommentResponseDto.builder()
                .id(saved.getId())
                .text(saved.getText())
                .authorName(saved.getAuthor().getName())
                .itemId(saved.getItem().getId())
                .created(saved.getCreated())
                        .build();

    }
}
