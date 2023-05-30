package ru.practicum.users.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ApiError.exception.ConflictException;
import ru.practicum.ApiError.exception.NotFoundException;
import ru.practicum.users.dto.NewUserRequest;
import ru.practicum.users.dto.UserDto;
import ru.practicum.users.model.User;
import ru.practicum.users.model.UserMapper;
import ru.practicum.users.repository.UsersRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UsersServiceImpl implements UsersService {

    private final UsersRepository usersRepository;

    @Override
    @Transactional
    public UserDto createUser(NewUserRequest newUser) {
        log.info("Создание пользователя с именем: {} и почтой: {}.", newUser.getName(),
                newUser.getEmail().replaceAll("(?<=.{2}).(?=[^@]*?@)", "*"));
        if (!isUserPresentByEmail(newUser.getEmail())) {
            User user = usersRepository.save(UserMapper.INSTANT.newUserRequestToUser(newUser));
            log.debug("Пользователь создан. ID = {}.", user.getId());
            return UserMapper.INSTANT.toUserDto(user);
        } else {
            log.error("Не удалось создать пользователя. Email уже занят.");
            throw new ConflictException("Данный адрес почты уже занят.");
        }
    }

    @Override
    public List<UserDto> getAllUsers(Integer from, Integer size, Set<Long> usersIds) {
        Integer page = from / size;
        PageRequest pageRequest = PageRequest.of(page, size);
        if (usersIds.size() > 0) {
            log.info("Выгрузка списка пользователей {} с параметрами: size={}, from={}.", usersIds, size, page);
            Page<User> pagesByIds = usersRepository.getAllUsersById(pageRequest, usersIds);
            List<User> requests = pagesByIds.getContent();
            List<UserDto> requestsDto = requests.stream()
                    .map(request -> UserMapper.INSTANT.toUserDto(request))
                    .collect(Collectors.toList());
            return requestsDto;
        } else {
            log.info("Выгрузка списка пользователей с параметрами: size={}, from={}.", size, page);
            Page<User> pages = usersRepository.findAll(pageRequest);
            List<User> requests = pages.getContent();
            List<UserDto> requestsDto = requests.stream()
                    .map(request -> UserMapper.INSTANT.toUserDto(request))
                    .collect(Collectors.toList());
            return requestsDto;
        }
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        log.debug("Удаление пользователя с id={}.", userId);
        usersRepository.delete(getUserById(userId));
    }

    @Override
    public User getUserById(Long userId) {
        log.info("Получение пользователя по ID = {}.", userId);
        return usersRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь с ID = " + userId + " не найден.")
        );
    }

    @Override
    public boolean isUserPresentByEmail(String email) {
        return usersRepository.findFirstByEmail(email) != null;
    }

    @Override
    public void isUserPresent(Long userId) {
        usersRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь с ID = " + userId + " не найден.")
        );
    }

}