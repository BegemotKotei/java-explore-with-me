package ru.practicum.comment.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.ApiError.exception.ConflictException;
import ru.practicum.ApiError.exception.NotFoundException;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.repository.CommentRepository;
import ru.practicum.event.enums.EventState;
import ru.practicum.event.model.Event;
import ru.practicum.users.model.User;


@Component
@RequiredArgsConstructor
@Slf4j
public class CommentUtils {

    private final CommentRepository commentRepository;

    public Comment getCommentById(Long commentId) {
        log.info("Получение комментария с ID = {}.", commentId);
        return commentRepository.findById(commentId).orElseThrow(
                () -> new NotFoundException("Комментарий с ID = " + commentId + " не найден.")
        );
    }

    public void checkCommentIsPresent(Long commentId) {
        log.info("Получение комментария с ID = {}.", commentId);
        commentRepository.findById(commentId).orElseThrow(
                () -> new NotFoundException("Комментарий с ID = " + commentId + " не найден.")
        );
    }

    public void checkCanUserAddComment(User user, Event event) {
        if (event.getInitiator().getId().equals(user.getId())) {
            throw new ConflictException("Нельзя оставлять комментарий на своё мероприятие.");
        }
        if (!event.getParticipants().contains(user)) {
            throw new ConflictException("Нельзя оставлять комментарий на мероприятие, которое вы не посещали.");
        }
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Нельзя оставлять комментарий к неопубликованным мероприятиям.");
        }
        /* Восстановить проверку после сдачи проекта
        if (!event.getState().equals(EventState.PUBLISHED) || event.getEventDate().isAfter(LocalDateTime.now())) {
            throw new ConflictException("Нельзя оставлять комментарий к неопубликованным или не прошедшим мероприятиям.");
        }*/
    }

    public void checkIfUserIsOwnerComment(Comment comment, Long userId) {
        if (!comment.getUser().getId().equals(userId)) {
            throw new ConflictException("Только автор комментария может его менять или удалять.");
        }
    }

}