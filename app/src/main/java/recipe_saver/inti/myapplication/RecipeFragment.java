package recipe_saver.inti.myapplication;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.VolleyError;
import com.google.android.material.imageview.ShapeableImageView;

import org.json.JSONArray;
import org.json.JSONObject;

import recipe_saver.inti.myapplication.connector.RecipeDAO;
import recipe_saver.inti.myapplication.connector.SupabaseConnector;
import recipe_saver.inti.myapplication.interfaces.IngredientImpl;

public class RecipeFragment extends Fragment {
    private static final String TAG = "RecipeFragment";
    private RecipeDAO mRecipeDAO;
    private String recipeID;
    private String type = "Metric";
    private String mInstructions;

    private ImageButton mBackButton;
    private ShapeableImageView mDeleteButton;
    private ShapeableImageView mCollectButton;
    private ShapeableImageView mLikeButton;
    private ImageButton mConvertButton;
    private ImageView mRecipeImage;
    private TextView mRecipeTitle;
    private TextView mCuisineType;
    private TextView mRecipeDescription;
    private TextView mRecipePrepTime;
    private TextView mRecipeServings;
    private TextView mRecipeCalories;
    private TextView mRecipeDifficulty;
    private TextView mRecipeIngredients;
    private TextView mRecipeInstructions;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_recipe, container, false);

        Bundle bundle = getArguments();
        if (bundle != null) {
            recipeID = bundle.getString("recipe_id");
        }
        mRecipeDAO = new RecipeDAO((SupabaseConnector.getInstance(getContext())));

        mBackButton = v.findViewById(R.id.back_button);
        mDeleteButton = v.findViewById(R.id.share_button);
        mCollectButton = v.findViewById(R.id.collect_button);
        mLikeButton = v.findViewById(R.id.like_button);
        mRecipeImage = v.findViewById(R.id.recipe_image);
        mRecipeTitle = v.findViewById(R.id.recipe_title);
        mCuisineType = v.findViewById(R.id.cuisine_subtitle);
        mRecipeDescription = v.findViewById(R.id.description_text);
        mRecipePrepTime = v.findViewById(R.id.recipe_min_text);
        mRecipeServings = v.findViewById(R.id.recipe_servings_text);
        mRecipeCalories = v.findViewById(R.id.recipe_calories_text);
        mRecipeDifficulty = v.findViewById(R.id.recipe_difficulty_text);
        mRecipeIngredients = v.findViewById(R.id.ingredients_text);
        mRecipeInstructions = v.findViewById(R.id.instructions_text);
        mConvertButton = v.findViewById(R.id.convert_button);


        mBackButton.setOnClickListener(view -> {
            getParentFragmentManager().popBackStack();
        });

        mDeleteButton.setOnClickListener(view -> {

            mRecipeDAO.checkUser(Integer.parseInt(recipeID), new RecipeDAO.BooleanCallback() {
                @Override
                public void onSuccess(Boolean result) {
                    mRecipeDAO.deleteRecipe(Integer.parseInt(recipeID), new RecipeDAO.FetchCallback() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(getContext(), "Recipe Deleted!", Toast.LENGTH_SHORT).show();
                            getParentFragmentManager().popBackStack();
                        }

                        @Override
                        public void onError(VolleyError error) {
                            Log.e(TAG, "Error: " + error.toString());
                            if (error.networkResponse != null) {
                                Log.e(TAG, "Status Code: " + error.networkResponse.statusCode);
                                Log.e(TAG, "Response Data: " + new String(error.networkResponse.data));
                            }
                        }
                    });
                }

                @Override
                public void onError(VolleyError error) {
                    Toast.makeText(getContext(), "User did not create this recipe.", Toast.LENGTH_SHORT).show();
                }
            });

        });

        mLikeButton.setOnClickListener(view -> {
            mRecipeDAO.toggleLikeRecipe(recipeID, new RecipeDAO.BooleanCallback() {
                @Override
                public void onSuccess(Boolean result) {
                    if (result) {
                        Toast.makeText(getContext(), "Recipe Liked!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Recipe Unliked!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(VolleyError error) {
                    Log.e(TAG, "Error: " + error.toString());
                    if (error.networkResponse != null) {
                        Log.e(TAG, "Status Code: " + error.networkResponse.statusCode);
                        Log.e(TAG, "Response Data: " + new String(error.networkResponse.data));
                    }
                }
            });
        });

        mCollectButton.setOnClickListener(view -> {
            mRecipeDAO.toggleCollectRecipe(recipeID, new RecipeDAO.BooleanCallback() {
                @Override
                public void onSuccess(Boolean result) {
                    if (result) {
                        Toast.makeText(getContext(), "Recipe Collected!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Recipe Uncollected!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(VolleyError error) {
                    Log.e(TAG, "Error: " + error.toString());
                    if (error.networkResponse != null) {
                        Log.e(TAG, "Status Code: " + error.networkResponse.statusCode);
                        Log.e(TAG, "Response Data: " + new String(error.networkResponse.data));
                    }
                }
            });
        });

        mRecipeDAO.fetchRecipeDetails(Integer.parseInt(recipeID), new RecipeDAO.ArrayCallback() {
            @Override
            public void onSuccess(JSONArray result) {
                JSONObject recipe = result.optJSONObject(0);
                try {
                    mInstructions = recipe.getString("instructions");

                    mRecipeTitle.setText(recipe.getString("recipe_name"));
                    mRecipeDescription.setText(recipe.getString("description"));
                    mCuisineType.setText(recipe.getString("cuisine"));
                    mRecipePrepTime.setText(recipe.getString("time_taken"));
                    mRecipeServings.setText(recipe.getString("servings"));
                    Log.d(TAG, "Calories: " + recipe.getJSONArray("quantity_calorie_pair").toString());
                    if (recipe.getJSONArray("quantity_calorie_pair").length() == 0) {
                        mRecipeCalories.setText("N/A");
                    } else {
                        mRecipeCalories.setText(IngredientImpl.parseCalories(recipe.getJSONArray("quantity_calorie_pair")));
                    }
                    if (mInstructions != null) {
                        mRecipeInstructions.setText(IngredientImpl.parseIngredients(mInstructions, "Metric"));
                    }

                    mRecipeDAO.fetchRecipeImage(recipe.getString("image"), new RecipeDAO.ImageCallback() {
                        @Override
                        public void onSuccess(Bitmap result) {
                            mRecipeImage.setImageBitmap(result);
                        }

                        @Override
                        public void onError(VolleyError error) {
                            Log.e(TAG, "Error: " + error.toString());
                            if (error.networkResponse != null) {
                                Log.e(TAG, "Status Code: " + error.networkResponse.statusCode);
                                Log.e(TAG, "Response Data: " + new String(error.networkResponse.data));
                            }
                        }
                    });

                    JSONArray ingredientsArray = recipe.getJSONArray("ingredients");
                    StringBuilder ingredientsFormatted = new StringBuilder();
                    for (int i = 0; i < ingredientsArray.length(); i++) {
                        ingredientsFormatted.append("• ").append(ingredientsArray.getString(i)).append("\n");
                    }
                    mRecipeIngredients.setText(ingredientsFormatted.toString().trim());

                    String difficulty = recipe.getString("difficulty");
                    if (!difficulty.isEmpty()) {
                        difficulty = difficulty.substring(0, 1).toUpperCase() + difficulty.substring(1).toLowerCase();
                    }
                    mRecipeDifficulty.setText(difficulty);

                    mConvertButton.setOnClickListener(view -> {
                        if (type.equals("Metric")) {
                            mRecipeInstructions.setText(IngredientImpl.parseIngredients(mInstructions, "Imperial"));
                            type = "Imperial";

                        } else {
                            mRecipeInstructions.setText(IngredientImpl.parseIngredients(mInstructions, "Metric"));
                            type = "Metric";
                        }

                        Toast.makeText(getContext(), "The units have been successfully converted to " + type + ".", Toast.LENGTH_SHORT).show();
                    });
                } catch (Exception e) {
                    Log.e(TAG, "Error: " + e.toString());
                }
            }

            @Override
            public void onError(VolleyError error) {
                Log.e(TAG, "Error: " + error.toString());
                if (error.networkResponse != null) {
                    Log.e(TAG, "Status Code: " + error.networkResponse.statusCode);
                    Log.e(TAG, "Response Data: " + new String(error.networkResponse.data));
                }
            }
        });

        return v;
    }
}
