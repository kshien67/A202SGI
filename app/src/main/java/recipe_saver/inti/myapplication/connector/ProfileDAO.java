package recipe_saver.inti.myapplication.connector;

import android.content.Context;
import android.graphics.Bitmap;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class ProfileDAO extends SupabaseConnector {

    public ProfileDAO(Context context) {
        super(context);
    }

    public void upsertAvatar(final VolleyCallback callback) {
        String url = SUPABASE_URL + "/storage/v1/object/avatar/user_" + userID + "_avatar.png";
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
                headers.put("apikey", SUPABASE_KEY);
                headers.put("Authorization", "Bearer " + accessToken);
                headers.put("Content-Type", "image/jpeg");
                return headers;
            }
        };

        getRequestQueue().add(jsonObjectRequest);
    }

    public void retrieveAvatar(final ImageCallback callback) {
        String url = SUPABASE_URL + "/storage/v1/object/avatar/user_" + userID + "_avatar.png";

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
                headers.put("apikey", SUPABASE_KEY);
                headers.put("Authorization", "Bearer " + accessToken);
                return headers;
            }
        };

        getRequestQueue().add(imageRequest);
    }

    public interface ImageCallback {
        void onSuccess(Bitmap result);
        void onError(VolleyError error);
    }
}
