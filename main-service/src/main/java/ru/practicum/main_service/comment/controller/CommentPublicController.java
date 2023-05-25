package ru.practicum.main_service.comment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.main_service.comment.dto.CommentDto;
import ru.practicum.main_service.comment.service.CommentService;
import ru.practicum.main_service.constant.Constants;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
@Validated
public class CommentPublicController {

    private final CommentService commentService;

    @GetMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto getCommentByPublic(@PathVariable Long commentId) {
        return commentService.getCommentByPublic(commentId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> getCommentsByPublic(
            @RequestParam Long eventId,
            @PositiveOrZero @RequestParam(defaultValue = Constants.PAGE_FROM) Integer from,
            @Positive @RequestParam(defaultValue = Constants.PAGE_SIZE) Integer size) {
        return commentService.getCommentsByPublic(eventId, PageRequest.of(from / size, size));
    }

}
