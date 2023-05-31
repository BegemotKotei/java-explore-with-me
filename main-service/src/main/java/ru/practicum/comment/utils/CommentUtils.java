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
        log.info("Getting a comment with ID = {}.", commentId);
        return commentRepository.findById(commentId).orElseThrow(
                () -> new NotFoundException("Comment with ID = " + commentId + " not found.")
        );
    }

    public void checkCommentIsPresent(Long commentId) {
        log.info("Getting a comment with ID = {}.", commentId);
        commentRepository.findById(commentId).orElseThrow(
                () -> new NotFoundException("Comment with ID = " + commentId + " not found.")
        );
    }

    public void checkCanUserAddComment(User user, Event event) {
        if (event.getInitiator().getId().equals(user.getId())) {
            throw new ConflictException("You cannot leave a comment on your event.");
        }
        if (!event.getParticipants().contains(user)) {
            throw new ConflictException("You cannot leave a comment on an event that you have not attended.");
        }
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("You cannot leave comments on unpublished events.");
        }
    }

    public void checkIfUserIsOwnerComment(Comment comment, Long userId) {
        if (!comment.getUser().getId().equals(userId)) {
            throw new ConflictException("Only the author of the comment can change or delete it.");
        }
    }

}