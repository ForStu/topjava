package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;

import java.util.Arrays;
import java.util.List;

public class UserUntil {
    public static final List<User> users = Arrays.asList(
            new User(1, "Name", "Email", "12345", Role.ADMIN),
            new User(2, "Name1", "Email1", "123451", Role.USER),
            new User(3, "Name2", "Email2", "123452", Role.USER)
    );
}
