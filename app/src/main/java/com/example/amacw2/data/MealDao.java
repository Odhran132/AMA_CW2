package com.example.amacw2.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MealDao
{
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insertMeal(MealEntity meal);

    @Query("SELECT COUNT(*) FROM Meal")
    int getMealCount();

    @Query("SELECT * FROM Meal ORDER BY mealName")
    List<MealEntity> getAllMeals();

    @Query("SELECT * FROM Meal WHERE mealName LIKE '%' || :term || '%' "
            + "OR ingredient1 LIKE '%' || :term || '%' "
            + "OR ingredient2 LIKE '%' || :term || '%' "
            + "OR ingredient3 LIKE '%' || :term || '%' "
            + "OR ingredient4 LIKE '%' || :term || '%' "
            + "ORDER BY mealName")
    List<MealEntity> searchByNameOrIngredient(String term);


}
