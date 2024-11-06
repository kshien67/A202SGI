package recipe_saver.inti.myapplication.connector;

import android.content.Context;
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
        String url = SupabaseConnector.SUPABASE_URL + "/storage/v1/object/avatar/user_" + SupabaseConnector.userID + "_avatar.png";

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
                        headers.put("apikey", mSupabaseConnector.SUPABASE_KEY);
                        headers.put("Authorization", "Bearer " + mSupabaseConnector.accessToken);
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
                        headers.put("apikey", mSupabaseConnector.SUPABASE_KEY);
                        headers.put("Authorization", "Bearer " + mSupabaseConnector.accessToken);
                        headers.put("Content-Type", "image/jpeg");
                        return headers;
                    }
                };

                mSupabaseConnector.getRequestQueue().add(jsonObjectRequest);
            }
        });
    }

    public void fetchAvatar(final ImageCallback callback) {
        String url = mSupabaseConnector.SUPABASE_URL + "/storage/v1/object/avatar/user_" + mSupabaseConnector.userID + "_avatar.png";

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

    public void fetchUserDetails(final VolleyCallback callback) {
        String url = SupabaseConnector.SUPABASE_URL + "/rest/v1/users?select=username,bio";
        Log.d(TAG, "Fetch User Details URL: " + url);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        callback.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Fetch User Details Error: " + error.getMessage());
                callback.onError(error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("apikey", mSupabaseConnector.SUPABASE_KEY);
                headers.put("Authorization", "Bearer " + mSupabaseConnector.SUPABASE_KEY);
                return headers;
            }
        };

        mSupabaseConnector.getRequestQueue().add(jsonArrayRequest);
    }

    public void avatarExists(final SupabaseConnector.VolleyCallback callback) {
        String url = SupabaseConnector.SUPABASE_URL + "/storage/v1/object/public/avatar/user_" + SupabaseConnector.userID + "_avatar.png";

        StringRequest stringRequest = new StringRequest(Request.Method.HEAD, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        callback.onSuccess(new JSONObject());
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
}
