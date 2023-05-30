package ru.practicum.event.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import ru.practicum.event.enums.EventState;
import ru.practicum.event.model.Event;

import java.util.List;


@Repository(value = "dbEventRepository")
public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {

    @Query(value =
            "SELECT * " +
            "FROM events AS e " +
            "WHERE e.category_id = ?1 " +
            "LIMIT 1", nativeQuery = true)
    Event findFirstByCategory(Long catId);

    @Query(value =
            "SELECT * " +
            "FROM events AS e " +
            "WHERE e.initiator_id = ?1 ", nativeQuery = true)
    Page<Event> getAllEventsByUserId(Long userId, PageRequest pageRequest);

    Event findFirstByIdAndState(Long evenId, EventState state);

    List<Event> getByIdIn(List<Long> events);

}