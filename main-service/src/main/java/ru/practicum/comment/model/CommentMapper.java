package ru.practicum.comment.model;

import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.event.model.Event;
import ru.practicum.users.model.User;

import java.util.List;
import java.util.stream.Collectors;

public enum CommentMapper {
    INSTANT;

    public CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .eventId(comment.getEvent().getId())
                .userId(comment.getUser().getId())
                .comment(comment.getComment())
                .created(comment.getCreated())
                .build();
    }

    public List<CommentDto> toCommentsDto(List<Comment> comments) {
        return comments.stream().map(CommentMapper.INSTANT::toCommentDto).collect(Collectors.toList());
    }

    public Comment toComment(NewCommentDto newCommentDto, User user, Event event) {
        return Comment.builder()
                .event(event)
                .user(user)
                .comment(newCommentDto.getComment())
                .created(newCommentDto.getCreated())
                .build();
    }

}