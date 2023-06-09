package ru.practicum.users.service;

import ru.practicum.users.dto.NewUserRequest;
import ru.practicum.users.dto.UserDto;

import java.util.List;
import java.util.Set;

public interface UsersService {

    UserDto createUser(NewUserRequest newUserRequest);

    List<UserDto> getAllUsers(Integer from, Integer size, Set<Long> usersIds);

    void deleteUser(Long userId);

    boolean isUserPresentByEmail(String email);

}