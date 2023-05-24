package ru.practicum.main_service.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.main_service.category.dto.CategoryDto;
import ru.practicum.main_service.category.dto.NewCategoryDto;
import ru.practicum.main_service.category.mapper.CategoryMapper;
import ru.practicum.main_service.category.model.Category;
import ru.practicum.main_service.category.repository.CategoryRepository;
import ru.practicum.main_service.exception.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper mapper;

    @Override
    public CategoryDto createCategory(NewCategoryDto newCategoryDto) {
        log.info("MainService - createCategory: newCategoryDto={}", newCategoryDto);

        return mapper.toCategoryDto(categoryRepository.save(mapper.newCategoryDtoToCategory(newCategoryDto)));
    }

    @Override
    public List<CategoryDto> getAllCategoryDto(Pageable pageable) {
        log.info("MainService - getAllCategoryDto: pageable={}", pageable);

        return categoryRepository.findAll(pageable)
                .stream()
                .map(mapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategoryDtoById(Long catId) {
        log.info("MainService - getCategoryDtoById: catId={}", catId);

        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Category with ID " + catId + " does not exist"));

        return mapper.toCategoryDto(category);
    }

    @Override
    public CategoryDto updateCategory(Long catId, CategoryDto categoryDto) {
        log.info("MainService - updateCategory: catId={}, categoryDto={}", catId, categoryDto);

        if (!categoryRepository.existsById(catId)) {
            throw new NotFoundException("Category with ID " + catId + " does not exist");
        }

        categoryDto.setId(catId);
        return mapper.toCategoryDto(categoryRepository.save(mapper.categoryDtoToCategory(categoryDto)));
    }

    @Override
    public void deleteCategoryById(Long catId) {
        log.info("MainService - deleteCategoryById: catId={}", catId);

        if (!categoryRepository.existsById(catId)) {
            throw new NotFoundException("Category with ID " + catId + " does not exist");
        }

        categoryRepository.deleteById(catId);
    }

    @Override
    public Category getCategoryById(Long catId) {
        log.info("MainService - getCategoryById: catId={}", catId);

        return categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Category with ID " + catId + " does not exist"));
    }

}
