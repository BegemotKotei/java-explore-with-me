package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.DateConstants;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.UpdateEventRequest;
import ru.practicum.event.enums.EventState;
import ru.practicum.event.service.adminPart.EventAdminService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
public class EventAdminController {

    private final EventAdminService eventService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventFullDto> getAllUsersEvents(
            @RequestParam(required = false) Set<Long> users,
            @RequestParam(required = false) Set<EventState> states,
            @RequestParam(required = false) Set<Long> categories,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = DateConstants.DATE_PATTERN) LocalDateTime rangeStart,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = DateConstants.DATE_PATTERN) LocalDateTime rangeEnd,
            @PositiveOrZero @RequestParam(value = "from", defaultValue = "1") Integer from,
            @Positive @RequestParam(value = "size", defaultValue = "10") Integer size) {
        users = users == null ? new HashSet<>() : users;
        states = states == null ? new HashSet<>() : states;
        categories = categories == null ? new HashSet<>() : categories;
        rangeStart = rangeStart == null ? LocalDateTime.now() : rangeStart;
        rangeEnd = rangeEnd == null ? rangeStart.plusYears(100) : rangeEnd;
        return eventService.getAllEventsByAdmin(
                users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEventByAdmin(
            @Positive @PathVariable Long eventId,
            @Valid @RequestBody UpdateEventRequest updatedEventByAdmin) {
        return eventService.updateEvent(eventId, updatedEventByAdmin);
    }

}