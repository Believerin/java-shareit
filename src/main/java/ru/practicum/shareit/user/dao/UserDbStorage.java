package ru.practicum.shareit.user.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
public class UserDbStorage {

    private final Map<Integer, User> users = new HashMap<>();
    private int nextId = 0;

    public Collection<User> findAll() {
        return users.values();
    }

    public User addUser(User user) {
        for (User o : users.values()) {
            if (o.getEmail().equals(user.getEmail())) {
                return null;
            }
        }
        user.setId(++nextId);
        users.put(nextId, user);
        return user;
    }

    public User modifyUser(int userId, User user) {
        for (User o : users.values()) {
            if (userId != o.getId() && o.getEmail().equals(user.getEmail())) {
                return null;
            }
        }
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