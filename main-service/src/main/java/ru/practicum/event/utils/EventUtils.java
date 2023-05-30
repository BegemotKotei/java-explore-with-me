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
            log.error("Некорректная дата начала мероприятия. (Меньше 2-х часов до начала).");
            throw new BadRequestException("Некорректная дата начала мероприятия. (Меньше 2-х часов до начала).");
        }
    }

    public void checkIfEventCanBeUpdated(UpdateEventRequest updatedEven, Event oldEvent, User user) {
        if (!oldEvent.getInitiator().getId().equals(user.getId())) {
            log.error("Только инициатор или администратор могут менять мероприятие.");
            throw new BadRequestException("Только инициатор или администратор могут менять мероприятие.");
        }
        if (updatedEven.getEventDate() != null) {
            checkIfEvenDateCorrect(updatedEven.getEventDate());
        }
        if (oldEvent.getState().equals(EventState.PUBLISHED)) {
            log.error("Только мероприятия со статусом PENDING или CANCELED могут быть изменены.");
            throw new ConflictException("Только мероприятия со статусом PENDING или CANCELED могут быть изменены.");
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
        log.info("Получение мероприятия по ID = {}.", eventId);
        return eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Мероприятие с ID = " + eventId + " не найдено.")
        );
    }

    public void checkEventIsPresent(Long eventId) {
        eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Мероприятие с ID = " + eventId + " не найдено.")
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
                    throw new ConflictException("Мероприятие с ID = " + updatedEvent.getId() + " уже опубликовано/отменено.");
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
        log.info("Выгрузка списка мероприятий по списку ID.");
        return eventRepository.getByIdIn(events);
    }

}