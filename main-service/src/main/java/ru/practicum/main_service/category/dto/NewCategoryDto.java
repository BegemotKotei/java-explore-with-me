package ru.practicum.main_service.category.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class NewCategoryDto {

    @NotBlank
    private String name;

}
