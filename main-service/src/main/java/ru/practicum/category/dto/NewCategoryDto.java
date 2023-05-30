package ru.practicum.category.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class NewCategoryDto {

    @NotBlank
    @Size(min = 2, max = 50)
    private String name;

}