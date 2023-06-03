package ru.practicum.comment.service.publicPart;

import ru.practicum.comment.dto.CommentDto;

import java.util.List;

public interface CommentPubService {
    List<CommentDto> getCommentByEventId(Long eventId);

}