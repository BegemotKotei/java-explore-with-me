package ru.practicum.stats_server.service;

import ru.practicum.stats_common.model.ViewStats;
import ru.practicum.stats_common.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {

    void createHit(EndpointHit endpointHit);

    List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);

}
