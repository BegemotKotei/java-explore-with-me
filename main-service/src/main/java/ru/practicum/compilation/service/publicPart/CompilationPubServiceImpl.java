package ru.practicum.compilation.service.publicPart;

import io.micrometer.core.lang.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.model.CompilationMapper;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.compilation.utils.CompilationUtils;

import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompilationPubServiceImpl implements CompilationPubService {

    private final CompilationRepository compilationRepository;

    private final CompilationUtils compilationUtils;

    @Override
    public CompilationDto getCompilationByIdPublic(Long compId) {
        return CompilationMapper.INSTANT.toCompilationDto(
                compilationUtils.getCompilationById(compId));
    }

    @Override
    public List<CompilationDto> getComplicationsPublic(@Nullable Boolean pinned, Integer from, Integer size) {
        int page = from / size;
        PageRequest pageRequest = PageRequest.of(page, size);
        log.info("Выгрузка списка подборок с параметрами: pinned = {}, size={}, from={}.",pinned, size, page);
        Page<Compilation> pageCompilation;
        if (pinned != null) {
            pageCompilation = compilationRepository.findAllByPinned(pinned, pageRequest);
        } else {
            pageCompilation = compilationRepository.findAll(pageRequest);
        }
        List<Compilation> requests = pageCompilation.getContent();
        return CompilationMapper.INSTANT.toCompilationDto(requests);
    }

}