package recipe_saver.inti.myapplication.connector;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DashboardDAO {

    private static final String TAG = "DashboardDAO";
    private SupabaseConnector mSupabaseConnector;

    public DashboardDAO(SupabaseConnector supabaseConnector) {
        mSupabaseConnector = supabaseConnector;
    }

    public void fetchAvatar(final DashboardDAO.ImageCallback callback) {
        Log.d(TAG, "Fetching avatar " + mSupabaseConnector.userID + mSupabaseConnector.userAuthID);
        String url = mSupabaseConnector.SUPABASE_URL + "/storage/v1/object/avatar/user_" + mSupabaseConnector.userAuthID + "_avatar.png";

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

    public void fetchUsername(final StringCallback callback) {
        String url = SupabaseConnector.SUPABASE_URL + "/rest/v1/users?select=username&user_auth_id=eq." + SupabaseConnector.userAuthID;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url,
                response -> {
                    try {
                        if (response.length() > 0) {
                            String username = response.getJSONObject(0).getString("username");
                            callback.onSuccess(username);
                        } else {
                            callback.onError(new VolleyError("No user found"));
                        }
                    } catch (JSONException e) {
                        callback.onError(new VolleyError("JSON parsing error: " + e.getMessage()));
                    }
                },
                callback::onError) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("apikey", SupabaseConnector.SUPABASE_KEY);
                headers.put("Authorization", "Bearer " + SupabaseConnector.accessToken);
                return headers;
            }
        };

        mSupabaseConnector.getRequestQueue().add(jsonArrayRequest);
    }

    public void fetchDashboardRecipes(@Nullable String cuisine, ArrayCallback callback) {
        String url = SupabaseConnector.SUPABASE_URL + "/rest/v1/rpc/get_dashboard_recipes";

        JSONArray jsonBody = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        try {
            if (cuisine != null) {
                jsonObject.put("cuisine_type", cuisine);
            } else {
                jsonObject.put("cuisine_type", JSONObject.NULL);
            }
            jsonBody.put(jsonObject);
        } catch (JSONException e) {
            callback.onError(new VolleyError("JSON error: " + e.getMessage()));
            return;
        }
        Log.d(TAG, "Request Body: " + jsonBody.toString());

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.POST,
                url,
                jsonBody,
                callback::onSuccess,
                callback::onError) {
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

    public void fetchRecipeImage(String url, ImageCallback callback) {
        ImageRequest imageRequest = new ImageRequest(
                url,
                callback::onSuccess,
                0,
                0,
                ImageView.ScaleType.CENTER_CROP,
                Bitmap.Config.RGB_565,
                callback::onError
        );

        mSupabaseConnector.getRequestQueue().add(imageRequest);
    }

    public interface ImageCallback {
        void onSuccess(Bitmap result);

        void onError(VolleyError error);
    }

    public interface StringCallback {
        void onSuccess(String result);
        void onError(VolleyError error);
    }

    public interface ArrayCallback {
        void onSuccess(JSONArray response);
        void onError(VolleyError error);
    }
}
