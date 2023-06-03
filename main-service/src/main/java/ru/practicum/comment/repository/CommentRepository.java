package ru.practicum.comment.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import ru.practicum.comment.model.Comment;

import java.util.List;

@Repository(value = "dbCommentRepository")
public interface CommentRepository extends JpaRepository<Comment, Long>, QuerydslPredicateExecutor<Comment> {

    List<Comment> findAllByEventId(Long eventId);

    Comment findFirstByUserIdAndEventId(Long userId, Long eventId);

    Page<Comment> findAllByUserId(Long userId, PageRequest pageable);

}