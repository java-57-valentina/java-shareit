package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {
    @Test
    void userCreation_ShouldSetFieldsCorrectly() {
        // Создаем пользователя
        User user = new User(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");

        // Проверяем поля
        assertEquals(1L, user.getId());
        assertEquals("Test User", user.getName());
        assertEquals("test@example.com", user.getEmail());
    }

    @Test
    void constructor_ShouldSetId() {
        User user = new User(2L);
        assertEquals(2L, user.getId());
        assertNull(user.getName()); // name не установлен
        assertNull(user.getEmail()); // email не установен
    }
}