package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private UserDto userDto;
    private User user;


    @BeforeEach
    void setUp() {
        userDto = new UserDto(1L, "John Doe", "john@example.com");
        user = UserMapper.toUser(userDto);
    }

    @Test
    void add_ShouldSaveAndReturnUserDto() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto result = userService.add(userDto);

        assertNotNull(result);
        assertEquals(userDto.getId(), result.getId());
        assertEquals(userDto.getName(), result.getName());
        assertEquals(userDto.getEmail(), result.getEmail());

        verify(userRepository).existsByEmail(userDto.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void add_ShouldThrowConflictException_WhenEmailExists() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(ConflictException.class, () -> userService.add(userDto));

        verify(userRepository).existsByEmail(userDto.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void update_ShouldUpdateNameAndReturnUpdatedUser() {
        UserDto updateDto = new UserDto(null,"Updated Name", null);
        User updated = new User(1L, "Updated Name", "john@example.com");

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(updated);

        UserDto updatedDto = userService.update(1L, updateDto);

        assertNotNull(updatedDto);
        assertEquals("Updated Name", updatedDto.getName());
        assertEquals("john@example.com", updatedDto.getEmail());

        verify(userRepository).findById(1L);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void update_ShouldUpdateEmailAndReturnUpdatedUser() {
        UserDto updateDto = new UserDto(null,null,  "updated@example.com");
        User updated = new User(1L, "John Doe", "updated@example.com");

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(updated);

        UserDto result = userService.update(1L, updateDto);

        assertNotNull(result);
        assertEquals("John Doe", result.getName());
        assertEquals("updated@example.com", result.getEmail());

        verify(userRepository).findById(1L);
        verify(userRepository).existsByEmail("updated@example.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void update_ShouldThrowConflictException_WhenNewEmailExists() {
        UserDto updateDto = new UserDto(null,null,  "existing@example.com");

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(ConflictException.class, () -> userService.update(1L, updateDto));

        verify(userRepository).findById(1L);
        verify(userRepository).existsByEmail("existing@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void update_ShouldThrowNotFoundException_WhenUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.update(999L, userDto));

        verify(userRepository).findById(999L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getById_ShouldReturnUserDto() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        UserDto result = userService.getById(1L);

        assertNotNull(result);
        assertEquals(userDto.getId(), result.getId());
        assertEquals(userDto.getName(), result.getName());
        assertEquals(userDto.getEmail(), result.getEmail());

        verify(userRepository).findById(1L);
    }

    @Test
    void getById_ShouldThrowNotFoundException_WhenUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getById(999L));

        verify(userRepository).findById(999L);
    }

    @Test
    void getAll_ShouldReturnListOfUserDto() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserDto> result = userService.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(userDto.getId(), result.getFirst().getId());
        assertEquals(userDto.getName(), result.getFirst().getName());
        assertEquals(userDto.getEmail(), result.getFirst().getEmail());

        verify(userRepository).findAll();
    }

    @Test
    void delete_ShouldCallRepositoryDelete() {
        doNothing().when(userRepository).deleteById(anyLong());

        userService.delete(1L);

        verify(userRepository).deleteById(1L);
    }
}