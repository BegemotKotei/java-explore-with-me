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
import ru.practicum.users.repository.UsersRepository;

import java.time.LocalDateTime;
import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;

    private final UsersRepository usersRepository;


    private final EventUtils eventUtils;


    @Override
    @Transactional
    public ParticipationRequestDto createRequest(Long userId, Long eventId) {
        log.info("Creating a request to participate in an event with ID = {} from a user with ID = {}.", eventId, userId);
        Event event = eventUtils.getEventById(eventId);
        User user = usersRepository.getUserById(userId);
        Boolean isUnlimited = event.getParticipantLimit().equals(0);
        checkUserAndEvent(user, event, isUnlimited);
        Request participantsRequests = requestRepository.findFirstByRequesterIdAndEventId(userId, eventId);
        if (participantsRequests != null) {
            log.error("A participation request from a user with ID = {} already exists.", userId);
            throw new ConflictException("You have already sent a request to participate.");
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
        log.debug("A request to participate in an event with ID = {} from a user with ID = {} was created under ID = {}.",
                eventId, userId, request.getId());
        return RequestMapper.INSTANT.toParticipationRequestDto(request);
    }

    @Override
    public List<ParticipationRequestDto> getAllUsersRequests(Long userId) {
        log.info("A user with ID = {} has requested their applications to participate in events.", userId);
        usersRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User with ID = " + userId + " not found.")
        );
        return RequestMapper.INSTANT.toParticipationRequestDto(
                requestRepository.findAllByRequesterId(userId));
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelRequestByRequester(Long userId, Long requestId) {
        log.info("The user with ID = {} cancels the participation request with ID = {}.", userId, requestId);
        usersRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User with ID = " + userId + " not found.")
        );
        Request request = getRequestById(requestId);
        if (!request.getRequester().getId().equals(userId)) {
            log.error("Attempt to cancel someone else's registration at the event by a user with ID = {}.", userId);
            throw new BadRequestException("You cannot cancel someone else's application.");
        }
        request.setStatus(RequestStatus.CANCELED);
        return RequestMapper.INSTANT.toParticipationRequestDto(request);
    }

    public Request getRequestById(Long requestId) {
        return requestRepository.findById(requestId).orElseThrow(
                () -> new NotFoundException("Request for participation with ID = " + requestId + " not found.")
        );
    }

    private void checkUserAndEvent(User user, Event event, Boolean isUnlimited) {
        if (event.getInitiator().getId().equals(user.getId())) {
            log.error("An attempt to register in your own event. User with ID = {}, event with ID = {}.",
                    user.getId(), event.getId());
            throw new ConflictException("The organizer does not need to register to participate in the event.");
        }
        if (!event.getState().equals(EventState.PUBLISHED)) {
            log.error("The event with ID = {} has not been published yet. Current status: \"{}\".",
                    event.getId(), event.getState());
            throw new ConflictException("Registration is only possible in published events.");
        }
        if (!isUnlimited) {
            if (requestRepository.getByEventIdAndStatus(event.getId(), RequestStatus.CONFIRMED).size() ==
                    event.getParticipantLimit()) {
                log.error("There are no available seats for the event with ID = {} no.", event.getId());
                throw new ConflictException("There are no available seats for the event.");
            }
        }
    }

}