package com.example.amacw2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import androidx.annotation.NonNull;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.amacw2.data.MealDao;
import com.example.amacw2.data.MealDatabase;
import com.example.amacw2.data.MealEntity;

import java.util.List;

public class SavedMealsScreen extends AppCompatActivity
{

    private static final String TAG = "SavedMealsScreen";
    private static final String KEY_STATUS = "status";
    private static final String KEY_RESULTS = "results";

    private EditText searchInput;
    private TextView statusTxt;
    private TextView resultsTxt;
    private MealDao mealDao;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_meals_screen);

        searchInput = findViewById(R.id.savedSearchInput);
        statusTxt = findViewById(R.id.savedStatusTxt);
        resultsTxt = findViewById(R.id.savedResultsTxt);
        Button searchBtn = findViewById(R.id.savedSearchBtn);

        mealDao = MealDatabase.getInstance(getApplicationContext()).mealDao();

        searchBtn.setOnClickListener(v -> searchSavedMeals());
        findViewById(R.id.savedBackBtn).setOnClickListener(v -> finish());

        searchInput.setOnEditorActionListener((v, actionId, event) ->
        {
            if (actionId == EditorInfo.IME_ACTION_SEARCH)
            {
                searchSavedMeals();
                return true;
            }
            return false;
        });

        if (savedInstanceState != null)
        {
            statusTxt.setText(savedInstanceState.getString(KEY_STATUS, ""));
            resultsTxt.setText(savedInstanceState.getString(KEY_RESULTS, ""));
        }

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_STATUS, statusTxt.getText().toString());
        outState.putString(KEY_RESULTS, resultsTxt.getText().toString());
    }

    private void searchSavedMeals()
    {
        String input = searchInput.getText().toString();

        String error = MealApi.validateInput(input, "part of a meal or ingredient name");
        if (error != null)
        {
            searchInput.setError(error);
            statusTxt.setText(error);
            resultsTxt.setText("");
            return;
        }

        String term = input.trim();
        statusTxt.setText(R.string.searching);

        new Thread(() ->
        {
            List<MealEntity> matches = mealDao.searchByNameOrIngredient(term);
            Log.d(TAG, "\"" + term + "\" matched " + matches.size() + " meal(s)");

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < matches.size(); i++)
            {
                if (i > 0) sb.append("\n\n────────────\n\n");
                sb.append(matches.get(i).toDisplayString());
            }

            runOnUiThread(() ->
            {
                if (matches.isEmpty())
                {
                    statusTxt.setText("No saved meals contain \"" + term
                            + "\" in their name or ingredients.");
                }
                else
                {
                    statusTxt.setText(matches.size() + " saved meal(s) match \"" + term + "\":");
                }
                resultsTxt.setText(sb.toString());
            });
        }).start();
    }


}