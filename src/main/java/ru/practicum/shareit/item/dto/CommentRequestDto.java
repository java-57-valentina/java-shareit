package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.shareit.item.model.Comment;

@Data
public class CommentRequestDto {

    @NotNull
    private String text;

    public Comment toComment() {
        Comment comment = new Comment();
        comment.setText(text);
        return comment;
    }
}
