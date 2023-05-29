package ru.practicum.request.service;


import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.model.Request;

import java.util.List;

public interface RequestService {

    ParticipationRequestDto createRequest(Long userId, Long eventId);

    List<ParticipationRequestDto> getAllUsersRequests(Long userId);

    ParticipationRequestDto cancelRequestByRequester(Long userId, Long requestId);

    Request getRequestById(Long requestId);

}