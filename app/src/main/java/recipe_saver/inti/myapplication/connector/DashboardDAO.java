package recipe_saver.inti.myapplication.connector;

import android.graphics.Bitmap;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;

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

    public interface ImageCallback {
        void onSuccess(Bitmap result);

        void onError(VolleyError error);
    }
}
