package ru.practicum.event.service.adminPart;


import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.UpdateEventRequest;
import ru.practicum.event.enums.EventState;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface EventAdminService {

    List<EventFullDto> getAllEventsByAdmin(Set<Long> users, Set<EventState> states, Set<Long> categories,
                                           LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size);

    EventFullDto updateEvent(Long eventId, UpdateEventRequest updateEventByUser);

}