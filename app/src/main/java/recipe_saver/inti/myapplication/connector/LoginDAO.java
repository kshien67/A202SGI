package recipe_saver.inti.myapplication.connector;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class LoginDAO extends SupabaseConnector {

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
                            userID = user.getString("id");
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
}