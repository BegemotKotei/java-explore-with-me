package ru.practicum.compilation.service.adminPart;


import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;

public interface CompilationAdminService {

    CompilationDto createCompilation(NewCompilationDto newCompilation);

    CompilationDto updateCompilationById(Long compId, UpdateCompilationRequest updatedCompilation);

    void deleteComplication(Long compId);

}