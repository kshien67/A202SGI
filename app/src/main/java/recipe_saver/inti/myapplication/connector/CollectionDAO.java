package recipe_saver.inti.myapplication.connector;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

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

public class CollectionDAO {
    private static final String TAG = "CollectionDAO";
    private final SupabaseConnector mSupabaseConnector;

    public CollectionDAO(SupabaseConnector supabaseConnector) {
        mSupabaseConnector = supabaseConnector;
    }

    public void fetchCollectedRecipes(ArrayCallback callback) {
        String url = SupabaseConnector.SUPABASE_URL + "/rest/v1/rpc/get_collected_recipes";
        Log.d(TAG, "URL: " + url);

        JSONArray jsonBody = new JSONArray();
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userid", SupabaseConnector.userID);
            jsonBody.put(jsonObject);
            Log.d(TAG, "Request Body: " + jsonBody.toString());
        } catch (Exception e) {
            callback.onError(new VolleyError("JSON error: " + e.getMessage()));
            return;
        }

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.POST, url, jsonBody,
                jsonArray -> {
                    Log.d(TAG, "Collected recipes: " + jsonArray.toString());
                    callback.onSuccess(jsonArray);
                },
                error -> {
                    Log.e(TAG, "Error: " + error.toString());
                    if (error.networkResponse != null) {
                        Log.e(TAG, "Status Code: " + error.networkResponse.statusCode);
                        Log.e(TAG, "Response Data: " + new String(error.networkResponse.data));
                    }
                    callback.onError(error);
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("apikey", SupabaseConnector.SUPABASE_KEY);
                headers.put("Authorization", "Bearer " + SupabaseConnector.accessToken);
                Log.d(TAG, "Headers: " + headers);
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

    public interface ArrayCallback {
        void onSuccess(JSONArray response);

        void onError(VolleyError error);
    }

    public interface ImageCallback {
        void onSuccess(Bitmap result);

        void onError(VolleyError error);
    }
}
