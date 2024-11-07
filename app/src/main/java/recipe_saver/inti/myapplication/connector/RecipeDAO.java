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

    public void addRecipe(Recipe recipe, FetchCallback callback) {
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

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                url,
                response -> {
                    Log.d(TAG, "Recipe added successfully");
                    callback.onSuccess();
                },
                callback::onError
        ) {
            @Override
            public byte[] getBody() {
                return jsonBody.toString().getBytes();
            }

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

        mSupabaseConnector.getRequestQueue().add(stringRequest);
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

    public void upsertRecipeIngredient(Recipe recipe, Ingredient ingredient, double grams, FetchCallback callback) {
        String url = SupabaseConnector.SUPABASE_URL + "/rest/v1/recipeingredient";
        JSONObject jsonBody = new JSONObject();

        try {
            jsonBody.put("recipe_id", recipe.getRecipeId());
            jsonBody.put("ingredient_id", ingredient.getIngredientId());
            jsonBody.put("quantity", grams);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonBody,
                response -> {
                    Log.d(TAG, "Ingredient upserted successfully");
                    callback.onSuccess();
                },
                callback::onError
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("apikey", SupabaseConnector.SUPABASE_KEY);
                headers.put("Authorization", "Bearer " + SupabaseConnector.accessToken);
                headers.put("Content-Type", "application/json");
                headers.put("Prefer", "resolution=merge-duplicates");
                return headers;
            }
        };

        mSupabaseConnector.getRequestQueue().add(jsonObjectRequest);
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
