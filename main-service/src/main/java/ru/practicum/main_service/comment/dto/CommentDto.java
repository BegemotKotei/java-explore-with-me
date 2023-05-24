package ru.practicum.main_service.comment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.main_service.constant.Constants;
import ru.practicum.main_service.user.dto.UserShortDto;

import java.time.LocalDateTime;

@Getter
@Setter
public class CommentDto {

    private long id;

    private UserShortDto author;

    private Long eventId;

    private String text;

    @JsonFormat(pattern = Constants.DATE_FORMAT, shape = JsonFormat.Shape.STRING)
    private LocalDateTime created;

    @JsonFormat(pattern = Constants.DATE_FORMAT, shape = JsonFormat.Shape.STRING)
    private LocalDateTime edited;

}
