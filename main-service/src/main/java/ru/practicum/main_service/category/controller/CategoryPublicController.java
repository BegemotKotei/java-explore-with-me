package ru.practicum.main_service.category.controller;

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
import ru.practicum.main_service.category.dto.CategoryDto;
import ru.practicum.main_service.category.service.CategoryService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
@Validated
public class CategoryPublicController {
    private final CategoryService categoryService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<CategoryDto>> getAllCategoryDto(
            @PositiveOrZero @RequestParam(defaultValue = Constants.PAGE_FROM) Integer from,
            @Positive @RequestParam(defaultValue = Constants.PAGE_SIZE) Integer size) {
        return ResponseEntity.ok().body(categoryService.getAllCategoryDto(PageRequest.of(from / size, size)));
    }

    @GetMapping("/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CategoryDto> getCategoryDtoById(@PathVariable Long catId) {
        return ResponseEntity.ok().body(categoryService.getCategoryDtoById(catId));
    }
}
