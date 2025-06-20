package ru.practicum.shareit.user;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.validation.Create;
import ru.practicum.shareit.user.validation.Update;

import java.util.List;


@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserDto add(@Validated({Create.class}) @RequestBody UserDto userDto) {
        UserDto added = userService.add(userDto);
        log.info("User was added: {}", added);
        return added;
    }

    @GetMapping("/{userId}")
    public UserDto getById(@PathVariable @Min(1) Long userId) {
        return userService.getById(userId);
    }

    @GetMapping
    public List<UserDto> getAll() {
        return userService.getAll();
    }

    @PatchMapping("/{userId}")
    public UserDto update(@PathVariable Long userId, @Validated({Update.class}) @RequestBody UserDto userDto) {
        return userService.update(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @Min(1) Long userId) {
        userService.delete(userId);
    }
}
