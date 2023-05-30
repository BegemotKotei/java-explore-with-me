package ru.practicum.event.model;

import ru.practicum.category.dto.CategoryDto;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.location.LocationDto;
import ru.practicum.event.enums.EventState;
import ru.practicum.users.model.UserMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public enum EventMapper {
    INSTANT;

    public EventShortDto toEventShortDto(Event event) {
        Integer confirmRequests = Optional.ofNullable(event.getParticipants())
                .orElse(new ArrayList<>()).size();
        return EventShortDto.builder()
                .id(event.getId())
                .eventDate(event.getEventDate())
                .annotation(event.getAnnotation())
                .title(event.getTitle())
                .paid(event.getPaid())
                .confirmedRequests(confirmRequests)
                .category(CategoryDto.builder()
                        .id(event.getCategory().getId())
                        .name(event.getCategory().getName())
                        .build())
                .initiator(UserMapper.INSTANT.toUserShortDto(event.getInitiator()))
                .views(null)
                .build();
    }

    public EventFullDto toEventFullDto(Event event) {
        return EventFullDto.builder()
                .id(event.getId())
                .eventDate(event.getEventDate())
                .annotation(event.getAnnotation())
                .description(event.getDescription())
                .requestModeration(event.getRequestModeration())
                .title(event.getTitle())
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .confirmedRequests(Optional.ofNullable(event.getParticipants())
                        .orElse(new ArrayList<>()).size())
                .state(event.getState())
                .publishedOn(event.getPublishedOn())
                .createdOn(event.getCreatedOn())
                .category(CategoryDto.builder()
                        .id(event.getCategory().getId())
                        .name(event.getCategory().getName())
                        .build())
                .location(LocationDto.builder()
                        .lat(event.getLat())
                        .lon(event.getLon())
                        .build())
                .initiator(UserMapper.INSTANT.toUserShortDto(event.getInitiator()))
                .views(null)
                .build();
    }

    public List<EventShortDto> toEventShortDto(List<Event> events) {
        return events.stream().map(EventMapper.INSTANT::toEventShortDto).collect(Collectors.toList());
    }

    public Event toEvent(NewEventDto newEventDto) {
        return Event.builder()
                .annotation(newEventDto.getAnnotation())
                .description(newEventDto.getDescription())
                .eventDate(newEventDto.getEventDate())
                .paid(newEventDto.getPaid())
                .participantLimit(newEventDto.getParticipantLimit())
                .requestModeration(newEventDto.getRequestModeration())
                .title(newEventDto.getTitle())
                .state(EventState.PENDING)
                .lat(newEventDto.getLocation().getLat())
                .lon(newEventDto.getLocation().getLon())
                .build();
    }

    public List<EventFullDto> iterableToList(Iterable<Event> events) {
        List<EventFullDto> dtos = new ArrayList<>();
        for (Event event: events) {
            dtos.add(toEventFullDto(event));
        }
        return dtos;
    }

}