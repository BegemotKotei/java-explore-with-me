package ru.practicum.compilation.service.adminPart;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.model.CompilationMapper;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.compilation.utils.CompilationUtils;
import ru.practicum.event.model.Event;
import ru.practicum.event.utils.EventUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class CompilationAdminServiceImpl implements CompilationAdminService {

    private final CompilationRepository compilationRepository;

    private final EventUtils eventUtils;

    private final CompilationUtils compilationUtils;

    @Override
    public CompilationDto createCompilation(NewCompilationDto newCompilation) {
        log.info("Администратор создает новую подборку \"{}\".", newCompilation.getTitle());
        List<Event> events = new ArrayList<>();
        if (newCompilation.getEvents() != null) {
            events = eventUtils.getEventByIds(newCompilation.getEvents());
        }
        Compilation compilation = compilationRepository.save(CompilationMapper.INSTANT.toCompilation(newCompilation, events));
        log.debug("Администратор создал новую подборку \"{}\" с ID = {}.", compilation.getTitle(), compilation.getId());
        return CompilationMapper.INSTANT.toCompilationDto(compilation);
    }

    @Override
    public CompilationDto updateCompilationById(Long compId, UpdateCompilationRequest updatedCompilation) {
        log.info("Администратор обновляет подборку с ID = {}.", compId);
        Compilation compilationForUpdate = compilationUtils.getCompilationById(compId);
        List<Event> events = new ArrayList<>();
        if (updatedCompilation.getEvents() != null) {
            events = eventUtils.getEventByIds(updatedCompilation.getEvents());
        }
        Optional.ofNullable(updatedCompilation.getPinned()).ifPresent(compilationForUpdate::setPinned);
        Optional.ofNullable(updatedCompilation.getTitle()).ifPresent(compilationForUpdate::setTitle);
        Optional.ofNullable(events).ifPresent(compilationForUpdate::setEvents);
        Compilation compilation = compilationRepository.save(CompilationMapper.INSTANT.toCompilation(compilationForUpdate, events));
        log.debug("Администратор обновил подборку \"{}\" с ID = {}.", compilation.getTitle(), compilation.getId());
            return CompilationMapper.INSTANT.toCompilationDto(compilation);
    }

    @Override
    public void deleteComplication(Long compId) {
        log.debug("Администратор удаляет подборку с ID = {}.", compId);
        compilationRepository.deleteById(compId);
    }

}