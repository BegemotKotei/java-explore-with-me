package ru.practicum.comment.service.adminPart;

import org.springframework.data.domain.Pageable;
import ru.practicum.comment.dto.CommentDto;

import java.util.List;

public interface CommentAdminService {

    void deleteComment(Long commentId);

    CommentDto getCommentById(Long commentId);

    List<CommentDto> getComments(Long userId, Long eventId, Pageable pageable);

}