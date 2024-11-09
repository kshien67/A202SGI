package recipe_saver.inti.myapplication.connector;

import android.graphics.Bitmap;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONException;

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

    public interface ImageCallback {
        void onSuccess(Bitmap result);

        void onError(VolleyError error);
    }

    public interface StringCallback {
        void onSuccess(String result);
        void onError(VolleyError error);
    }
}
