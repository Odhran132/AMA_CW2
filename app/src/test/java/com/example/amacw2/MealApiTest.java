package com.example.amacw2;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import com.example.amacw2.data.MealEntity;

import org.junit.Test;

import java.util.List;
public class MealApiTest
{
    private static String meal(String name, String tags)
    {
        return "{\"strMeal\":\"" + name + "\",\"strCategory\":\"Pasta\",\"strArea\":\"Italian\","
                + "\"strMealThumb\":\"https://example.com/" + name.replace(" ", "") + ".jpg\","
                + "\"strTags\":" + (tags == null ? "null" : "\"" + tags + "\"") + ","
                + "\"strYoutube\":\"https://www.youtube.com/watch?v=abc\","
                + "\"strIngredient1\":\"penne rigate\",\"strIngredient2\":\"olive oil\","
                + "\"strIngredient3\":\"garlic\",\"strIngredient4\":\"chopped tomatoes\"}";
    }

    private static String response(String... meals)
    {
        return "{\"meals\":[" + String.join(",", meals) + "]}";
    }

    private static final String NO_RESULTS = "{\"meals\":null}";

    @Test
    public void emptyInput_isRejected()
    {
        assertNotNull(MealApi.validateInput("", "a meal name"));
        assertNotNull(MealApi.validateInput("    ", "a meal name"));
        assertNotNull(MealApi.validateInput(null, "a meal name"));
    }

    @Test
    public void invalidCharacters_areRejected()
    {
        assertNotNull(MealApi.validateInput("@@@", "a meal name"));
        assertNotNull(MealApi.validateInput("%", "a meal name"));
        assertNotNull(MealApi.validateInput("pie_", "a meal name"));
    }

    @Test
    public void normalMealNames_areAccepted()
    {
        assertNull(MealApi.validateInput("Arrabiata", "a meal name"));
        assertNull(MealApi.validateInput("  Sweet and Sour Pork ", "a meal name"));
        assertNull(MealApi.validateInput("Beef & Mustard Pie", "a meal name"));
        assertNull(MealApi.validateInput("Shepherd's Pie", "a meal name"));
    }

    @Test
    public void exactMatch_ignoresCaseAndOuterWhitespace()
    {
        assertTrue(MealApi.isExactMatch("Spicy Arrabiata Penne", "  spicy ARRABIATA penne  "));
        assertFalse(MealApi.isExactMatch("Spicy Arrabiata Penne", "Arrabiata"));
    }

    @Test
    public void findExactMatch_picksTheExactMealFromSeveral() throws Exception
    {
        String json = response(meal("Spicy Arrabiata Penne", "Pasta,Curry"), meal("Arrabiata", null));
        MealEntity found = MealApi.findExactMatch(json, " arrabiata ");
        assertNotNull(found);
        assertEquals("Arrabiata", found.getMealName());
        assertEquals("Pasta", found.getCategory());
        assertEquals("penne rigate", found.getIngredient1());
        assertNull("JSON null tags must become Java null, not \"null\"", found.getTags());
    }

    @Test
    public void findExactMatch_returnsNullWhenOnlyPartialMatches() throws Exception
    {
        String json = response(meal("Spicy Arrabiata Penne", null));
        assertNull(MealApi.findExactMatch(json, "Arrabiata"));
    }

    @Test
    public void findExactMatch_returnsNullWhenApiFindsNothing() throws Exception
    {
        assertNull(MealApi.findExactMatch(NO_RESULTS, "NotARealMeal"));
    }

    @Test
    public void parseFirstMeals_limitsToFiveInApiOrder() throws Exception
    {
        String json = response(meal("A", null), meal("B", null), meal("C", null),
                meal("D", null), meal("E", null), meal("F", null), meal("G", null));

        List<MealEntity> meals = MealApi.parseFirstMeals(json, MealApi.MAX_RESULTS);

        assertEquals(5, meals.size());
        assertEquals("A", meals.get(0).getMealName());
        assertEquals("E", meals.get(4).getMealName());
        assertEquals(7, MealApi.countMeals(json));
    }

    @Test
    public void parseFirstMeals_returnsAllWhenFewerThanFive() throws Exception
    {
        String json = response(meal("A", null), meal("B", null));
        assertEquals(2, MealApi.parseFirstMeals(json, MealApi.MAX_RESULTS).size());
    }

    @Test
    public void parseFirstMeals_emptyListWhenNoResults() throws Exception
    {
        assertTrue(MealApi.parseFirstMeals(NO_RESULTS, MealApi.MAX_RESULTS).isEmpty());
        assertEquals(0, MealApi.countMeals(NO_RESULTS));
    }

    @Test
    public void searchUrl_isEncoded()
    {
        assertEquals(MealApi.SEARCH_URL + "Sweet+and+Sour+Pork",
                MealApi.buildSearchUrl("  Sweet and Sour Pork "));
    }
}
