package ru.practicum.comment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.service.publicPart.CommentPubService;

import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping(value = "/event/{eventId}/comments")
@RequiredArgsConstructor
public class CommentPubController {

    private final CommentPubService commentPubService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> getCommentByEventId(
            @Positive @PathVariable Long eventId) {
        return commentPubService.getCommentByEventId(eventId);
    }

}