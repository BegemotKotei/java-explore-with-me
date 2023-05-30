package ru.practicum.compilation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.service.adminPart.CompilationAdminService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;


@RestController
@RequestMapping(value = "/admin/compilations")
@RequiredArgsConstructor
@Validated
public class CompilationAdminController {

    private final CompilationAdminService compilationAdminService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto createEvent(
            @Valid @RequestBody NewCompilationDto newCompilation) {
        return compilationAdminService.createCompilation(newCompilation);
    }

    @PatchMapping("/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDto updateCompilationById(
            @Positive @PathVariable Long compId,
            @Valid @RequestBody UpdateCompilationRequest updatedCompilation) {
        return compilationAdminService.updateCompilationById(compId, updatedCompilation);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComplication(
            @PathVariable Long compId) {
        compilationAdminService.deleteComplication(compId);
    }

}