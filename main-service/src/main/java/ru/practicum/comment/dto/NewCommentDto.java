package ru.practicum.comment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.DateConstants;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewCommentDto {

    private Long eventId;

    private Long userId;

    @NotBlank
    @Size(min = 10, max = 2000)
    private String comment;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateConstants.DATE_PATTERN)
    private LocalDateTime created = LocalDateTime.now();

}