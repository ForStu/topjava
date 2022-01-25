package ru.javawebinar.topjava.repository.inmemory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.UserRepository;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.util.UserUntil;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class InMemoryUserRepository implements UserRepository {
    private static final Logger log = LoggerFactory.getLogger(InMemoryUserRepository.class);

    private final Map<Integer, User> usersRepository = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    {
        UserUntil.users.forEach(this::save);
    }

    @Override
    public boolean delete(int id) {
        log.info("delete {}", id);
        return usersRepository.remove(id) != null;
    }

    @Override
    public User save(User user) {
        log.info("save {}", user);

        if (user.isNew()) {
            user.setId(counter.incrementAndGet());
            usersRepository.put(user.getId(), user);
            return user;
        }
        // handle case: update, but not present in storage
        return usersRepository.computeIfPresent(user.getId(), (id, oldUser) -> user);
    }

    @Override
    public User get(int id) {
        log.info("get {}", id);
        return usersRepository.get(id);
    }

    @Override
    public List<User> getAll() {
        log.info("getAll");
        Comparator<User> comparator = new Comparator<User>() {
            public int compare(User u1, User u2) {
                return u1.getName().compareTo(u2.getName());
            }
        };
        List<User> userList = new ArrayList<User>(usersRepository.values());
        userList.sort(comparator);

        return userList;
    }

    @Override
    public User getByEmail(String email) {
        log.info("getByEmail {}", email);

        List<User> users = getAll();

        for (User user : users) {
            if (user.getEmail().equals(email)) {
                return user;
            }
        }
        return null;
    }
}
