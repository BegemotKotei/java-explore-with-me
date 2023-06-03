package ru.practicum.category.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.ApiError.exception.BadRequestException;
import ru.practicum.ApiError.exception.ConflictException;
import ru.practicum.ApiError.exception.NotFoundException;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.model.Category;
import ru.practicum.category.model.CategoryMapper;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.repository.EventRepository;

@Component
@RequiredArgsConstructor
@Slf4j
public class CategoryUtils {

    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    public Category getCategoryModelById(Long catId) {
        log.info("Getting a category by ID = {}.", catId);
        return categoryRepository.findById(catId).orElseThrow(
                () -> new NotFoundException("Category an ID = " + catId + " not found."));
    }

    public CategoryDto getCategoryById(Long catId) {
        log.info("Getting a category by ID = {}.", catId);
        return CategoryMapper.INSTANT.toCategoryDto(categoryRepository.findById(catId).orElseThrow(
                () -> new NotFoundException("Category an ID = " + catId + " not found.")
        ));
    }

    public void checkCategoryPresent(Long catId) {
        if (categoryRepository.getCategoryById(catId) == null) {
            log.error("Category c ID = {} does not exist.", catId);
            throw new BadRequestException("Category an ID = " + catId + " does not exist.");
        }
    }

    public void checkCategoryNameIsBusy(String name) {
        if (categoryRepository.findFirstByName(name) != null) {
            log.error("The category \"{}\" already exists.", name);
            throw new ConflictException("The category already exists.");
        }
    }

    public void checkCategoryUsing(Long catId) {
        if (eventRepository.findFirstByCategory(catId) != null) {
            log.error("Category c ID = {} is used and cannot be deleted.", catId);
            throw new ConflictException("Category an ID = " + catId + " used and cannot be deleted.");
        }
    }

    public Category getCategoryByName(String name) {
        return categoryRepository.findFirstByName(name);
    }

}