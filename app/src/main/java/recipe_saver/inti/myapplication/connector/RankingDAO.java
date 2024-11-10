package recipe_saver.inti.myapplication.connector;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class RankingDAO extends SupabaseConnector {

    public RankingDAO(Context context) {
        super(context);
    }

    public void getTopRankedRecipes(final ArrayCallback callback) {
        String url = SUPABASE_URL + "/rest/v1/rpc/get_top_ranked_recipes";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.POST,
                url,
                null,
                callback::onSuccess,
                callback::onError) {
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

    // Define the ArrayCallback interface (non-public if it's an inner class)
    public interface ArrayCallback {
        void onSuccess(JSONArray response);
        void onError(VolleyError error);
    }
}
