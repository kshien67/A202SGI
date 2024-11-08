package recipe_saver.inti.myapplication.connector;

import android.graphics.Bitmap;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class ProfileDAO {

    private static final String TAG = "ProfileDAO";
    private SupabaseConnector mSupabaseConnector;

    public ProfileDAO(SupabaseConnector supabaseConnector) {
        mSupabaseConnector = supabaseConnector;
    }

    public void upsertAvatar(final Bitmap bitmap, final SupabaseConnector.VolleyCallback callback) {
        String url = SupabaseConnector.SUPABASE_URL + "/storage/v1/object/avatar/user_" + SupabaseConnector.userAuthID + "_avatar.png";

        // Convert Bitmap to byte array
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        // Check if avatar exists
        avatarExists(new SupabaseConnector.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                Log.d(TAG, "Avatar exists");
                int method = Request.Method.PUT;

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(method, url, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                callback.onSuccess(response);
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onError(error);
                    }
                }) {
                    @Override
                    public byte[] getBody() {
                        return byteArray;
                    }

                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> headers = new HashMap<>();
                        headers.put("apikey", SupabaseConnector.SUPABASE_KEY);
                        headers.put("Authorization", "Bearer " + SupabaseConnector.accessToken);
                        headers.put("Content-Type", "image/jpeg");
                        return headers;
                    }
                };

                mSupabaseConnector.getRequestQueue().add(jsonObjectRequest);
            }

            @Override
            public void onError(VolleyError error) {
                Log.e(TAG, "Avatar does not exist: " + error.getMessage());
                int method = Request.Method.POST;

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(method, url, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                callback.onSuccess(response);
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onError(error);
                    }
                }) {
                    @Override
                    public byte[] getBody() {
                        return byteArray;
                    }

                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> headers = new HashMap<>();
                        headers.put("apikey", SupabaseConnector.SUPABASE_KEY);
                        headers.put("Authorization", "Bearer " + SupabaseConnector.accessToken);
                        headers.put("Content-Type", "image/jpeg");
                        return headers;
                    }
                };

                mSupabaseConnector.getRequestQueue().add(jsonObjectRequest);
            }
        });
    }

    public void fetchAvatar(final ImageCallback callback) {
        String url = SupabaseConnector.SUPABASE_URL + "/storage/v1/object/avatar/user_" + mSupabaseConnector.userAuthID + "_avatar.png";

        ImageRequest imageRequest = new ImageRequest(url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        callback.onSuccess(response);
                    }
                }, 0, 0, null, Bitmap.Config.RGB_565,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onError(error);
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("apikey", SupabaseConnector.SUPABASE_KEY);
                headers.put("Authorization", "Bearer " + SupabaseConnector.accessToken);
                return headers;
            }
        };

        mSupabaseConnector.getRequestQueue().add(imageRequest);
    }

    public void fetchUserProfile(final VolleyCallback callback) {
        String url = SupabaseConnector.SUPABASE_URL + "/rest/v1/users?select=username,bio&user_auth_id=eq." + SupabaseConnector.userAuthID;
        Log.d(TAG, "Fetch User Profile URL: " + url);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                callback::onSuccess, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Fetch User Profile Error: " + error.getMessage());
                callback.onError(error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("apikey", SupabaseConnector.SUPABASE_KEY);
                headers.put("Authorization", "BEARER " + SupabaseConnector.accessToken);
                return headers;
            }
        };

        mSupabaseConnector.getRequestQueue().add(jsonArrayRequest);
    }

    public void fetchUserDetails(final UserDetailsCallback callback) {
        String urlRecipesCreated = SupabaseConnector.SUPABASE_URL + "/rest/v1/recipe?select=recipe_id,user:user_id(user_auth_id)&user.user_auth_id=eq." + SupabaseConnector.userAuthID;
        String urlRecipesLiked = SupabaseConnector.SUPABASE_URL + "/rest/v1/userrecipeaction?select=user:user_id(user_auth_id)&user.user_auth_id=eq." + SupabaseConnector.userAuthID + "&action_type=eq.Like";
        String urlRecipesCollected = SupabaseConnector.SUPABASE_URL + "/rest/v1/userrecipeaction?select=user:user_id(user_auth_id)&user.user_auth_id=eq." + SupabaseConnector.userAuthID + "&action_type=eq.Collect";

        JsonArrayRequest recipesCreatedRequest = new JsonArrayRequest(Request.Method.GET, urlRecipesCreated, null,
                response -> {
                    int count = response.length();
                    callback.onRecipesCreatedCount(count);
                }, error -> {
                    Log.e(TAG, "Fetch User Details Error: " + error.getMessage());
                    callback.onError(error);
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("apikey", SupabaseConnector.SUPABASE_KEY);
                headers.put("Authorization", "BEARER " + SupabaseConnector.accessToken);
                return headers;
            }
        };

        JsonArrayRequest recipesLikedRequest = new JsonArrayRequest(Request.Method.GET, urlRecipesLiked, null,
                response -> {
                    int count = response.length();
                    callback.onRecipesLikedCount(count);
                }, error -> {
                    Log.e(TAG, "Fetch User Details Error: " + error.getMessage());
                    callback.onError(error);
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("apikey", SupabaseConnector.SUPABASE_KEY);
                headers.put("Authorization", "BEARER " + SupabaseConnector.accessToken);
                return headers;
            }
        };

        JsonArrayRequest recipesCollectedRequest = new JsonArrayRequest(Request.Method.GET, urlRecipesCollected, null,
                response -> {
                    int count = response.length();
                    callback.onRecipesCollectedCount(count);
                }, error -> {
                    Log.e(TAG, "Fetch User Details Error: " + error.getMessage());
                    callback.onError(error);
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("apikey", SupabaseConnector.SUPABASE_KEY);
                headers.put("Authorization", "BEARER " + SupabaseConnector.accessToken);
                return headers;
            }
        };

        mSupabaseConnector.getRequestQueue().add(recipesCreatedRequest);
        mSupabaseConnector.getRequestQueue().add(recipesLikedRequest);
        mSupabaseConnector.getRequestQueue().add(recipesCollectedRequest);
    }

    public void updateUsername(final String newUsername, final SupabaseConnector.VolleyCallback callback) {
        String url = SupabaseConnector.SUPABASE_URL + "/rest/v1/users?user_auth_id=eq." + SupabaseConnector.userAuthID;

        StringRequest stringRequest = new StringRequest(Request.Method.PATCH, url,
                response -> callback.onSuccess(new JSONObject()), callback::onError) {
            @Override
            public byte[] getBody() {
                String requestBody = "{\"username\": \"" + newUsername + "\"}";
                return requestBody.getBytes();
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

    public void updateBio(final String newBio, final SupabaseConnector.VolleyCallback callback) {
        String url = SupabaseConnector.SUPABASE_URL + "/rest/v1/users?user_auth_id=eq." + SupabaseConnector.userAuthID;

        StringRequest stringRequest = new StringRequest(Request.Method.PATCH, url,
                response -> callback.onSuccess(new JSONObject()), callback::onError) {
            @Override
            public byte[] getBody() {
                String requestBody = "{\"bio\": \"" + newBio + "\"}";
                return requestBody.getBytes();
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

    public void avatarExists(final SupabaseConnector.VolleyCallback callback) {
        String url = SupabaseConnector.SUPABASE_URL + "/storage/v1/object/public/avatar/user_" + SupabaseConnector.userAuthID + "_avatar.png";

        StringRequest stringRequest = new StringRequest(Request.Method.HEAD, url,
                response -> callback.onSuccess(new JSONObject()), error -> callback.onError(error)) {
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

    public interface ImageCallback {
        void onSuccess(Bitmap result);

        void onError(VolleyError error);
    }

    public interface VolleyCallback {
        void onSuccess(JSONArray result);

        void onError(VolleyError error);
    }

    public interface UserDetailsCallback {
        void onRecipesCreatedCount(int count);

        void onRecipesCollectedCount(int count);

        void onRecipesLikedCount(int count);

        void onError(VolleyError error);
    }

}