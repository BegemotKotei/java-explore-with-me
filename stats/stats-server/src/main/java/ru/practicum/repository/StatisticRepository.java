package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import ru.practicum.dto.ViewStats;
import ru.practicum.model.Hit;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Component("dbStatisticRepository")
public interface StatisticRepository  extends JpaRepository<Hit, Long> {

    //Просмотры всех uri (не уникальные)
    @Query("SELECT NEW ru.practicum.dto.ViewStats(s.app, s.uri, COUNT(s.ip)) " +
            "FROM Hit AS s " +
            "WHERE s.timestamp BETWEEN :start AND :end " +
            "GROUP BY s.ip, s.app, s.uri " +
            "ORDER BY COUNT(s.ip) DESC")
    List<ViewStats> getUrisViews(LocalDateTime start, LocalDateTime end);


    //Просмотры конкретных uri (не уникальные)
    @Query("SELECT NEW ru.practicum.dto.ViewStats(s.app, s.uri, COUNT(s.ip)) " +
            "FROM Hit AS s " +
            "WHERE s.timestamp BETWEEN :start AND :end " +
            "AND s.uri IN (:uris) " +
            "GROUP BY s.uri, s.ip, s.app " +
            "ORDER BY COUNT(s.ip) DESC")
    List<ViewStats> getUrisViewsFromSet(Set<String> uris, LocalDateTime start, LocalDateTime end);

    //Просмотры всех uri (уникальные)
    @Query("SELECT NEW ru.practicum.dto.ViewStats(s.app, s.uri, COUNT(DISTINCT s.ip)) " +
            "FROM Hit s " +
            "WHERE s.timestamp BETWEEN :start AND :end " +
            "GROUP BY s.uri, s.ip, s.app " +
            "ORDER BY COUNT(DISTINCT s.ip) DESC")
    List<ViewStats> getUrisViewsUnique(LocalDateTime start, LocalDateTime end);

    //Просмотры конкретных uri (уникальные)
    @Query("SELECT NEW ru.practicum.dto.ViewStats(s.app, s.uri, COUNT(DISTINCT s.ip)) " +
            "FROM Hit s " +
            "WHERE s.timestamp BETWEEN :start AND :end " +
            "AND s.uri IN (:uris) " +
            "GROUP BY s.uri, s.ip, s.app " +
            "ORDER BY COUNT(DISTINCT s.ip) DESC")
    List<ViewStats> getUrisViewsFromSetUnique(Set<String> uris, LocalDateTime start, LocalDateTime end);

}