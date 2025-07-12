package ru.practicum.shareit.item.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoOut;
import ru.practicum.shareit.item.model.Comment;

@UtilityClass
public class CommentMapper {

    public Comment toComment(CommentDto dto) {
        Comment comment = new Comment();
        comment.setText(dto.getText());
        return comment;
    }

    public static CommentDtoOut toResponseDto(Comment saved) {
        return CommentDtoOut.builder()
                .id(saved.getId())
                .text(saved.getText())
                .authorName(saved.getAuthor().getName())
                .itemId(saved.getItem().getId())
                .created(saved.getCreated())
                .build();

    }
}
