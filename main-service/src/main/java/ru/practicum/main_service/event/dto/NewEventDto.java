package ru.practicum.main_service.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.main_service.constant.Constants;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
public class NewEventDto {

    @NotBlank
    @Size(min = Constants.MIN_ANNOTATION, max = Constants.MAX_ANNOTATION)
    private String annotation;

    @NotNull
    private Long category;

    @NotBlank
    @Size(min = Constants.MIN_DESCRIPTION, max = Constants.MAX_DESCRIPTION)
    private String description;

    @NotNull
    @JsonFormat(pattern = Constants.DATE_FORMAT, shape = JsonFormat.Shape.STRING)
    private LocalDateTime eventDate;

    @NotNull
    @Valid
    private LocationDto location;

    private Boolean paid = false;

    @PositiveOrZero
    private Integer participantLimit = 0;

    private Boolean requestModeration = true;

    @NotBlank
    @Size(min = Constants.MIN_TITLE, max = Constants.MAX_TITLE)
    private String title;

}
