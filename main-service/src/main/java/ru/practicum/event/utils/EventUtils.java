package ru.practicum.event.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.ApiError.exception.BadRequestException;
import ru.practicum.ApiError.exception.ConflictException;
import ru.practicum.ApiError.exception.NotFoundException;
import ru.practicum.category.utils.CategoryUtils;
import ru.practicum.event.dto.UpdateEventRequest;
import ru.practicum.event.enums.EventState;
import ru.practicum.event.enums.EventStateAction;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.users.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class EventUtils {

    private final EventRepository eventRepository;

    private final CategoryUtils categoryUtils;

    public void checkIfEvenDateCorrect(LocalDateTime evenDate) {
        if (LocalDateTime.now().plusHours(2).isAfter(evenDate)) {
            log.error("Incorrect start date of the event. (Less than 2 hours before the start).");
            throw new BadRequestException("Incorrect start date of the event. (Less than 2 hours before the start).");
        }
    }

    public void checkIfEventCanBeUpdated(UpdateEventRequest updatedEven, Event oldEvent, User user) {
        if (!oldEvent.getInitiator().getId().equals(user.getId())) {
            log.error("Only the initiator or the administrator can change the event.");
            throw new BadRequestException("Only the initiator or the administrator can change the event.");
        }
        if (updatedEven.getEventDate() != null) {
            checkIfEvenDateCorrect(updatedEven.getEventDate());
        }
        if (oldEvent.getState().equals(EventState.PUBLISHED)) {
            log.error("Only events with PENDING or CANCELLED status can be changed.");
            throw new ConflictException("Only events with PENDING or CANCELLED status can be changed.");
        }
    }

    public void setEventStateByEventStateAction(Event event, EventStateAction eventStateAction) {
        switch (eventStateAction) {
            case PUBLISH_EVENT:
                event.setState(EventState.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
                break;
            case SEND_TO_REVIEW:
                event.setState(EventState.PENDING);
                break;
            default:
                event.setState(EventState.CANCELED);
                break;
        }
    }

    public Event getEventById(Long eventId) {
        log.info("Getting an event by ID = {}.", eventId);
        return eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Event with ID = " + eventId + " not found.")
        );
    }

    public void checkEventIsPresent(Long eventId) {
        eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Event with ID = " + eventId + " not found.")
        );
    }

    public Event updateEvent(Event updatedEvent, UpdateEventRequest updateEventRequest, Boolean isAdmin) {
        Optional.ofNullable(updateEventRequest.getAnnotation()).ifPresent(updatedEvent::setAnnotation);
        Optional.ofNullable(updateEventRequest.getCategory()).ifPresent(
                c -> updatedEvent.setCategory(categoryUtils.getCategoryModelById(c)));
        Optional.ofNullable(updateEventRequest.getDescription()).ifPresent(updatedEvent::setDescription);
        Optional.ofNullable(updateEventRequest.getEventDate()).ifPresent(updatedEvent::setEventDate);
        if (updateEventRequest.getLocation() != null) {
            if (updateEventRequest.getLocation().getLat() != null) {
                updatedEvent.setLat(updateEventRequest.getLocation().getLat());
            }
            if (updateEventRequest.getLocation().getLon() != null) {
                updatedEvent.setLon(updateEventRequest.getLocation().getLon());
            }
        }
        Optional.ofNullable(updateEventRequest.getPaid()).ifPresent(updatedEvent::setPaid);
        Optional.ofNullable(updateEventRequest.getParticipantLimit()).ifPresent(updatedEvent::setParticipantLimit);
        Optional.ofNullable(updateEventRequest.getRequestModeration()).ifPresent(updatedEvent::setRequestModeration);
        if (isAdmin) {
            if (updateEventRequest.getStateAction() != null) {
                if (updatedEvent.getState().equals(EventState.PENDING)) {
                    setEventStateByEventStateAction(updatedEvent, updateEventRequest.getStateAction());
                } else {
                    throw new ConflictException("Event with ID = " + updatedEvent.getId() + " already published/canceled.");
                }
            }
        } else {
            Optional.ofNullable(updateEventRequest.getStateAction()).ifPresent(
                    s -> setEventStateByEventStateAction(updatedEvent, updateEventRequest.getStateAction())
            );
        }
        Optional.ofNullable(updateEventRequest.getTitle()).ifPresent(updatedEvent::setTitle);
        return updatedEvent;
    }

    public List<Event> getEventByIds(List<Long> events) {
        log.info("Uploading a list of events by ID list.");
        return eventRepository.getByIdIn(events);
    }

}