package ru.practicum.event.service.privatePart;


import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventRequest;
import ru.practicum.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.ParticipationRequestDto;

import java.util.List;

public interface EventPrivService {

    EventFullDto createEvent(Long userId, NewEventDto newEvent);

    EventFullDto updateEvent(Long userId, Long eventId, UpdateEventRequest updateEventByUser);

    EventFullDto getFullEventById(Long userId, Long eventId);

    List<EventShortDto> getAllUsersEvents(Integer from, Integer size, Long userId);

    List<ParticipationRequestDto> getRequestsOnEvent(Long userId, Long eventId);

    EventRequestStatusUpdateResult processWithEventsRequests(Long userId, Long eventId,
                                                             EventRequestStatusUpdateRequest requests);

}