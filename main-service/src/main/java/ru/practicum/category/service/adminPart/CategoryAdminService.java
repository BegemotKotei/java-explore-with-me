package ru.practicum.category.service.adminPart;

import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;

public interface CategoryAdminService {

    CategoryDto createCategory(NewCategoryDto newCategory);

    CategoryDto patchCategoryById(Long catId, NewCategoryDto updateCategory);

    void deleteCategory(Long catId);

}