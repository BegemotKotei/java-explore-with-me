package ru.practicum.event.service.privatePart;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ApiError.exception.BadRequestException;
import ru.practicum.ApiError.exception.ConflictException;
import ru.practicum.ApiError.exception.NotFoundException;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.model.CategoryMapper;
import ru.practicum.category.utils.CategoryUtils;
import ru.practicum.client.Client;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventRequest;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventMapper;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.event.utils.EventUtils;
import ru.practicum.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.enums.RequestStatus;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.RequestMapper;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.users.model.User;
import ru.practicum.users.repository.UsersRepository;

import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventPrivServiceImpl implements EventPrivService {

    private final EventRepository eventRepository;

    private final UsersRepository usersRepository;

    private final RequestRepository requestRepository;

    private final CategoryUtils categoryUtils;

    private final EventUtils eventUtils;

    private final Client client;

    @Override
    @Transactional
    public EventFullDto createEvent(Long userId, NewEventDto newEvent) {
        log.info("A user with ID = {} creates an event \"{}\".", userId, newEvent.getTitle());
        eventUtils.checkIfEvenDateCorrect(newEvent.getEventDate());
        User user = usersRepository.getUserById(userId);
        CategoryDto categoryDto = categoryUtils.getCategoryById(newEvent.getCategory());
        Event event = EventMapper.INSTANT.toEvent(newEvent);
        event.setInitiator(user);
        event.setCategory(CategoryMapper.INSTANT.categoryDtoToCategory(categoryDto));
        eventRepository.save(event);
        log.debug("A user with ID = {} created an event \"{}\". ID = {}.",
                userId, newEvent.getTitle(), event.getId());
        return client.setViewsEventFullDto(
                EventMapper.INSTANT.toEventFullDto(event));
    }

    @Override
    @Transactional
    public EventFullDto updateEvent(Long userId, Long eventId, UpdateEventRequest updateEvent) {
        if (updateEvent.getCategory() != null) {
            categoryUtils.checkCategoryPresent(updateEvent.getCategory());
        }
        Event eventForUpdate = eventUtils.getEventById(eventId);
        log.info("A user with ID = {} updates the event with ID = {}.", userId, eventId);
        User user = usersRepository.getUserById(userId);
        eventUtils.checkIfEventCanBeUpdated(updateEvent, eventForUpdate, user);
        log.debug("User with ID = {} updated the event with ID = {}.", userId, eventId);
        return client.setViewsEventFullDto(
                EventMapper.INSTANT.toEventFullDto(
                        eventRepository.save(
                                eventUtils.updateEvent(eventForUpdate, updateEvent, false))));
    }

    @Override
    public EventFullDto getFullEventById(Long userId, Long eventId) {
        log.info("A user with ID = {} requested information about an event with ID = {}.", userId, eventId);
        usersRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User with ID = " + userId + " not found.")
        );
        return client.setViewsEventFullDto(
                EventMapper.INSTANT.toEventFullDto(
                        eventUtils.getEventById(eventId)));
    }

    @Override
    public List<EventShortDto> getAllUsersEvents(Integer from, Integer size, Long userId) {
        Integer page = from / size;
        PageRequest pageRequest = PageRequest.of(page, size);
        log.info("Uploading a list of events for a user with ID = {} with parameters: size={}, from={}.", userId, size, page);
        Page<Event> pageEvents = eventRepository.getAllEventsByUserId(userId, pageRequest);
        List<Event> requests = pageEvents.getContent();
        List<EventShortDto> requestsDto = EventMapper.INSTANT.toEventShortDto(requests);
        return client.setViewsEventShortDtoList(requestsDto);
    }

    @Override
    public List<ParticipationRequestDto> getRequestsOnEvent(Long userId, Long eventId) {
        log.info("Uploading a list of requests to participate in an event with an ID = {}.", eventId);
        usersRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User with ID = " + userId + " not found.")
        );
        Event event = eventUtils.getEventById(eventId);
        if (!event.getInitiator().getId().equals(userId)) {
            throw new BadRequestException("Only the organizer can view the list of participation requests.");
        } else {
            List<Request> request = requestRepository.findAllByEventId(eventId);
            return RequestMapper.INSTANT.toParticipationRequestDto(request);
        }
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult processWithEventsRequests(
            Long userId, Long eventId, EventRequestStatusUpdateRequest requests) {
        log.info("Use with ID = {} processes event requests with ID = {}.", userId, eventId);
        usersRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User with ID = " + userId + " not found.")
        );
        Event event = eventUtils.getEventById(eventId);
        if (!event.getInitiator().getId().equals(userId)) {
            throw new BadRequestException("Only the organizer can process the list of participation requests.");
        }
        if (!event.getRequestModeration()) {
            throw new BadRequestException("Requests do not require moderation. Pre-moderation is disabled.");
        }
        if (event.getParticipantLimit() == 0) {
            throw new BadRequestException("Requests do not require moderation. There is no limit on participants.");
        }
        if (event.getParticipantLimit() == event.getParticipants().size()) {
            throw new ConflictException("There are no empty seats.");
        }
        EventRequestStatusUpdateResult eventRequestStatusUpdateResult = new EventRequestStatusUpdateResult();
        List<Request> requestsList = requestRepository.findAllByIdInAndStatus(
                requests.getRequestIds(), RequestStatus.PENDING);
        if (requests.getStatus().equals(RequestStatus.CONFIRMED)) {
            int freePlaces = event.getParticipantLimit() - event.getParticipants().size();
            int count = 0;
            for (Request request : requestsList) {
                checkRequestBeforeUpdate(event, request);
                log.info("Processing a request with an ID = {}.", request.getId());
                if (freePlaces != count) {
                    request.setStatus(RequestStatus.CONFIRMED);
                    eventRequestStatusUpdateResult.getConfirmedRequests()
                            .add(RequestMapper.INSTANT.toParticipationRequestDto(request));
                    count++;
                } else {
                    request.setStatus(RequestStatus.REJECTED);
                    eventRequestStatusUpdateResult.getRejectedRequests()
                            .add(RequestMapper.INSTANT.toParticipationRequestDto(request));
                }
                log.debug("Request status from ID = {} to \"{}\".", request.getId(), request.getStatus());
            }
        } else {
            for (Request request : requestsList) {
                checkRequestBeforeUpdate(event, request);
                log.info("Processing a request with an ID = {}.", request.getId());
                request.setStatus(RequestStatus.REJECTED);
                eventRequestStatusUpdateResult.getRejectedRequests()
                        .add(RequestMapper.INSTANT.toParticipationRequestDto(request));
                log.debug("Request status from ID = {} to \"{}\".", request.getId(), RequestStatus.REJECTED);
            }
        }
        requestRepository.saveAll(requestsList);
        return eventRequestStatusUpdateResult;
    }

    void checkRequestBeforeUpdate(Event event, Request request) {
        if (!request.getEvent().getId().equals(event.getId())) {
            throw new BadRequestException("Request for another event.");
        }
        if (!request.getStatus().equals(RequestStatus.PENDING)) {
            throw new BadRequestException("The request status is different from PENDING.");
        }
    }

}