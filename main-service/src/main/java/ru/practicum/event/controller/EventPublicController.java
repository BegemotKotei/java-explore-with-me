package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ApiError.exception.BadRequestException;
import ru.practicum.DateConstants;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.enums.EventSort;
import ru.practicum.event.service.publicPart.EventPubService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventPublicController {

    private final EventPubService eventService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getEventsPubic(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) Set<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = DateConstants.DATE_PATTERN) LocalDateTime rangeStart,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = DateConstants.DATE_PATTERN) LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(defaultValue = "EVENT_DATE") EventSort sort,
            @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(value = "size", defaultValue = "10") Integer size,
            HttpServletRequest request) {
        categories = categories == null ? new HashSet<>() : categories;
        rangeStart = rangeStart == null ? LocalDateTime.now() : rangeStart;
        rangeEnd = rangeEnd == null ? rangeStart.plusYears(100) : rangeEnd;
        if (rangeStart.isAfter(rangeEnd)) {
            throw new BadRequestException("Неправильно задан временной отрезок. (rangeStart is after rangeEnd).");
        }
        return eventService.getEventsByPublic(
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size, request);
    }

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getEventByIdPubic(
            @Positive @PathVariable Long eventId, HttpServletRequest request) {
        return eventService.getEventByIdPubic(eventId, request);
    }

}