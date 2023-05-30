package ru.practicum.comment.service.publicPart;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.model.CommentMapper;
import ru.practicum.comment.repository.CommentRepository;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentPubServiceImpl implements CommentPubService {

    private final CommentRepository commentRepository;

    @Override
    public List<CommentDto> getCommentByEventId(Long eventId) {
        return CommentMapper.INSTANT.toCommentsDto(
                commentRepository.findAllByEventId(eventId));
    }

}