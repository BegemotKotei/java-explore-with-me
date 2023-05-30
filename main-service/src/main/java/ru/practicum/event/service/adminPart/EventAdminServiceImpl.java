package ru.practicum.event.service.adminPart;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.utils.CategoryUtils;
import ru.practicum.client.Client;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.UpdateEventRequest;
import ru.practicum.event.enums.EventState;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventMapper;
import ru.practicum.event.model.QEvent;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.event.utils.EventUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventAdminServiceImpl implements EventAdminService {

    private final EventRepository eventRepository;

    private final CategoryUtils categoryUtils;

    private final EventUtils eventUtils;

    private final Client client;

    @Override
    public List<EventFullDto> getAllEventsByAdmin(
            Set<Long> users, Set<EventState> states, Set<Long> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size) {
        log.info("Выгрузка списка мероприятий администратором с параметрами: " +
                        "users = {}, sates = {}, categories = {}, rangeStart = {}, rangeEnd = {}, from = {}, size = {}.",
                users, states, categories, rangeStart, rangeEnd, from, size);
        BooleanExpression byUsers;
        BooleanExpression byStates;
        BooleanExpression byCategories;
        BooleanExpression byDate = QEvent.event.eventDate.between(rangeStart, rangeEnd);
        if (users.isEmpty()) {
            byUsers = QEvent.event.initiator.id.notIn(users);
        } else {
            byUsers = QEvent.event.initiator.id.in(users);
        }
        if (states.isEmpty()) {
            byStates = QEvent.event.state.notIn(states);
        } else {
            byStates = QEvent.event.state.in(states);
        }
        if (categories.isEmpty()) {
            byCategories = QEvent.event.category.id.notIn(categories);
        } else {
            byCategories = QEvent.event.category.id.in(categories);
        }
        int page = from / size;
        PageRequest pageRequest = PageRequest.of(page, size);
        Iterable<Event> foundEvents = eventRepository.findAll(
                byUsers.and(byStates).and(byCategories).and(byDate), pageRequest);
        return client.setViewsEventFullDtoList(
                EventMapper.INSTANT.iterableToList(foundEvents));
    }

    @Override
    @Transactional
    public EventFullDto updateEvent(Long eventId, UpdateEventRequest updateEvent) {
        if (updateEvent.getCategory() != null) {
            categoryUtils.checkCategoryPresent(updateEvent.getCategory());
        }
        Event eventForUpdate = eventUtils.getEventById(eventId);
        log.info("Администратор обновляет мероприятие с ID = {}.", eventId);
        if (updateEvent.getEventDate() != null) {
            eventUtils.checkIfEvenDateCorrect(updateEvent.getEventDate());
        }
        log.debug("Администратор обновил мероприятие с ID = {}.", eventId);
        return client.setViewsEventFullDto(
                EventMapper.INSTANT.toEventFullDto(
                        eventRepository.save(
                                eventUtils.updateEvent(eventForUpdate, updateEvent, true))));
    }

}