package ru.practicum.category.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.service.adminPart.CategoryAdminService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@RestController
@RequestMapping(path = "/admin/categories")
@RequiredArgsConstructor
@Validated
public class CategoryAdminController {

    private final CategoryAdminService categoryAdminService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto createCategory(
            @Valid @RequestBody NewCategoryDto newCategory) {
        return categoryAdminService.createCategory(newCategory);
    }

    @PatchMapping("/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto patchCategoryById(
            @Valid @RequestBody NewCategoryDto updatedCategory,
            @Positive @PathVariable Long catId) {
        return categoryAdminService.patchCategoryById(catId, updatedCategory);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(
            @Positive @PathVariable Long catId) {
        categoryAdminService.deleteCategory(catId);
    }
}