package ru.practicum.comment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.dto.UpdateCommentDto;
import ru.practicum.comment.service.privatePart.CommentPrivService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(value = "/user/{userId}")
@RequiredArgsConstructor
@Validated
public class CommentPrivController {

    private final CommentPrivService commentPrivService;

    @PostMapping("event/{eventId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto addComment(
            @Positive @PathVariable Long userId,
            @Positive @PathVariable Long eventId,
            @Valid @RequestBody NewCommentDto newCommentDto) {
        return commentPrivService.addComment(userId, eventId, newCommentDto);
    }

    @PatchMapping("comments/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto updateComment(
            @Positive @PathVariable Long userId,
            @Positive @PathVariable Long commentId,
            @Valid @RequestBody UpdateCommentDto updateCommentDto) {
        return commentPrivService.updateComment(userId, commentId, updateCommentDto);
    }

    @DeleteMapping("comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(
            @Positive @PathVariable Long userId,
            @Positive @PathVariable Long commentId) {
        commentPrivService.deleteComment(userId, commentId);
    }

    @GetMapping("comments/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto getCommentById(
            @Positive @PathVariable Long userId,
            @Positive @PathVariable Long commentId) {
        return commentPrivService.getCommentById(userId, commentId);
    }

    @GetMapping("comments")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> getAllComments(
            @Positive @PathVariable Long userId,
            @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") Integer from,
            @PositiveOrZero @RequestParam(value = "size", defaultValue = "20") Integer size) {
        return commentPrivService.getAllComments(userId, PageRequest.of(from / size, size));
    }

}