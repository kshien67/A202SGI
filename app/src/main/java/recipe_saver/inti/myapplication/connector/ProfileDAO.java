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

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class ProfileDAO {

    private static final String TAG = "ProfileDAO";
    private SupabaseConnector mSupabaseConnector;

    public ProfileDAO(SupabaseConnector supabaseConnector) {
        mSupabaseConnector = supabaseConnector;
    }

    public void upsertAvatar(final SupabaseConnector.VolleyCallback callback) {
        String url = SupabaseConnector.SUPABASE_URL + "/storage/v1/object/avatar/user_" + SupabaseConnector.userID + "_avatar.png";
        JSONObject jsonBody = new JSONObject(); // Assuming you need to send some JSON body

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
                headers.put("apikey", mSupabaseConnector.SUPABASE_KEY);
                headers.put("Authorization", "Bearer " + mSupabaseConnector.accessToken);
                headers.put("Content-Type", "image/jpeg");
                return headers;
            }
        };

        mSupabaseConnector.getRequestQueue().add(jsonObjectRequest);
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

    public interface ImageCallback {
        void onSuccess(Bitmap result);
        void onError(VolleyError error);
    }

    public interface VolleyCallback {
        void onSuccess(JSONArray result);
        void onError(VolleyError error);
    }
}
