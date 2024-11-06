package recipe_saver.inti.myapplication;

import android.app.Application;
import  recipe_saver.inti.myapplication.connector.SupabaseConnector;

public class RecipeApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize SupabaseConnector Singleton
        SupabaseConnector.getInstance(this);
    }
}
