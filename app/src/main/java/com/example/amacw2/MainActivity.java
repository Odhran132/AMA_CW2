package com.example.amacw2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.util.Log;
import android.widget.TextView;

import com.example.amacw2.data.MealDao;
import com.example.amacw2.data.MealDatabase;

public class MainActivity extends AppCompatActivity
{
    Button mealByNameBtn, mealSearchBtn, mealsImagesBtn;

    private static final String TAG = "MainActivity";

    private MealDao mealDao;
    private TextView savedMealsTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mealDao = MealDatabase.getInstance(getApplicationContext()).mealDao();
        savedMealsTxt = findViewById(R.id.savedMealsTxt);


        mealByNameBtn = findViewById(R.id.mealByNameBtn);
        mealByNameBtn.setOnClickListener(v ->
        {
            Intent intent = new Intent(MainActivity.this, MealByNameScreen.class);
            startActivity(intent);
        });


        mealSearchBtn = findViewById(R.id.sMealSearchBtn);
        mealSearchBtn.setOnClickListener(v ->
        {
            Intent intent = new Intent(MainActivity.this, SavedMealsScreen.class);
            startActivity(intent);
        });


        mealsImagesBtn = findViewById(R.id.bMealsImagesBtn);
        mealsImagesBtn.setOnClickListener(v ->
        {
            Intent intent = new Intent(MainActivity.this, MealsImagesScreen.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        refreshSavedMealsCount();
    }

    private void refreshSavedMealsCount()
    {
        new Thread(() ->
        {
            int inserted = MealDatabase.seedInitialMeals(mealDao);
            Log.d(TAG, "Seeding: " + inserted + " new meal(s) inserted");

            int count = mealDao.getMealCount();

            runOnUiThread(() ->
                    savedMealsTxt.setText(getString(R.string.saved_meals_count, count)));
        }).start();
    }

}