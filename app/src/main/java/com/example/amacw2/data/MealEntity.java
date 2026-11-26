package com.example.amacw2.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "Meal",
        indices = {@Index(value = {"mealName"}, unique = true)})
public class MealEntity implements Serializable
{
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "mealId")
    private Integer mealId;

    @NonNull
    @ColumnInfo(name = "mealName")
    private String mealName = "";

    @ColumnInfo(name = "category")
    private String category;

    @ColumnInfo(name = "area")
    private String area;

    @ColumnInfo(name = "mealThumb")
    private String mealThumb;

    @ColumnInfo(name = "tags")
    private String tags;

    @ColumnInfo(name = "youtube")
    private String youtube;

    @ColumnInfo(name = "ingredient1")
    private String ingredient1;

    @ColumnInfo(name = "ingredient2")
    private String ingredient2;

    @ColumnInfo(name = "ingredient3")
    private String ingredient3;

    @ColumnInfo(name = "ingredient4")
    private String ingredient4;

    public MealEntity() {
    }

    @Ignore
    public MealEntity(@NonNull String mealName, String category, String area, String mealThumb,
                      String tags, String youtube, String ingredient1, String ingredient2,
                      String ingredient3, String ingredient4)
    {
        this.mealName = mealName;
        this.category = category;
        this.area = area;
        this.mealThumb = mealThumb;
        this.tags = tags;
        this.youtube = youtube;
        this.ingredient1 = ingredient1;
        this.ingredient2 = ingredient2;
        this.ingredient3 = ingredient3;
        this.ingredient4 = ingredient4;
    }

    public String toDisplayString() {
        return "Meal: " + mealName + "\n"
                + "Category: " + orNone(category) + "\n"
                + "Area: " + orNone(area) + "\n"
                + "MealThumb: " + orNone(mealThumb) + "\n"
                + "Tags: " + orNone(tags) + "\n"
                + "YouTube: " + orNone(youtube) + "\n"
                + "Ingredient1: " + orNone(ingredient1) + "\n"
                + "Ingredient2: " + orNone(ingredient2) + "\n"
                + "Ingredient3: " + orNone(ingredient3) + "\n"
                + "Ingredient4: " + orNone(ingredient4);
    }

    private static String orNone(String value) {
        return (value == null || value.trim().isEmpty()) ? "None" : value;
    }
}


