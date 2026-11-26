package com.example.amacw2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import androidx.annotation.NonNull;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.amacw2.data.MealDao;
import com.example.amacw2.data.MealDatabase;
import com.example.amacw2.data.MealEntity;

import org.json.JSONException;
public class MealByNameScreen extends AppCompatActivity
{

    private static final String TAG = "MealByNameScreen";

    private static final String KEY_MEAL = "current_meal";
    private static final String KEY_STATUS = "status";
    private static final String KEY_LOADING = "loading";

    private EditText mealNameInput;
    private Button searchBtn;
    private Button saveBtn;
    private TextView statusTxt;
    private TextView detailsTxt;
    private ImageView mealImg;

    private RequestQueue queue;
    private MealDao mealDao;

    private MealEntity currentMeal;
    private boolean loading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_by_name_screen);

        mealNameInput = findViewById(R.id.mealNameInput);
        searchBtn = findViewById(R.id.searchNameBtn);
        saveBtn = findViewById(R.id.saveMealBtn);
        statusTxt = findViewById(R.id.nameStatusTxt);
        detailsTxt = findViewById(R.id.nameMealDetailsTxt);
        mealImg = findViewById(R.id.nameMealImg);

        queue = Volley.newRequestQueue(this);
        mealDao = MealDatabase.getInstance(getApplicationContext()).mealDao();


        searchBtn.setOnClickListener(v -> searchMeal());
        saveBtn.setOnClickListener(v -> saveMeal());
        findViewById(R.id.nameBackBtn).setOnClickListener(v -> finish());

        mealNameInput.setOnEditorActionListener((v, actionId, event) ->
        {
            if (actionId == EditorInfo.IME_ACTION_SEARCH)
            {
                searchMeal();
                return true;
            }
            return false;
        });

        if (savedInstanceState != null)
        {
            statusTxt.setText(savedInstanceState.getString(KEY_STATUS, ""));

            MealEntity saved = (MealEntity) savedInstanceState.getSerializable(KEY_MEAL);
            if (saved != null)
            {
                showMeal(saved);
            }

            if (savedInstanceState.getBoolean(KEY_LOADING))
            {
                searchMeal();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putSerializable(KEY_MEAL, currentMeal);
        outState.putString(KEY_STATUS, statusTxt.getText().toString());
        outState.putBoolean(KEY_LOADING, loading);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        queue.cancelAll(TAG);
    }

    private void searchMeal()
    {
        String input = mealNameInput.getText().toString();

        String error = MealApi.validateInput(input, "a meal name");
        if (error != null)
        {
            mealNameInput.setError(error);
            clearMeal();
            statusTxt.setText(error);
            return;
        }

        String query = input.trim();
        clearMeal();
        setLoading(true);
        statusTxt.setText(R.string.searching);

        String url = MealApi.buildSearchUrl(query);
        Log.d(TAG, "Requesting " + url);

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response ->
                {
                    setLoading(false);
                    try
                    {
                        MealEntity meal = MealApi.findExactMatch(response, query);
                        if (meal == null)
                        {
                            statusTxt.setText("No meal called \"" + query + "\" was found. "
                                    + "Check the spelling and enter the full meal name.");
                        }
                        else
                        {
                            statusTxt.setText("Meal found:");
                            showMeal(meal);
                        }
                    }
                    catch (JSONException e)
                    {
                        Log.e(TAG, "Bad JSON", e);
                        statusTxt.setText("The server sent data the app couldn't read.");
                    }
                },
                volleyError ->
                {
                    setLoading(false);
                    Log.e(TAG, "Volley error", volleyError);
                    statusTxt.setText(MealApi.describeError(volleyError));
                });

        request.setTag(TAG);
        queue.add(request);
    }


    private void showMeal(MealEntity meal)
    {
        currentMeal = meal;
        detailsTxt.setText(meal.toDisplayString());
        saveBtn.setVisibility(View.VISIBLE);
        saveBtn.setEnabled(true);
        loadImage(meal.getMealThumb());
    }

    private void clearMeal()
    {
        currentMeal = null;
        detailsTxt.setText("");
        saveBtn.setVisibility(View.GONE);
        mealImg.setImageDrawable(null);
        mealImg.setVisibility(View.GONE);
    }

    private void loadImage(String url)
    {
        if (url == null)
        {
            mealImg.setVisibility(View.GONE);
            return;
        }
        mealImg.setVisibility(View.VISIBLE);

        ImageRequest imageRequest = new ImageRequest(url,
                (Bitmap bitmap) -> mealImg.setImageBitmap(bitmap),
                0, 0, ImageView.ScaleType.CENTER_CROP, Bitmap.Config.RGB_565,
                error ->
                {
                    Log.e(TAG, "Image failed", error);
                    mealImg.setImageResource(android.R.drawable.ic_menu_report_image);
                });
        imageRequest.setTag(TAG);
        queue.add(imageRequest);
    }


    private void saveMeal()
    {
        if (currentMeal == null) return;
        MealEntity toSave = currentMeal;
        saveBtn.setEnabled(false);

        new Thread(() ->
        {
            long rowId = mealDao.insertMeal(toSave);
            runOnUiThread(() ->
            {
                if (rowId == -1)
                {
                    Toast.makeText(this, toSave.getMealName() + " is already saved",
                            Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(this, toSave.getMealName() + " saved to the database",
                            Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    private void setLoading(boolean isLoading)
    {
        loading = isLoading;
        searchBtn.setEnabled(!isLoading);
    }
}