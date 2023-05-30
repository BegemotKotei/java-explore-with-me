package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EndpointHit;
import ru.practicum.dto.ViewStats;
import ru.practicum.service.StatisticService;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@Slf4j
public class StatisticController {

    private final StatisticService statisticService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public EndpointHit addHit(
            @RequestBody EndpointHit endpointHit) {
        log.info("Добавлен просмотр в статистику для URI: {}", endpointHit.getUri());
        return statisticService.save(endpointHit);
    }

    @GetMapping("/stats")
    @ResponseStatus(HttpStatus.OK)
    public List<ViewStats> getStatistic(
            @RequestParam(value = "start") String start,
            @RequestParam(value = "end") String end,
            @RequestParam(value = "unique", defaultValue = "false") String unique,
            @RequestParam(value = "uris", required = false) Set<String> uris) {

        log.info("Запрос статистики с параметрами: \n start={} \n end={} \n isUnique={} \n uris={}",
                start, end, unique, uris);
        Map<String, String> params = Map.of(
                "start", start,
                "end", end,
                "unique", unique);
        return statisticService.getStatistic(params, uris);
    }

}