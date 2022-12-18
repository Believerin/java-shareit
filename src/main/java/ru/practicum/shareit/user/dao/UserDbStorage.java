package ru.practicum.shareit.user.dao;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Component
public class UserDbStorage {

    private final Map<Integer, User> users = new HashMap<>();
    private int nextId = 0;

    public Collection<User> findAll() {
        return users.values();
    }

    public User addUser(User user) {
        user.setId(++nextId);
        users.put(nextId, user);
        return user;
    }

    public User modifyUser(int userId, User user) {
        users.put(userId, user);
        return user;
    }

    public User getUser(int userId) {
        if (users.containsKey(userId)) {
            return users.get(userId);
        }
        return  null;
    }

    public void deleteUser(int userId) {
        users.remove(userId);
    }
}