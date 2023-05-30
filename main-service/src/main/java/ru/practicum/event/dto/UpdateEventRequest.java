package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.micrometer.core.lang.Nullable;
import lombok.*;
import ru.practicum.DateConstants;
import ru.practicum.event.dto.location.LocationDto;
import ru.practicum.event.enums.EventStateAction;

import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEventRequest {

    @Nullable
    @Size(min = 20, max = 2000)
    private String annotation;

    @Nullable
    @Positive
    private Long category;

    @Nullable
    @Size(min = 20, max = 7000)
    private String description;

    @Nullable
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateConstants.DATE_PATTERN)
    private LocalDateTime eventDate;

    @Nullable
    private LocationDto location;

    private Boolean paid;

    private Integer participantLimit;

    private Boolean requestModeration;

    private EventStateAction stateAction;

    @Nullable
    @Size(min = 3, max = 120)
    private String title;

}