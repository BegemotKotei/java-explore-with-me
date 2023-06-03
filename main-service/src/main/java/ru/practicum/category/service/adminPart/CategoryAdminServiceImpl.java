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
        log.info("Creating a new category: {}.", newCategory.getName());
        categoryUtils.checkCategoryNameIsBusy(newCategory.getName());
        Category category = categoryRepository.save(
                CategoryMapper.INSTANT.newCategoryDtoToCategory(newCategory));
        log.debug("The category has been created. ID = {}.", category.getId());
        return CategoryMapper.INSTANT.toCategoryDto(category);
    }

    @Override
    public CategoryDto patchCategoryById(Long catId, NewCategoryDto updatedCategory) {
        log.info("Updating a category with an ID = {}.", catId);
        categoryUtils.checkCategoryPresent(catId);
        Category categoryById = categoryRepository.getCategoryById(catId);
        Category categoryByName = categoryRepository.findFirstByName(updatedCategory.getName());
        if (categoryByName != null) {
            if (!categoryByName.getId().equals(categoryById.getId())) {
                throw new ConflictException("The category already exists.");
            }
        }
        categoryById.setName(updatedCategory.getName());
        categoryRepository.save(categoryById);
        log.debug("The category with ID = {} has been updated.", catId);
        return CategoryMapper.INSTANT.toCategoryDto(categoryById);
    }

    @Override
    public void deleteCategory(Long catId) {
        log.info("Deleting a category with ID = {}.", catId);
        categoryUtils.checkCategoryPresent(catId);
        categoryUtils.checkCategoryUsing(catId);
        categoryRepository.deleteById(catId);
        log.debug("Categories with ID = {} removed.", catId);
    }

}