package ru.practicum.compilation.model;

import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public enum CompilationMapper {
    INSTANT;

    public Compilation toCompilation(NewCompilationDto compilationDto, List<Event> events) {
        return Compilation.builder()
                .pinned(Optional.ofNullable(compilationDto.getPinned()).orElse(false))
                .title(compilationDto.getTitle())
                .events(events)
                .build();
    }

    public CompilationDto toCompilationDto(Compilation compilation) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .title(compilation.getTitle())
                .events(EventMapper.INSTANT.toEventShortDto(compilation.getEvents()))
                .pinned(compilation.getPinned())
                .build();
    }

    public Compilation toCompilation(Compilation compilationOld, List<Event> events) {
        return Compilation.builder()
                .id(compilationOld.getId())
                .pinned(Optional.ofNullable(compilationOld.getPinned()).orElse(false))
                .title(compilationOld.getTitle())
                .events(events)
                .build();
    }

    public List<CompilationDto> toCompilationDto(List<Compilation> compilations) {
        return compilations.stream()
                .map(CompilationMapper.INSTANT::toCompilationDto)
                .collect(Collectors.toList());
    }

}