package recipe_saver.inti.myapplication.connector;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class LoginDAO extends SupabaseConnector {

    private static final String TAG = "LoginDAO";

    public LoginDAO(Context context) {
        super(context);
    }

    public void signUp(String email, String password, final VolleyCallback callback) {
        String url = SUPABASE_URL + "/auth/v1/signup";
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("email", email);
            jsonBody.put("password", password);
        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
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
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("apikey", SUPABASE_KEY);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        getRequestQueue().add(jsonObjectRequest);
    }

    public void login(String email, String password, final VolleyCallback callback) {
        String url = SUPABASE_URL + "/auth/v1/token?grant_type=password";
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("email", email);
            jsonBody.put("password", password);
        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            accessToken = response.getString("access_token");
                            JSONObject user = response.getJSONObject("user");
                            userAuthID = user.getString("id");
                            fetchUserID();
                            callback.onSuccess(response);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError(error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("apikey", SUPABASE_KEY);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        getRequestQueue().add(jsonObjectRequest);
    }

    public void logout(String userToken, final VolleyCallback callback) {
        String url = SUPABASE_URL + "/auth/v1/logout";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        accessToken = null;
                        userAuthID = null;
                        userID = null;
                        callback.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError(error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("apikey", SUPABASE_KEY);
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + userToken);
                return headers;
            }
        };

        getRequestQueue().add(jsonObjectRequest);
    }

    private void fetchUserID() {
        Log.d(TAG, "fetchUserID: " + userAuthID);
        String url = SUPABASE_URL + "/rest/v1/users?select=user_id&user_auth_id=eq." + userAuthID;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        JSONObject userObject = response.getJSONObject(0);
                        userID = userObject.getString("user_id");
                        Log.d(TAG, "fetchUserID: " + userID);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Log.e(TAG, "Error fetching user ID: " + error.getMessage())
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("apikey", SUPABASE_KEY);
                headers.put("Authorization", "Bearer " + accessToken);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        getRequestQueue().add(jsonArrayRequest);
    }
}