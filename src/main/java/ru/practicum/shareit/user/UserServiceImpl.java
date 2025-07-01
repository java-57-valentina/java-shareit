package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Transactional
    public UserDto add(UserDto userDto) {
        checkEmailIsFree(userDto.getEmail());
        User user = UserMapper.toUser(userDto);
        User created = userRepository.save(user);
        return UserMapper.toDto(created);
    }

    private void checkEmailIsFree(String email) {
        boolean exists = userRepository.existsByEmail(email);
        if (exists)
            throw new ConflictException(
                    String.format("User with email %s already exists", email));
    }

    @Transactional
    public UserDto update(Long userId, UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        user.setId(userId);

        User origin = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User", userId));

        if (user.getName() != null && !user.getName().equals(origin.getName()))
            origin.setName(user.getName());

        if (user.getEmail() != null && !user.getEmail().equals(origin.getEmail())) {
            checkEmailIsFree(user.getEmail());
            origin.setEmail(user.getEmail());
        }

        User updated = userRepository.save(origin);
        return UserMapper.toDto(updated);
    }

    @Transactional
    public void delete(Long userId) {
        userRepository.deleteById(userId);
    }

    public UserDto getById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User", userId));

        return UserMapper.toDto(user);
    }

    public List<UserDto> getAll() {
        return userRepository.findAll().stream()
                .map(UserMapper::toDto)
                .toList();
    }
}
