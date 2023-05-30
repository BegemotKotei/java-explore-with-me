package ru.practicum.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ApiError.exception.BadRequestException;
import ru.practicum.ApiError.exception.ConflictException;
import ru.practicum.ApiError.exception.NotFoundException;
import ru.practicum.event.enums.EventState;
import ru.practicum.event.model.Event;
import ru.practicum.event.utils.EventUtils;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.enums.RequestStatus;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.RequestMapper;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.users.model.User;
import ru.practicum.users.service.UsersService;

import java.time.LocalDateTime;
import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;

    private final UsersService usersService;


    private final EventUtils eventUtils;


    @Override
    @Transactional
    public ParticipationRequestDto createRequest(Long userId, Long eventId) {
        log.info("Создание запроса на участие в мероприятии с ID = {} от пользователя с ID = {}.", eventId, userId);
        Event event = eventUtils.getEventById(eventId);
        User user = usersService.getUserById(userId);
        Boolean isUnlimited = event.getParticipantLimit().equals(0);
        checkUserAndEvent(user, event, isUnlimited);
        Request participantsRequests = requestRepository.findFirstByRequesterIdAndEventId(userId, eventId);
        if (participantsRequests != null) {
            log.error("Запрос на участие от пользователя с ID = {} уже существует.", userId);
            throw new ConflictException("Вы уже отправляли запрос на участие.");
        }
        RequestStatus requestStatus = RequestStatus.CONFIRMED;
        if (event.getRequestModeration() && !event.getParticipantLimit().equals(0)) {
            requestStatus = RequestStatus.PENDING;
        }
        Request request = requestRepository.save(Request
                .builder()
                .created(LocalDateTime.now())
                .requester(user)
                .status(requestStatus)
                .event(event)
                .build());
        log.debug("Запрос на участие в мероприятии с ID = {} от пользователя с ID = {} создан под ID = {}.",
                eventId, userId, request.getId());
        return RequestMapper.INSTANT.toParticipationRequestDto(request);
    }

    @Override
    public List<ParticipationRequestDto> getAllUsersRequests(Long userId) {
        log.info("Пользователь с ID = {} запросил свои заявки на участия в мероприятиях.", userId);
        usersService.checkIsUserPresent(userId);
        return RequestMapper.INSTANT.toParticipationRequestDto(
                requestRepository.findAllByRequesterId(userId));
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelRequestByRequester(Long userId, Long requestId) {
        log.info("Пользователь с ID = {} отменяет запрос на участие с ID = {}.", userId, requestId);
        usersService.checkIsUserPresent(userId);
        Request request = getRequestById(requestId);
        if (!request.getRequester().getId().equals(userId)) {
            log.error("Попытка отмены чужой регистрации на мероприятии пользователем с ID = {}.", userId);
            throw new BadRequestException("Вы не можете отменить чужую заявку.");
        }
        request.setStatus(RequestStatus.CANCELED);
        return RequestMapper.INSTANT.toParticipationRequestDto(request);
    }

    @Override
    public Request getRequestById(Long requestId) {
        return requestRepository.findById(requestId).orElseThrow(
                () -> new NotFoundException("Запрос на участие с ID = " + requestId + " не найден.")
        );
    }

    private void checkUserAndEvent(User user, Event event, Boolean isUnlimited) {
        if (event.getInitiator().getId().equals(user.getId())) {
            log.error("Попытка зарегистрироваться в своём же мероприятии. Пользователь с ID = {}, мероприятие с ID = {}.",
                    user.getId(), event.getId());
            throw new ConflictException("Организатору не нужно регистрироваться для участия в мероприятии.");
        }
        if (!event.getState().equals(EventState.PUBLISHED)) {
            log.error("Мероприятие с ID = {} ещё не опубликовано. Текущий статус: \"{}\".",
                    event.getId(), event.getState());
            throw new ConflictException("Регистрация возможна только в опубликованных мероприятиях.");
        }
        if (!isUnlimited) {
            if (requestRepository.getByEventIdAndStatus(event.getId(), RequestStatus.CONFIRMED).size() ==
                    event.getParticipantLimit()) {
                log.error("Нет свободных мест на мероприятие с ID = {} нет.", event.getId());
                throw new ConflictException("Нет свободных мест на мероприятие.");
            }
        }
    }

}