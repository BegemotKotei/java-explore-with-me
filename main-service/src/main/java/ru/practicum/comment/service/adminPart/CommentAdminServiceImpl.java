package ru.practicum.comment.service.adminPart;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.model.CommentMapper;
import ru.practicum.comment.model.QComment;
import ru.practicum.comment.repository.CommentRepository;
import ru.practicum.comment.utils.CommentUtils;
import ru.practicum.event.utils.EventUtils;
import ru.practicum.users.service.UsersService;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentAdminServiceImpl implements CommentAdminService {

    private final UsersService usersService;

    private final EventUtils eventUtils;

    private final CommentRepository commentRepository;

    private final CommentUtils commentUtils;

    @Override
    @Transactional
    public void deleteComment(Long commentId) {
        log.info("Администратор удаляет комментарий с ID = {}.", commentId);
        commentUtils.checkCommentIsPresent(commentId);
        commentRepository.deleteById(commentId);
        log.debug("Администратор удалил комментарий с ID = {}.", commentId);
    }

    @Override
    public CommentDto getCommentById(Long commentId) {
        log.info("Администратор выгружает комментарий с ID = {}.", commentId);
        return CommentMapper.INSTANT.toCommentDto(
                commentUtils.getCommentById(commentId));
    }

    @Override
    public List<CommentDto> getComments(Long userId, Long eventId, Pageable pageable) {
        log.info("Администратор выгружает комментарий по параметрам: userId = {}, eventId = {}.", userId, eventId);
        List<Comment> comments;
        if (userId == null && eventId == null) {
            comments = commentRepository.findAll(pageable).getContent();
        } else if (userId != null && eventId != null) {
            BooleanExpression booleanExpression = QComment.comment1.user.id.eq(userId)
                    .and(QComment.comment1.event.id.eq(eventId));
            comments = commentRepository.findAll(booleanExpression, pageable).getContent();
        } else if (userId != null) {
            BooleanExpression booleanExpression = QComment.comment1.user.id.eq(userId);
            comments = commentRepository.findAll(booleanExpression, pageable).getContent();
        } else {
            BooleanExpression booleanExpression = QComment.comment1.event.id.eq(eventId);
            comments = commentRepository.findAll(booleanExpression, pageable).getContent();
        }
        return CommentMapper.INSTANT.toCommentsDto(comments);
    }

}