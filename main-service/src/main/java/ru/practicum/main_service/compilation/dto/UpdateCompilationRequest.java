package ru.practicum.main_service.compilation.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.main_service.constant.Constants;

import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
public class UpdateCompilationRequest {

    @Size(min = Constants.MIN_TITLE, max = Constants.MAX_TITLE)
    private String title;

    private Boolean pinned;

    private List<Long> events;

}
