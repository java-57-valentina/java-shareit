package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.User;

import java.util.Collection;

public interface UserStorage {

    User add(User user);

    User update(User user);

    User getUser(Long userId);

    void delete(Long userId);

    Collection<User> getAll();
}
