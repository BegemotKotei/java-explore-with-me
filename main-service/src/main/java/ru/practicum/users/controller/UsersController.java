package ru.practicum.users.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.users.dto.NewUserRequest;
import ru.practicum.users.dto.UserDto;
import ru.practicum.users.service.UsersService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(path = "/admin/users")
@RequiredArgsConstructor
@Validated
public class UsersController {

    private final UsersService usersService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(
            @Valid @RequestBody NewUserRequest newUser) {
        return usersService.createUser(newUser);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> getAllUsers(
            @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "ids", required = false) Set<Long> ids) {
        ids = ids == null ? new HashSet<>() : ids;
        return usersService.getAllUsers(from, size, ids);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(
            @Positive @PathVariable Long userId) {
        usersService.deleteUser(userId);
    }

}