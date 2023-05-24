package ru.practicum.main_service.compilation.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.main_service.event.dto.EventShortDto;

import java.util.List;

@Getter
@Setter
public class CompilationDto {
    private Long id;
    private String title;
    private Boolean pinned;
    private List<EventShortDto> events;
}
