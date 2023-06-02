package ru.practicum.event.service.publicPart;


import io.micrometer.core.lang.Nullable;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.enums.EventSort;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface EventPubService {

    List<EventShortDto> getEventsByPublic(String text, Set<Long> categories, @Nullable Boolean paid,
                                          LocalDateTime rangeStart, LocalDateTime rangeEnd, Boolean onlyAvailable,
                                          EventSort sort, Integer from, Integer size, HttpServletRequest request);

    EventFullDto getEventByIdPubic(Long eventId, HttpServletRequest request);

}