package ru.practicum.category.service.adminPart;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ApiError.exception.ConflictException;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.model.Category;
import ru.practicum.category.model.CategoryMapper;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.category.utils.CategoryUtils;


@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class CategoryAdminServiceImpl implements CategoryAdminService {

    private final CategoryRepository categoryRepository;

    private final CategoryUtils categoryUtils;

    @Override
    public CategoryDto createCategory(NewCategoryDto newCategory) {
        log.info("Создание новой категории: {}.", newCategory.getName());
        categoryUtils.checkCategoryNameIsBusy(newCategory.getName());
        Category category = categoryRepository.save(
                CategoryMapper.INSTANT.newCategoryDtoToCategory(newCategory));
        log.debug("Категория создана. ID = {}.", category.getId());
        return CategoryMapper.INSTANT.toCategoryDto(category);
    }

    @Override
    public CategoryDto patchCategoryById(Long catId, NewCategoryDto updatedCategory) {
        log.info("Обновление категории с ID = {}.", catId);
        categoryUtils.checkCategoryPresent(catId);
        Category categoryById = categoryRepository.getCategoryById(catId);
        Category categoryByName = categoryRepository.findFirstByName(updatedCategory.getName());
        if (categoryByName != null) {
            if (!categoryByName.getId().equals(categoryById.getId())) {
                throw new ConflictException("Категория уже существует.");
            }
        }
        categoryById.setName(updatedCategory.getName());
        categoryRepository.save(categoryById);
        log.debug("Категория с ID = {} обновлена.", catId);
        return CategoryMapper.INSTANT.toCategoryDto(categoryById);
    }

    @Override
    public void deleteCategory(Long catId) {
        log.info("Удаление категории с ID = {}.", catId);
        categoryUtils.checkCategoryPresent(catId);
        categoryUtils.checkCategoryUsing(catId);
        categoryRepository.deleteById(catId);
        log.debug("Категории с ID = {} удалена.", catId);
    }

}