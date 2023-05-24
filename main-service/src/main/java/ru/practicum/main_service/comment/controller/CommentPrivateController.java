package ru.practicum.main_service.comment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.main_service.comment.dto.CommentDto;
import ru.practicum.main_service.comment.dto.NewCommentDto;
import ru.practicum.main_service.comment.service.CommentService;
import ru.practicum.main_service.constant.Constants;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/comments")
@Validated
public class CommentPrivateController {

    private final CommentService commentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<CommentDto> createByPrivate(@PathVariable Long userId,
                                      @RequestParam Long eventId,
                                      @Valid @RequestBody NewCommentDto newCommentDto) {
        return ResponseEntity.ok().body(commentService.createCommentByPrivate(userId, eventId, newCommentDto));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<CommentDto>> getCommentsByPrivate(
            @PathVariable Long userId,
            @RequestParam(required = false) Long eventId,
            @PositiveOrZero @RequestParam(defaultValue = Constants.PAGE_FROM) Integer from,
            @Positive @RequestParam(defaultValue = Constants.PAGE_SIZE) Integer size) {
        return ResponseEntity.ok().body(commentService.getCommentsByPrivate(userId, eventId, PageRequest.of(from / size, size)));
    }

    @PatchMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CommentDto> updateByPrivate(@PathVariable Long userId,
                                      @PathVariable Long commentId,
                                      @Valid @RequestBody NewCommentDto newCommentDto) {
        return ResponseEntity.ok().body(commentService.updateCommentByPrivate(userId, commentId, newCommentDto));
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteByPrivate(@PathVariable Long userId,
                                @PathVariable Long commentId) {
        commentService.deleteCommentByPrivate(userId, commentId);
    }

}
