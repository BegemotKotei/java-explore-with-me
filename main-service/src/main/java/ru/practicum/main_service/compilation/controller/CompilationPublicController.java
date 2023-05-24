package ru.practicum.main_service.compilation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.main_service.constant.Constants;
import ru.practicum.main_service.compilation.dto.CompilationDto;
import ru.practicum.main_service.compilation.service.CompilationService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/compilations")
@Validated
public class CompilationPublicController {

    private final CompilationService compilationService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<CompilationDto>> getAllCompilationDto(
            @RequestParam(required = false) Boolean pinned,
            @PositiveOrZero @RequestParam(defaultValue = Constants.PAGE_FROM) Integer from,
            @Positive @RequestParam(defaultValue = Constants.PAGE_SIZE) Integer size) {
        return ResponseEntity.ok().body(compilationService.getAllCompilationDto(pinned, PageRequest.of(from / size, size)));
    }

    @GetMapping("/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CompilationDto> getCompilationDtoById(@PathVariable Long compId) {
        return ResponseEntity.ok().body(compilationService.getCompilationDtoById(compId));
    }

}
