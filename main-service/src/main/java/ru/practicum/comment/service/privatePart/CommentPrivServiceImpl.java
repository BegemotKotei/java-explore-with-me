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
import ru.practicum.users.repository.UsersRepository;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentPrivServiceImpl implements CommentPrivService {

    private final CommentRepository commentRepository;

    private final CommentUtils commentUtils;

    private final EventUtils eventUtils;

    private final UsersRepository usersRepository;

    @Override
    @Transactional
    public CommentDto addComment(Long userId, Long eventId, NewCommentDto newCommentDto) {
        log.info("A user with ID = {} leaves a comment on the event with ID = {}.", userId, eventId);
        Event event = eventUtils.getEventById(eventId);
        User user = usersRepository.getUserById(userId);
        commentUtils.checkCanUserAddComment(user, event);
        newCommentDto.setUserId(userId);
        newCommentDto.setEventId(eventId);
        Comment comment = commentRepository.save(CommentMapper.INSTANT.toComment(newCommentDto, user, event));
        log.debug("A user with ID = {} left a comment on the event with ID = {}.", userId, eventId);
        return CommentMapper.INSTANT.toCommentDto(comment);
    }

    @Override
    @Transactional
    public CommentDto updateComment(Long userId, Long commentId, UpdateCommentDto updateCommentDto) {
        log.info("A user with ID = {} updates a comment with ID = {}.", userId, commentId);
        commentUtils.checkCommentIsPresent(commentId);
        usersRepository.checkIsUserPresent(userId);
        Comment commentForUpdate = commentUtils.getCommentById(commentId);
        commentUtils.checkIfUserIsOwnerComment(commentForUpdate, userId);
        commentForUpdate.setComment(updateCommentDto.getComment());
        log.debug("A user with ID = {} updated a comment with ID = {}.", userId, commentId);
        return CommentMapper.INSTANT.toCommentDto(
                commentRepository.save(commentForUpdate));
    }

    @Override
    @Transactional
    public void deleteComment(Long userId, Long commentId) {
        log.info("A user with ID = {} deletes a comment with ID = {}.", userId, commentId);
        commentUtils.checkCommentIsPresent(commentId);
        usersRepository.checkIsUserPresent(userId);
        commentUtils.checkIfUserIsOwnerComment(commentUtils.getCommentById(commentId), userId);
        commentRepository.deleteById(commentId);
        log.debug("A user with ID = {} deleted a comment with ID = {}.", userId, commentId);
    }

    @Override
    public CommentDto getCommentById(Long userId, Long commentId) {
        log.info("A user with ID = {} uploads his comment with ID = {}.", userId, commentId);
        commentUtils.checkCommentIsPresent(commentId);
        usersRepository.checkIsUserPresent(userId);
        return CommentMapper.INSTANT.toCommentDto(
                commentUtils.getCommentById(commentId));
    }

    @Override
    public List<CommentDto> getAllComments(Long userId, PageRequest pageable) {
        log.info("A user with ID = {} uploads his comments.", userId);
        usersRepository.checkIsUserPresent(userId);
        Page<Comment> pageComment = commentRepository.findAllByUserId(userId, pageable);
        List<Comment> comments = pageComment.getContent();
        return CommentMapper.INSTANT.toCommentsDto(comments);
    }

}