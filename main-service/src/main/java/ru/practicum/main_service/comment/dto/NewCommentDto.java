package ru.practicum.main_service.comment.dto;

import lombok.Getter;
import ru.practicum.main_service.constant.Constants;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
public class NewCommentDto {

    @NotBlank
    @Size(min = Constants.MIN_COMMENT, max = Constants.MAX_COMMENT)
    private String text;

}
