package ru.practicum.comment.service.privatePart;

import org.springframework.data.domain.PageRequest;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.dto.UpdateCommentDto;

import java.util.List;

public interface CommentPrivService {
    CommentDto addComment(Long userId, Long eventId, NewCommentDto newCommentDto);

    CommentDto updateComment(Long userId, Long commentId, UpdateCommentDto updateCommentDto);

    void deleteComment(Long userId, Long commentId);

    CommentDto getCommentById(Long userId, Long commentId);

    List<CommentDto> getAllComments(Long userId, PageRequest of);

}