package ru.practicum.comment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.service.adminPart.CommentAdminService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(value = "/admin/comments")
@RequiredArgsConstructor
public class CommentAdminController {

    private final CommentAdminService commentAdminService;


    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(
            @Positive @PathVariable Long commentId) {
        commentAdminService.deleteComment(commentId);
    }

    @GetMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto getCommentById(
            @Positive @PathVariable Long commentId) {
        return commentAdminService.getCommentById(commentId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> getComments(
            @Positive @RequestParam(value = "userId", required = false) Long userId,
            @Positive @RequestParam(value = "eventId", required = false) Long eventId,
            @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") Integer from,
            @PositiveOrZero @RequestParam(value = "size", defaultValue = "20") Integer size) {
        return commentAdminService.getComments(userId, eventId, PageRequest.of(from / size, size));
    }

}