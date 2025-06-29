package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserDto add(UserDto userDto) {
        checkEmailIsFree(userDto.getEmail());
        User user = userDto.toUser();
        User created = userRepository.save(user);
        return UserDto.fromUser(created);
    }

    private void checkEmailIsFree(String email) {
        boolean exists = userRepository.existsByEmail(email);
        if (exists)
            throw new ConflictException(
                    String.format("User with email %s already exists", email));
    }

    public UserDto update(Long userId, UserDto userDto) {
        User user = userDto.toUser();
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
        return UserDto.fromUser(updated);
    }

    public void delete(Long userId) {
        userRepository.deleteById(userId);
    }

    public UserDto getById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User", userId));

        return UserDto.fromUser(user);
    }

    public List<UserDto> getAll() {
        return userRepository.findAll().stream()
                .map(UserDto::fromUser)
                .toList();
    }
}
