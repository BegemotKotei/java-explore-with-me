package ru.practicum.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.service.RequestService;

import javax.validation.constraints.Positive;
import java.util.List;


@RestController
@RequestMapping(path = "/users/{userId}/requests")
@RequiredArgsConstructor
@Validated
public class RequestController {

    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createRequest(
            @Positive @PathVariable Long userId,
            @Positive @RequestParam(value = "eventId", required = true) Long eventId) {
        return requestService.createRequest(userId, eventId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getAllUsersRequests(
            @Positive @PathVariable Long userId) {
        return requestService.getAllUsersRequests(userId);
    }

    @PatchMapping("/{requestId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public ParticipationRequestDto cancelRequestByRequester(
            @Positive @PathVariable Long userId,
            @Positive @PathVariable Long requestId) {
        return requestService.cancelRequestByRequester(userId, requestId);
    }

}