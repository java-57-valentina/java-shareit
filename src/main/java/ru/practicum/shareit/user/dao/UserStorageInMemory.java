package ru.practicum.shareit.user.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.user.User;

import java.util.*;

@Repository
public class UserStorageInMemory implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    private final Set<String> emails = new HashSet<>();
    private Long generatedId = 0L;

    @Override
    public User add(User user) {
        checkEmailUnique(user.getEmail());
        user.setId(++generatedId);
        users.put(user.getId(), user);
        emails.add(user.getEmail());
        return user;
    }

    @Override
    public User update(User user) {
        User origin = getUser(user.getId());

        if (user.getEmail() != null && !origin.getEmail().equals(user.getEmail())) {
            checkEmailUnique(user.getEmail());
            origin.setEmail(user.getEmail());
        }

        if (user.getName() != null && !origin.getName().equals(user.getName()))
            origin.setName(user.getName());

        return origin;
    }

    @Override
    public User getUser(Long id) {
        User user = users.get(id);
        if (user == null)
            throw new NotFoundException("User", id);
        return user;
    }

    @Override
    public void delete(Long userId) {
        User user = users.get(userId);
        if (user == null)
            return;
        emails.remove(user.getEmail());
        users.remove(userId);
    }

    @Override
    public Collection<User> getAll() {
        return users.values();
    }

    private void checkEmailUnique(String email) {
        if (emails.contains(email))
            throw new ConflictException(
                    String.format("User with email %s already exists", email));
    }
}
