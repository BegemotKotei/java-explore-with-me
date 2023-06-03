package ru.practicum.compilation.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.ApiError.exception.NotFoundException;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.repository.CompilationRepository;

@Component
@RequiredArgsConstructor
@Slf4j
public class CompilationUtils {

    private final CompilationRepository compilationRepository;

    public Compilation getCompilationById(Long compId) {
        log.info("Getting a collection by ID = {}.", compId);
        return compilationRepository.findById(compId).orElseThrow(
                () -> new NotFoundException("Collection with ID = " + compId + " not found.")
        );
    }

}
