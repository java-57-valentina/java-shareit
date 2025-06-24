package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    public UserDto add(UserDto userDto) {
        User user = UserMapper.mapToUser(userDto);
        User created = userStorage.add(user);
        return UserMapper.mapToUserDto(created);
    }

    public UserDto update(Long userId, UserDto userDto) {
        User user = UserMapper.mapToUser(userDto);
        user.setId(userId);
        User updated = userStorage.update(user);
        return UserMapper.mapToUserDto(updated);
    }

    public void delete(Long userId) {
        userStorage.delete(userId);
    }

    public UserDto getById(Long userId) {
        User user = userStorage.getUser(userId);
        return UserMapper.mapToUserDto(user);
    }

    public List<UserDto> getAll() {
        return userStorage.getAll().stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }
}
