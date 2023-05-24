package ru.practicum.main_service.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.main_service.constant.Constants;
import ru.practicum.main_service.event.enums.EventStateAction;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
public class UpdateEventAdminRequest {

    @Size(min = Constants.MIN_ANNOTATION, max = Constants.MAX_ANNOTATION)
    private String annotation;

    private Long category;

    @Size(min = Constants.MIN_DESCRIPTION, max = Constants.MAX_DESCRIPTION)
    private String description;

    @JsonFormat(pattern = Constants.DATE_FORMAT, shape = JsonFormat.Shape.STRING)
    private LocalDateTime eventDate;

    @Valid
    private LocationDto location;

    private Boolean paid;

    @PositiveOrZero
    private Integer participantLimit;

    private Boolean requestModeration;

    private EventStateAction stateAction;

    @Size(min = Constants.MIN_TITLE, max = Constants.MAX_TITLE)
    private String title;

}
