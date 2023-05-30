package ru.practicum.validation;

import org.springframework.stereotype.Component;
import ru.practicum.dto.EndpointHit;
import ru.practicum.exception.BadRequest;

import java.util.regex.Pattern;


@Component("statisticValidation")
public class StatisticValidation {

    private Pattern ipPattern = Pattern.compile(
            "^(\\b25[0-5]|\\b2[0-4][0-9]|\\b[01]?[0-9][0-9]?)(\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)){3}$");

    private Pattern datePattern = Pattern.compile("^(20[23][0-9])-([0][1-9]|[1][012])-([12][0-9]|[3][01]|[0][1-9]) " +
            "([0][1-9]|[1][0-9]|[2][0-3]|[0][0])[:]([0-5][0-9])[:]([0-5][0-9])$");

    public void statisticDtoIsValid(EndpointHit endpointHit) {
        ipIsValid(endpointHit.getIp());
        dateIsValid(endpointHit.getTimestamp().toString());
    }

    public void ipIsValid(String ip) {
        if (!ipPattern.matcher(ip).matches()) {
            throw new BadRequest("Невалидный IP адрес.");
        }
    }

    public void dateIsValid(String date) {
        if (!datePattern.matcher(date).matches()) {
            throw new BadRequest("Невалидная дата. Формат: yyyy-MM-dd HH:mm:ss");
        }
    }

}