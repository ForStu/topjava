package ru.javawebinar.topjava.repository.inmemory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.util.UserUntil;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class InMemoryMealRepository implements MealRepository {
    private static final Logger log = LoggerFactory.getLogger(InMemoryMealRepository.class);

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
        log.info("save {}", meal);
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
        log.info("delete meal with id {}", id);
        Map<Integer, Meal> mealToDelete = authUserMeal.get(userId);
        return mealToDelete.remove(id) != null;
    }

    @Override
    public Meal get(int userId, int id) {
        log.info("get meal with id {}", id);
        Map<Integer, Meal> mealToGet = authUserMeal.get(userId);
        return mealToGet.get(id);
    }

    @Override
    public Collection<Meal> getAll(int userId) {
        log.info("getAll");
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

