package ru.practicum.comment.service.privatePart;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.dto.UpdateCommentDto;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.model.CommentMapper;
import ru.practicum.comment.repository.CommentRepository;
import ru.practicum.comment.utils.CommentUtils;
import ru.practicum.event.model.Event;
import ru.practicum.event.utils.EventUtils;
import ru.practicum.users.model.User;
import ru.practicum.users.service.UsersService;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentPrivServiceImpl implements CommentPrivService {

    private final CommentRepository commentRepository;

    private final CommentUtils commentUtils;

    private final EventUtils eventUtils;

    private final UsersService usersService;

    @Override
    @Transactional
    public CommentDto addComment(Long userId, Long eventId, NewCommentDto newCommentDto) {
        log.info("Пользователь с ID = {} оставляет комментарий к мероприятию с ID = {}.", userId, eventId);
        Event event = eventUtils.getEventById(eventId);
        User user = usersService.getUserById(userId);
        commentUtils.checkCanUserAddComment(user, event);
        newCommentDto.setUserId(userId);
        newCommentDto.setEventId(eventId);
        Comment comment = commentRepository.save(CommentMapper.INSTANT.toComment(newCommentDto, user, event));
        log.debug("Пользователь с ID = {} оставил комментарий к мероприятию с ID = {}.", userId, eventId);
        return CommentMapper.INSTANT.toCommentDto(comment);
    }

    @Override
    @Transactional
    public CommentDto updateComment(Long userId, Long commentId, UpdateCommentDto updateCommentDto) {
        log.info("Пользователь с ID = {} обновляет комментарий с ID = {}.", userId, commentId);
        commentUtils.checkCommentIsPresent(commentId);
        usersService.checkIsUserPresent(userId);
        Comment commentForUpdate = commentUtils.getCommentById(commentId);
        commentUtils.checkIfUserIsOwnerComment(commentForUpdate, userId);
        commentForUpdate.setComment(updateCommentDto.getComment());
        log.debug("Пользователь с ID = {} обновил комментарий с ID = {}.", userId, commentId);
        return CommentMapper.INSTANT.toCommentDto(
                commentRepository.save(commentForUpdate));
    }

    @Override
    @Transactional
    public void deleteComment(Long userId, Long commentId) {
        log.info("Пользователь с ID = {} удаляет комментарий с ID = {}.", userId, commentId);
        commentUtils.checkCommentIsPresent(commentId);
        usersService.checkIsUserPresent(userId);
        commentUtils.checkIfUserIsOwnerComment(commentUtils.getCommentById(commentId), userId);
        commentRepository.deleteById(commentId);
        log.debug("Пользователь с ID = {} удалил комментарий с ID = {}.", userId, commentId);
    }

    @Override
    public CommentDto getCommentById(Long userId, Long commentId) {
        log.info("Пользователь с ID = {} выгружает свой комментарий с ID = {}.", userId, commentId);
        commentUtils.checkCommentIsPresent(commentId);
        usersService.checkIsUserPresent(userId);
        return CommentMapper.INSTANT.toCommentDto(
                commentUtils.getCommentById(commentId));
    }

    @Override
    public List<CommentDto> getAllComments(Long userId, PageRequest pageable) {
        log.info("Пользователь с ID = {} выгружает свои комментарии.", userId);
        usersService.checkIsUserPresent(userId);
        Page<Comment> pageComment = commentRepository.findAllByUserId(userId, pageable);
        List<Comment> comments = pageComment.getContent();
        return CommentMapper.INSTANT.toCommentsDto(comments);
    }

}