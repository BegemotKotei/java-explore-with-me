package ru.practicum.main_service.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {
    private String email;
    private Long id;
    private String name;
}
