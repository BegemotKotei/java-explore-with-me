package ru.practicum.compilation.service.publicPart;


import io.micrometer.core.lang.Nullable;
import ru.practicum.compilation.dto.CompilationDto;

import java.util.List;

public interface CompilationPubService {

    CompilationDto getCompilationByIdPublic(Long compId);

    List<CompilationDto> getComplicationsPublic(@Nullable Boolean pinned, Integer from, Integer size);

}