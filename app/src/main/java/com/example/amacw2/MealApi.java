package com.example.amacw2;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.example.amacw2.data.MealEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public final class MealApi
{
    public static final String SEARCH_URL = "https://www.themealdb.com/api/json/v1/1/search.php?s=";
    public static final int MAX_RESULTS = 5;

    private static final String VALID_PATTERN = "[\\p{L}0-9 '&,.()\\-]+";

    private MealApi()
    {
    }

    public static String validateInput(String raw, String what)
    {
        if (raw == null || raw.trim().isEmpty())
        {
            return "Please enter " + what;
        }
        if (!raw.trim().matches(VALID_PATTERN))
        {
            return "Only letters, numbers, spaces and ' - & , . ( ) are allowed";
        }
        return null;
    }

    public static boolean isExactMatch(String apiName, String userInput)
    {
        if (apiName == null || userInput == null) return false;
        return apiName.trim().equalsIgnoreCase(userInput.trim());
    }


    public static String buildSearchUrl(String query)
    {
        try
        {
            return SEARCH_URL + URLEncoder.encode(query.trim(), "UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            return SEARCH_URL + query.trim().replace(" ", "%20");
        }
    }


    public static JSONArray getMealsArray(String response) throws JSONException
    {
        JSONObject root = new JSONObject(response);
        if (root.isNull("meals"))
        {
            return null;
        }
        return root.getJSONArray("meals");
    }


    public static MealEntity parseMeal(JSONObject meal)
    {
        return new MealEntity(
                text(meal, "strMeal"),
                text(meal, "strCategory"),
                text(meal, "strArea"),
                text(meal, "strMealThumb"),
                text(meal, "strTags"),
                text(meal, "strYoutube"),
                text(meal, "strIngredient1"),
                text(meal, "strIngredient2"),
                text(meal, "strIngredient3"),
                text(meal, "strIngredient4"));
    }

    public static MealEntity findExactMatch(String response, String userInput) throws JSONException
    {
        JSONArray meals = getMealsArray(response);
        if (meals == null) return null;

        for (int i = 0; i < meals.length(); i++)
        {
            JSONObject meal = meals.getJSONObject(i);
            if (isExactMatch(meal.optString("strMeal"), userInput))
            {
                return parseMeal(meal);
            }
        }
        return null;
    }


    public static ArrayList<MealEntity> parseFirstMeals(String response, int max) throws JSONException
    {
        ArrayList<MealEntity> result = new ArrayList<>();
        JSONArray meals = getMealsArray(response);
        if (meals == null) return result;

        int limit = Math.min(meals.length(), max);
        for (int i = 0; i < limit; i++)
        {
            result.add(parseMeal(meals.getJSONObject(i)));
        }
        return result;
    }

    public static int countMeals(String response) throws JSONException
    {
        JSONArray meals = getMealsArray(response);
        return meals == null ? 0 : meals.length();
    }


    static String text(JSONObject obj, String key)
    {
        if (obj.isNull(key)) return null;
        String value = obj.optString(key, "").trim();
        return value.isEmpty() ? null : value;
    }


    public static String describeError(VolleyError error)
    {
        if (error instanceof NoConnectionError)
        {
            return "No internet connection. Check your Wi-Fi or mobile data and try again.";
        }
        else if (error instanceof TimeoutError)
        {
            return "The request timed out. Please try again.";
        }
        else if (error instanceof AuthFailureError)
        {
            return "The server refused the request.";
        }
        else if (error instanceof ServerError)
        {
            int code = error.networkResponse != null ? error.networkResponse.statusCode : -1;
            return "The meal server returned an error (HTTP " + code + ").";
        }
        else if (error instanceof NetworkError)
        {
            return "Network error. Please try again.";
        }
        else if (error instanceof ParseError)
        {
            return "The server sent data the app couldn't read.";
        }
        return "Something went wrong contacting the meal server.";
    }

    public static List<String> namesOf(List<MealEntity> meals)
    {
        List<String> names = new ArrayList<>();
        for (MealEntity m : meals) names.add(m.getMealName());
        return names;
    }

}
