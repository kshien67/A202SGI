package recipe_saver.inti.myapplication;

import android.app.Application;
import  recipe_saver.inti.myapplication.connector.SupabaseConnector;

public class RecipeApplication extends Application{
    private static SupabaseConnector supabaseConnector;
    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize SupabaseConnector Singleton
        supabaseConnector = SupabaseConnector.getInstance(this);
    }

    public static SupabaseConnector getSupabaseConnector() {
        return supabaseConnector;
    }
}
