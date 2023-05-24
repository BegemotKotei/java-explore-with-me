package ru.practicum.main_service.event.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.main_service.event.enums.EventState;
import ru.practicum.main_service.event.model.Event;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventCustomRepository {

    List<Event> getEventsByAdmin(List<Long> users, List<EventState> states, List<Long> categories,
                                 LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size);

    List<Event> getEventsByPublic(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                  LocalDateTime rangeEnd, Integer from, Integer size);

}
