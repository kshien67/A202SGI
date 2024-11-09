package recipe_saver.inti.myapplication.connector;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
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
        // SQL query to count likes per recipe and get recipe details
        String query =
                "select r.recipe_id, r.recipe_name, r.description, r.image, count(ura.action_type) as like_count " +
                        "from Recipe r " +
                        "left join UserRecipeAction ura on r.recipe_id = ura.recipe_id and ura.action_type = 'like' " +
                        "group by r.recipe_id " +
                        "order by like_count desc";

        String url = SUPABASE_URL + "/rest/v1/RPC";  // Ensure correct endpoint for RPC query

        // Request to fetch the top-ranked recipes
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST, url, new JSONObject().put("query", query),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Safely access the data
                            if (response.has("data")) {
                                JSONArray data = response.getJSONArray("data");
                                callback.onSuccess(data);
                            } else {
                                callback.onError(new VolleyError("Data not found"));
                            }
                        } catch (JSONException e) {
                            // Log the error and pass it to the callback
                            e.printStackTrace();
                            callback.onError(new VolleyError("Error parsing JSON response: " + e.getMessage()));
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle errors like connection issues, etc.
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

        getRequestQueue().add(request);
    }

    // Define the ArrayCallback interface (non-public if it's an inner class)
    public interface ArrayCallback {
        void onSuccess(JSONArray response);
        void onError(VolleyError error);
    }
}
