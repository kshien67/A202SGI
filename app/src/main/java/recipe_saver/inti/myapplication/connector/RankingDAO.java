package recipe_saver.inti.myapplication.connector;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import org.json.JSONArray;
import java.util.HashMap;
import java.util.Map;

public class RankingDAO extends SupabaseConnector {

    public RankingDAO(Context context) {
        super(context);
    }

    // Method to fetch top-ranked recipes
    public void getTopRankedRecipes(final VolleyCallback callback) {
        String url = SUPABASE_URL + "/rest/v1/RecipeRanking";

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        callback.onSuccess(response);
                    }
                },
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
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        getRequestQueue().add(request);
    }
}
