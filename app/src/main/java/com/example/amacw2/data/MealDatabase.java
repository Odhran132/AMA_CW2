package com.example.amacw2.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {MealEntity.class}, version = 1, exportSchema = false)
public abstract class MealDatabase extends RoomDatabase
{
    private static final String DB_NAME = "Meals";
    private static MealDatabase instance;

    public static synchronized MealDatabase getInstance(Context context)
    {
        if (instance == null)
        {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            MealDatabase.class, DB_NAME)
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }

    public abstract MealDao mealDao();

    public static int seedInitialMeals(MealDao dao)
    {
        MealEntity sweetAndSourPork = new MealEntity(
                "Sweet and Sour Pork", "Pork", "Chinese",
                "https://www.themealdb.com/images/media/meals/1529442316.jpg",
                "Sweet",
                "https://www.youtube.com/watch?v=mdaBIhgEAMo",
                "Pork", "Egg", "Water", "Salt");

        MealEntity chickenMarengo = new MealEntity(
                "Chicken Marengo", "Chicken", "French",
                "https://www.themealdb.com/images/media/meals/qpxvuq1511798906.jpg",
                null,
                "https://www.youtube.com/watch?v=U33HYUr-0Fw",
                "Olive Oil", "Mushrooms", "Chicken Legs", "Passata");

        int inserted = 0;
        if (dao.insertMeal(sweetAndSourPork) != -1) inserted++;
        if (dao.insertMeal(chickenMarengo) != -1) inserted++;
        return inserted;
    }
}

