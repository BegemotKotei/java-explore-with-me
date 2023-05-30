package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.EndpointHit;
import ru.practicum.dto.ViewStats;
import ru.practicum.exception.BadRequest;
import ru.practicum.model.Hit;
import ru.practicum.model.HitMapper;
import ru.practicum.repository.StatisticRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatisticServiceImpl implements StatisticService {

    private final StatisticRepository statisticRepository;

    @Override
    public List<ViewStats> getStatistic(Map<String, String> params, Set<String> uris) {
        boolean isUnique = params.containsKey("unique") ? Boolean.parseBoolean(params.get("unique")) : false;
        uris = uris == null ? new HashSet<>() : uris;
        if (stringToLocalDate(params.get("start")).isAfter(stringToLocalDate(params.get("end")))) {
            throw new BadRequest("Некорректно заданы даты начала и конца. Start не может быть после end.");
        }
        if (uris.size() > 0 && !isUnique) {
            return statisticRepository.getUrisViewsFromSet(uris, stringToLocalDate(params.get("start")),
                    stringToLocalDate(params.get("end")));
        } else if (uris.size() > 0 && isUnique) {
            return statisticRepository.getUrisViewsFromSetUnique(uris, stringToLocalDate(params.get("start")),
                    stringToLocalDate(params.get("end")));
        } else if (isUnique) {
            return statisticRepository.getUrisViewsUnique(stringToLocalDate(params.get("start")),
                    stringToLocalDate(params.get("end")));
        } else {
            return statisticRepository.getUrisViews(stringToLocalDate(params.get("start")),
                    stringToLocalDate(params.get("end")));
        }
    }

    @Override
    @Transactional
    public EndpointHit save(EndpointHit endpointHit) {
        Hit save = HitMapper.INSTANT.toHit(endpointHit);
        statisticRepository.save(save);
        return HitMapper.INSTANT.toEndpointHit(save);
    }

    private LocalDateTime stringToLocalDate(String date) {
        return LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

}