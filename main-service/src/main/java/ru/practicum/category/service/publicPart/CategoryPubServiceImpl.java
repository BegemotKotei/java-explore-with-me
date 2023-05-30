package ru.practicum.category.service.publicPart;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.model.Category;
import ru.practicum.category.model.CategoryMapper;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.category.utils.CategoryUtils;

import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryPubServiceImpl implements CategoryPubService {

    private final CategoryRepository categoryRepository;

    private final CategoryUtils categoryUtils;

    @Override
    public List<CategoryDto> getAllCategories(Integer from, Integer size) {
        Integer page = from / size;
        PageRequest pageRequest = PageRequest.of(page, size);
        log.info("Выгрузка списка категорий с параметрами: size={}, from={}.", size, page);
        Page<Category> pageCategory = categoryRepository.getAllCategoriesById(pageRequest);
        List<Category> requests = pageCategory.getContent();
        List<CategoryDto> requestsDto = requests.stream()
                    .map(request -> CategoryMapper.INSTANT.toCategoryDto(request))
                    .collect(Collectors.toList());
        return requestsDto;
    }

    @Override
    public CategoryDto getCategoryById(Long catId) {
        return categoryUtils.getCategoryById(catId);
    }

}