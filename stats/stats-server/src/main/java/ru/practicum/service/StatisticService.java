package ru.practicum.service;

import ru.practicum.dto.EndpointHit;
import ru.practicum.dto.ViewStats;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface StatisticService {

    EndpointHit save(EndpointHit endpointHit);

    List<ViewStats> getStatistic(Map<String, String> params, Set<String> uris);

}