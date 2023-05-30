package ru.practicum.event.service.privatePart;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ApiError.exception.BadRequestException;
import ru.practicum.ApiError.exception.ConflictException;
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
import ru.practicum.users.service.UsersService;

import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventPrivServiceImpl implements EventPrivService {

    private final EventRepository eventRepository;


    private final UsersService usersService;


    private final RequestRepository requestRepository;

    private final CategoryUtils categoryUtils;

    private final EventUtils eventUtils;

    private final Client client;

    @Override
    @Transactional
    public EventFullDto createEvent(Long userId, NewEventDto newEvent) {
        log.info("Пользователь с ID = {} создает мероприятие \"{}\".", userId, newEvent.getTitle());
        eventUtils.checkIfEvenDateCorrect(newEvent.getEventDate());
        User user = usersService.getUserById(userId);
        CategoryDto categoryDto = categoryUtils.getCategoryById(newEvent.getCategory());
        Event event = EventMapper.INSTANT.toEvent(newEvent);
        event.setInitiator(user);
        event.setCategory(CategoryMapper.INSTANT.categoryDtoToCategory(categoryDto));
        eventRepository.save(event);
        log.debug("Пользователь с ID = {} создал мероприятие \"{}\". ID = {}.",
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
        log.info("Пользователь с ID = {} обновляет мероприятие с ID = {}.", userId, eventId);
        User user = usersService.getUserById(userId);
        eventUtils.checkIfEventCanBeUpdated(updateEvent, eventForUpdate, user);
        log.debug("Пользователь с ID = {} обновил мероприятие с ID = {}.", userId, eventId);
        return client.setViewsEventFullDto(
                EventMapper.INSTANT.toEventFullDto(
                        eventRepository.save(
                                eventUtils.updateEvent(eventForUpdate, updateEvent, false))));
    }

    @Override
    public EventFullDto getFullEventById(Long userId, Long eventId) {
        log.info("Пользователь с ID = {} запросил информации о мероприятии с ID = {}.", userId, eventId);
        usersService.checkIsUserPresent(userId);
        return client.setViewsEventFullDto(
                EventMapper.INSTANT.toEventFullDto(
                        eventUtils.getEventById(eventId)));
    }

    @Override
    public List<EventShortDto> getAllUsersEvents(Integer from, Integer size, Long userId) {
        Integer page = from / size;
        PageRequest pageRequest = PageRequest.of(page, size);
        log.info("Выгрузка списка мероприятий для пользователя с ID = {} с параметрами: size={}, from={}.",userId, size, page);
        Page<Event> pageEvents = eventRepository.getAllEventsByUserId(userId, pageRequest);
        List<Event> requests = pageEvents.getContent();
        List<EventShortDto> requestsDto = EventMapper.INSTANT.toEventShortDto(requests);
        return client.setViewsEventShortDtoList(requestsDto);
    }

    @Override
    public List<ParticipationRequestDto> getRequestsOnEvent(Long userId, Long eventId) {
        log.info("Выгрузка списка запросов на участие в мероприятии с ID = {}.", eventId);
        usersService.checkIsUserPresent(userId);
        Event event = eventUtils.getEventById(eventId);
        if (!event.getInitiator().getId().equals(userId)) {
            throw new BadRequestException("Только организатор может просматривать список запросов на участие.");
        } else {
            List<Request> request = requestRepository.findAllByEventId(eventId);
            return RequestMapper.INSTANT.toParticipationRequestDto(request);
        }
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult processWithEventsRequests(
            Long userId, Long eventId, EventRequestStatusUpdateRequest requests) {
        log.info("Пользовать с ID = {} обрабатывает заявки на мероприятие с ID = {}.", userId, eventId);
        usersService.checkIsUserPresent(userId);
        Event event = eventUtils.getEventById(eventId);
        if (!event.getInitiator().getId().equals(userId)) {
            throw new BadRequestException("Только организатор может обрабатывать список запросов на участие.");
        }
        if (!event.getRequestModeration()) {
            throw new BadRequestException("Запросы не требуют модерации. Пре-модерация отключена.");
        }
        if (event.getParticipantLimit() == 0) {
            throw new BadRequestException("Запросы не требуют модерации. Лимит на участников не установлен.");
        }
        if (event.getParticipantLimit() == event.getParticipants().size()) {
            throw new ConflictException("Свободных мест нет.");
        }
        EventRequestStatusUpdateResult eventRequestStatusUpdateResult = new EventRequestStatusUpdateResult();
        List<Request> requestsList = requestRepository.findAllByIdInAndStatus(
                requests.getRequestIds(), RequestStatus.PENDING);
        if (requests.getStatus().equals(RequestStatus.CONFIRMED)) {
            int freePlaces = event.getParticipantLimit() - event.getParticipants().size();
            int count = 0;
            for (Request request: requestsList) {
                checkRequestBeforeUpdate(event, request);
                log.info("Обработка запроса с ID = {}.", request.getId());
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
                log.debug("Статус запроса с ID = {} на \"{}\".", request.getId(), request.getStatus());
            }
        } else {
            for (Request request: requestsList) {
                checkRequestBeforeUpdate(event, request);
                log.info("Обработка запроса с ID = {}.", request.getId());
                request.setStatus(RequestStatus.REJECTED);
                eventRequestStatusUpdateResult.getRejectedRequests()
                        .add(RequestMapper.INSTANT.toParticipationRequestDto(request));
                log.debug("Статус запроса с ID = {} на \"{}\".", request.getId(), RequestStatus.REJECTED);
            }
        }
        requestRepository.saveAll(requestsList);
        return eventRequestStatusUpdateResult;
    }

    void checkRequestBeforeUpdate(Event event, Request request) {
        if (!request.getEvent().getId().equals(event.getId())) {
            throw new BadRequestException("Запрос для другого мероприятия.");
        }
        if (!request.getStatus().equals(RequestStatus.PENDING)) {
            throw new BadRequestException("Статус запроса отличен от PENDING.");
        }
    }

}