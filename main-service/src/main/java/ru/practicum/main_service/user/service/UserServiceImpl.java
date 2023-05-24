package ru.practicum.main_service.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.main_service.user.dto.NewUserRequest;
import ru.practicum.main_service.user.dto.UserDto;
import ru.practicum.main_service.exception.NotFoundException;
import ru.practicum.main_service.user.mapper.UserMapper;
import ru.practicum.main_service.user.model.User;
import ru.practicum.main_service.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper mapper;

    @Override
    public UserDto createUser(NewUserRequest newUserRequest) {
        log.info("MainService - createUser: newUserRequest={}", newUserRequest);

        return mapper.toUserDto(userRepository.save(mapper.toUser(newUserRequest)));
    }

    @Override
    public User getUserById(Long id) {
        log.info("MainService - getUserById: id={}", id);

        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with ID " + id + " doesn't exist"));
    }

    @Override
    public List<UserDto> getUsersDto(List<Long> ids, Pageable pageable) {
        log.info("MainService - getUsersDto: ids={}, pageable={}", ids, pageable);

        if (ids == null || ids.isEmpty()) {
            return userRepository.findAll(pageable).stream()
                    .map(mapper::toUserDto)
                    .collect(Collectors.toList());
        } else {
            return userRepository.findAllByIdIn(ids, pageable).stream()
                    .map(mapper::toUserDto)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public void deleteUserById(Long id) {
        log.info("MainService - deleteUserById: id={}", id);

        checkUserInBase(id);

        userRepository.deleteById(id);
    }

    @Override
    public void checkUserInBase(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("User with ID " + id + " doesn't exist");
        }
    }

}
