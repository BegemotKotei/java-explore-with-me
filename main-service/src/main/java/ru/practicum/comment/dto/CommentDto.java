package ru.practicum.comment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.DateConstants;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {

    private Long id;

    private Long eventId;

    private Long userId;

    private String comment;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateConstants.DATE_PATTERN)
    private LocalDateTime created;

}