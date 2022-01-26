package ru.javawebinar.topjava.repository.inmemory;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.util.UserUntil;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class InMemoryMealRepository implements MealRepository {
    private final Map<Integer, Map<Integer, Meal>> authUserMeal = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    {
        for (int i = 0; i < UserUntil.users.size(); i++) {
            authUserMeal.put(UserUntil.users.get(i).getId(), new ConcurrentHashMap<>());
        }

        for (int i = 0; i < MealsUtil.meals.size(); i++) {
            save(MealsUtil.meals.get(i).getUserId(), MealsUtil.meals.get(i));
        }
    }

    @Override
    public Meal save(int userId, Meal meal) {
        Map<Integer, Meal> allUserMeals = authUserMeal.get(userId);

        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
            allUserMeals.put(meal.getId(), meal);
            authUserMeal.put(userId, allUserMeals);
            return meal;
        }
//        // handle case: update, but not present in storage
        allUserMeals.computeIfPresent(meal.getId(), (id, oldMeal) -> meal);
        authUserMeal.put(userId, allUserMeals);
        return meal;
    }

    @Override
    public boolean delete(int userId, int id) {
        Map<Integer, Meal> mealToDelete = authUserMeal.get(userId);
        return mealToDelete.remove(id) != null;
    }

    @Override
    public Meal get(int userId, int id) {
        Map<Integer, Meal> mealToGet = authUserMeal.get(userId);
        return mealToGet.get(id);
    }

    @Override
    public Collection<Meal> getAll(int userId) {
        Map<Integer, Meal> allUserMeals = authUserMeal.get(userId);
        Comparator<Meal> mealComparator = new Comparator<Meal>() {
            @Override
            public int compare(Meal o1, Meal o2) {
                if (o1.getDateTime().isBefore(o2.getDateTime())) {
                    return 1;
                } else if (o1.getDateTime().isAfter(o2.getDateTime())) {
                    return -1;
                } else return 0;
            }
        };
        new ArrayList<>(allUserMeals.values()).sort(mealComparator);
        return allUserMeals.values();
    }
}

