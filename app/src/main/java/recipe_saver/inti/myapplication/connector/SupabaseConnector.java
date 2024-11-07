package recipe_saver.inti.myapplication.connector;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import org.json.JSONObject;

public class SupabaseConnector {
    private static SupabaseConnector instance;
    private RequestQueue requestQueue;
    private static Context ctx;
    protected static final String SUPABASE_URL = "https://eectqypapojndpoosfza.supabase.co";
    protected static final String SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImVlY3RxeXBhcG9qbmRwb29zZnphIiwicm9sZSI6ImFub24iLCJpYXQiOjE3Mjg2MzE0MzUsImV4cCI6MjA0NDIwNzQzNX0.6XY2mYEtVrmD9rKyoxjsijLHCNvyv4fQ2qEAAhhrdYg";
    protected static String accessToken = null;
    protected static String userID = null;
    protected static String userAuthID = null;

    protected SupabaseConnector(Context context) {
        ctx = context;
        requestQueue = getRequestQueue();
    }

    public static synchronized SupabaseConnector getInstance(Context context) {
        if (instance == null) {
            instance = new SupabaseConnector(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(ctx.getApplicationContext());
        }
        return requestQueue;
    }

    public interface VolleyCallback {
        void onSuccess(JSONObject result);
        void onError(VolleyError error);
    }
}