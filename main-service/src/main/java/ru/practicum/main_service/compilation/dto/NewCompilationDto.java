package ru.practicum.main_service.compilation.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.main_service.constant.Constants;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class NewCompilationDto {

    @NotBlank
    @Size(min = Constants.MIN_TITLE, max = Constants.MAX_TITLE)
    private String title;

    private Boolean pinned = false;

    private List<Long> events = new ArrayList<>();

}
