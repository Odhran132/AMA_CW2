package com.example.amacw2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity
{
    Button mealByNameBtn, mealSearchBtn, mealsImagesBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

}