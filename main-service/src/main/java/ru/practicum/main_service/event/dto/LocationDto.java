package ru.practicum.main_service.event.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class LocationDto {

    @NotNull
    private Float lat;

    @NotNull
    private Float lon;

}
