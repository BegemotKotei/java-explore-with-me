package ru.practicum.event.service.publicPart;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ApiError.exception.NotFoundException;
import ru.practicum.StatsClient;
import ru.practicum.client.Client;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.enums.EventSort;
import ru.practicum.event.enums.EventState;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventMapper;
import ru.practicum.event.model.QEvent;
import ru.practicum.event.repository.EventRepository;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;


@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventPubServiceImpl implements EventPubService {

    private final EventRepository eventRepository;

    private final StatsClient statisticClient;

    private final Client client;

    @Override
    public List<EventShortDto> getEventsByPublic(
            String text, Set<Long> categories, Boolean paid, LocalDateTime rangeStart, LocalDateTime rangeEnd,
            Boolean onlyAvailable, EventSort sort, Integer from, Integer size, HttpServletRequest request) {
        log.info("Выгрузка списка мероприятий. Публичный API с параметрами: " +
                        "text = {}, categories = {}, paid = {}, rangeStart = {}, rangeEnd = {}, onlyAvailable = {}, " +
                        "sort = {}, from = {}, size = {}.",
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
        BooleanExpression booleanExpression = QEvent.event.state.eq(EventState.PUBLISHED);
        if (text != null) {
            booleanExpression = booleanExpression.and(QEvent.event.description.containsIgnoreCase(text)
                    .or(QEvent.event.annotation.containsIgnoreCase(text)));
        }
        if (categories != null) {
            booleanExpression = booleanExpression.and(QEvent.event.category.id.in(categories));
        }
        if (paid != null) {
            booleanExpression = booleanExpression.and(QEvent.event.paid.eq(paid));
        }
        booleanExpression = booleanExpression.and(QEvent.event.eventDate.between(rangeStart, rangeEnd));
        Integer page = from / size;
        Pageable pageable = PageRequest.of(page, size);
        Page<Event> eventsPage;
        eventsPage = eventRepository.findAll(booleanExpression, pageable);
        List<Event> events = eventsPage.getContent();
        if (onlyAvailable) {
            events.removeIf(event -> event.getParticipants().size() == event.getParticipantLimit());
        }
        statisticClient.createHit(request.getRequestURI(), request.getRemoteAddr());
        return client.setViewsEventShortDtoList(
                EventMapper.INSTANT.toEventShortDto(events));
    }

    @Override
    public EventFullDto getEventByIdPubic(Long eventId, HttpServletRequest request) {
        Event event = eventRepository.findFirstByIdAndState(eventId, EventState.PUBLISHED);
        if (event != null) {
            statisticClient.createHit(request.getRequestURI(), request.getRemoteAddr());
            return client.setViewsEventFullDto(
                    EventMapper.INSTANT.toEventFullDto(event));
        } else {
            throw new NotFoundException("Мероприятие с ID = " + eventId + " не найдено.");
        }
    }

}