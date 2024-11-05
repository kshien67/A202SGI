package recipe_saver.inti.myapplication;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;


public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new DashboardFragment()).commit();
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.navigation_dashboard) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new DashboardFragment()).commit();
            } else if (id == R.id.navigation_ranking) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new RankingFragment()).commit();
            } else if (id == R.id.navigation_add) {
                /* #TODO: THIS SHOULD BE A FRAGMENT
                If you use an activity, it will not be able to access the bottom navigation bar,
                which is a problem because the user will not be able to navigate to other tabs.
                PLEASE ALSO CHANGE THE MANIFEST.XML DECLARATION WHEN YOU'RE DONE

                // Navigate to AddRecipeActivity
                Intent intent = new Intent(HomeActivity.this, AddRecipeActivity.class);
                startActivity(intent);
                */
            } else if (id == R.id.navigation_collection) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
            } else if (id == R.id.navigation_settings) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SettingsFragment()).commit();
            }
            return true;
        });
    }
}
