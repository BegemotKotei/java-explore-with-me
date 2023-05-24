package ru.practicum.stats_server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.stats_common.constant.Constants;
import ru.practicum.stats_common.model.EndpointHit;
import ru.practicum.stats_common.model.ViewStats;
import ru.practicum.stats_server.mapper.StatsMapper;
import ru.practicum.stats_server.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatsServiceImpl implements StatsService {

    private final StatsRepository statsRepository;
    private final StatsMapper mapper;

    @Override
    public void createHit(EndpointHit endpointHit) {
        log.info("StatsService - AddHit: {}", endpointHit);

        LocalDateTime timeStamp = LocalDateTime.parse(endpointHit.getTimestamp(), Constants.DATE_FORMATTER);
        statsRepository.save(mapper.toStats(endpointHit, timeStamp));
    }

    @Override
    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        log.info("StatsService - getStats: start={}, end={}, uris={}, unique={}", start, end, uris, unique);

        if (uris == null || uris.isEmpty()) {
            return unique ?
                    statsRepository.getStatsForUniqueIp(start, end) :
                    statsRepository.getAllStats(start, end);
        } else {
            return unique ?
                    statsRepository.getStatsByUrisForUniqueIp(start, end, uris) :
                    statsRepository.getStatsByUris(start, end, uris);
        }
    }

}
