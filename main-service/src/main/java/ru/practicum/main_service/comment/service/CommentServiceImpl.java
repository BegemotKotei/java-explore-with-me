package ru.practicum.main_service.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.main_service.comment.dto.NewCommentDto;
import ru.practicum.main_service.comment.dto.CommentDto;
import ru.practicum.main_service.comment.mapper.CommentMapper;
import ru.practicum.main_service.comment.model.Comment;
import ru.practicum.main_service.comment.repository.CommentRepository;
import ru.practicum.main_service.event.enums.EventState;
import ru.practicum.main_service.event.model.Event;
import ru.practicum.main_service.event.service.EventService;
import ru.practicum.main_service.exception.ForbiddenException;
import ru.practicum.main_service.exception.NotFoundException;
import ru.practicum.main_service.user.model.User;
import ru.practicum.main_service.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final UserService userService;
    private final EventService eventService;
    private final CommentRepository commentRepository;
    private final CommentMapper mapper;

    @Override
    public List<CommentDto> getCommentsByAdmin(Pageable pageable) {
        log.info("MainService - getCommentsByAdmin");

        return toCommentsDto(commentRepository.findAll(pageable).toList());
    }

    @Override
    public void deleteCommentByAdmin(Long commentId) {
        log.info("MainService - deleteCommentByAdmin: commentId={}", commentId);

        checkCommentInBase(commentId);

        commentRepository.deleteById(commentId);
    }

    @Override
    public CommentDto createCommentByPrivate(Long userId, Long eventId, NewCommentDto newCommentDto) {
        log.info("MainService - createCommentByPrivate: userId={}, eventId={}, NewCommentDto={}",
                eventId, userId, newCommentDto);

        User user = userService.getUserById(userId);
        Event event = eventService.getEventById(eventId);
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ForbiddenException("Impossible to add comment if event state isn't PUBLISHED");
        }

        Comment comment = Comment.builder()
                .text(newCommentDto.getText())
                .author(user)
                .event(event)
                .created(LocalDateTime.now())
                .build();

        return mapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    public List<CommentDto> getCommentsByPrivate(Long userId, Long eventId, Pageable pageable) {
        log.info("MainService - getCommentsByPrivate: userId={}, eventId={}, pageable={}",
                userId, eventId, pageable);

        userService.checkUserInBase(userId);
        List<Comment> comments;

        if (eventId == null) {
            comments = commentRepository.findAllByAuthorId(userId);
        } else {
            eventService.checkEventInBase(eventId);
            comments = commentRepository.findAllByAuthorIdAndEventId(userId, eventId);
        }

        return toCommentsDto(comments);
    }

    @Override
    public CommentDto updateCommentByPrivate(Long userId, Long commentId, NewCommentDto newCommentDto) {
        log.info("MainService - updateCommentByPrivate: userId={}, commentId={}, newCommentDto={}",
                commentId, userId, newCommentDto);

        userService.checkUserInBase(userId);
        Comment commentFromRepository = getCommentById(commentId);
        checkUserIsCommentAuthor(userId, commentFromRepository.getAuthor().getId());

        commentFromRepository.setText(newCommentDto.getText());
        commentFromRepository.setEdited(LocalDateTime.now());

        return mapper.toCommentDto(commentRepository.save(commentFromRepository));
    }

    @Override
    public void deleteCommentByPrivate(Long userId, Long commentId) {
        log.info("MainService - deleteCommentByPrivate: userId={}, commentId={}", userId, commentId);

        userService.checkUserInBase(userId);
        checkUserIsCommentAuthor(userId, getCommentById(commentId).getAuthor().getId());

        commentRepository.deleteById(commentId);
    }

    @Override
    public List<CommentDto> getCommentsByPublic(Long eventId, Pageable pageable) {
        log.info("MainService - getCommentsByPublic: eventId={}, pageable={}", eventId, pageable);

        eventService.checkEventInBase(eventId);

        return toCommentsDto(commentRepository.findAllByEventId(eventId, pageable));
    }

    @Override
    public CommentDto getCommentByPublic(Long commentId) {
        log.info("MainService - getCommentByPublic: commentId={}", commentId);

        return mapper.toCommentDto(getCommentById(commentId));
    }

    @Override
    public void checkCommentInBase(Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new NotFoundException("Comment with ID " + commentId + " doesn't exist");
        }
    }

    private List<CommentDto> toCommentsDto(List<Comment> comments) {
        return comments.stream()
                .map(mapper::toCommentDto)
                .collect(Collectors.toList());
    }

    private Comment getCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment with ID " + commentId + " doesn't exist"));
    }

    private void checkUserIsCommentAuthor(Long userId, Long authorId) {
        if (!Objects.equals(userId, authorId)) {
            throw new ForbiddenException("User with ID " + userId + " isn't author of comment");
        }
    }

}
