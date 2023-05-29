package ru.practicum.category.service.publicPart;

import ru.practicum.category.dto.CategoryDto;

import java.util.List;

public interface CategoryPubService {

    List<CategoryDto> getAllCategories(Integer from, Integer size);

    CategoryDto getCategoryById(Long catId);

}