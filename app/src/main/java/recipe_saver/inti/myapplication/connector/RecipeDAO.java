package recipe_saver.inti.myapplication.connector;

import android.graphics.Bitmap;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import recipe_saver.inti.myapplication.interfaces.Ingredient;
import recipe_saver.inti.myapplication.interfaces.IngredientImpl;
import recipe_saver.inti.myapplication.interfaces.Recipe;

public class RecipeDAO {
    private static final String TAG = "RecipeDAO";
    private final SupabaseConnector mSupabaseConnector;

    public RecipeDAO(SupabaseConnector supabaseConnector) {
        this.mSupabaseConnector = supabaseConnector;
    }

    public void addRecipe(Recipe recipe, List<List<Object>> ingredients, FetchCallback callback) {
        String url = SupabaseConnector.SUPABASE_URL + "/rest/v1/recipe";
        JSONObject jsonBody = new JSONObject();

        try {
            jsonBody.put("user_id", SupabaseConnector.userID);
            jsonBody.put("recipe_name", recipe.getRecipeName());
            jsonBody.put("description", recipe.getDescription());
            jsonBody.put("cuisine", recipe.getCuisine());
            jsonBody.put("time_taken", recipe.getTimeTaken());
            jsonBody.put("servings", recipe.getServings());
            jsonBody.put("difficulty", recipe.getDifficulty());
            jsonBody.put("image", bitmapToBase64(recipe.getImage()));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Convert JSONObject to JSONArray
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(jsonBody);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.POST,
                url,
                jsonArray,
                response -> {
                    try {
                        if (response.length() > 0) {
                            JSONObject firstObject = response.getJSONObject(0);
                            int recipeIdResponse = firstObject.getInt("recipe_id");
                            Log.d(TAG, "Recipe added successfully" + recipeIdResponse);

                            upsertRecipeIngredients(recipeIdResponse, ingredients, new FetchCallback() {
                                @Override
                                public void onSuccess() {
                                    Log.d(TAG, "Ingredients upserted successfully");
                                }

                                @Override
                                public void onError(VolleyError error) {
                                    Log.e(TAG, "Failed to upsert ingredients: " + error.getMessage());
                                }
                            });
                            callback.onSuccess();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                callback::onError
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("apikey", SupabaseConnector.SUPABASE_KEY);
                headers.put("Authorization", "Bearer " + SupabaseConnector.accessToken);
                headers.put("Content-Type", "application/json");
                headers.put("Prefer", "return=representation");
                return headers;
            }
        };

        mSupabaseConnector.getRequestQueue().add(jsonArrayRequest);
    }

    public void fetchAllIngredients(IngredientCallback callback) {
        String url = SupabaseConnector.SUPABASE_URL + "/rest/v1/ingredient?select=*";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<IngredientImpl>>() {}.getType();
                    List<IngredientImpl> ingredients = gson.fromJson(response.toString(), listType);
                    Log.d(TAG, "Ingredients fetched successfully");
                    Log.d(TAG, response.toString());
                    Log.d(TAG, ingredients.get(0).getIngredient_name());
                    callback.onSuccess(ingredients);
                },
                callback::onError
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("apikey", SupabaseConnector.SUPABASE_KEY);
                headers.put("Authorization", "Bearer " + SupabaseConnector.accessToken);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        mSupabaseConnector.getRequestQueue().add(jsonArrayRequest);
    }

    private void upsertRecipeIngredients(int recipe_id, List<List<Object>> ingredients, FetchCallback callback) {
        String url = SupabaseConnector.SUPABASE_URL + "/rest/v1/recipeingredient";
        JSONArray jsonArray = new JSONArray();

        try {
            for (List<Object> ingredient : ingredients) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("recipe_id", recipe_id);
                jsonObject.put("ingredient_id", ingredient.get(0));
                jsonObject.put("quantity", ingredient.get(1));
                jsonObject.put("unit", ingredient.get(2));
                jsonArray.put(jsonObject);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.POST,
                url,
                jsonArray,
                response -> {
                    // Handle success
                    System.out.println("Ingredients upserted successfully");
                },
                error -> {
                    // Handle error
                    System.err.println("Failed to upsert ingredients: " + error.getMessage());
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("apikey", SupabaseConnector.SUPABASE_KEY);
                headers.put("Authorization", "Bearer " + SupabaseConnector.accessToken);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        // Add the request to the RequestQueue
        mSupabaseConnector.getRequestQueue().add(jsonArrayRequest);
    }

    public interface IngredientCallback {
        void onSuccess(List<IngredientImpl> ingredients);
        void onError(VolleyError error);
    }

    public interface FetchCallback {
        void onSuccess();
        void onError(VolleyError error);
    }

    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return android.util.Base64.encodeToString(byteArray, android.util.Base64.DEFAULT);
    }
}


/*
Please don't delete the code below, it's used elsewhere in the project
    public void updateRecipe(Recipe recipe, SupabaseConnector.VolleyCallback callback) {
        String url = SupabaseConnector.SUPABASE_URL + "/rest/v1/recipe?recipe_id=eq." + recipe.getRecipeId();
        JSONObject jsonBody = new JSONObject();

        try {
            jsonBody.put("user_id", SupabaseConnector.userID);
            jsonBody.put("image", bitmapToBase64(recipe.getImage()));
            jsonBody.put("recipe_name", recipe.getRecipeName());
            jsonBody.put("description", recipe.getDescription());
            jsonBody.put("cuisine", recipe.getCuisine());
            jsonBody.put("time_taken", recipe.getTimeTaken());
            jsonBody.put("servings", recipe.getServings());
            jsonBody.put("difficulty", recipe.getDifficulty());
        } catch (JSONException e) {
            e.printStackTrace();
        }

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.PATCH,
                url,
                jsonBody,
                callback::onSuccess,
                callback::onError
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("apikey", SupabaseConnector.SUPABASE_KEY);
                headers.put("Authorization", "Bearer " + SupabaseConnector.accessToken);
                headers.put("Content-Type", "application/json");
                headers.put("Prefer", "return=minimal");
                return headers;
            }
        };

        mSupabaseConnector.getRequestQueue().add(jsonObjectRequest);
    }

        public void deleteRecipe(Recipe recipe, SupabaseConnector.VolleyCallback callback) {
        String url = SupabaseConnector.SUPABASE_URL + "/rest/v1/recipe?recipe_id=eq." + recipe.getRecipeId();
        StringRequest stringRequest = new StringRequest(
                Request.Method.DELETE,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Recipe deleted");
                        // Callback expects a JSONObject, not a String
                        // I don't think this is a problem because the response is empty
                        // Implement a new interface method if you need to handle this
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onError(error);
                    }
                }
        ) {
                    @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("apikey", SupabaseConnector.SUPABASE_KEY);
                headers.put("Authorization", "Bearer " + SupabaseConnector.accessToken);
                return headers;
            }
        };
        mSupabaseConnector.getRequestQueue().add(stringRequest);
    }
 */