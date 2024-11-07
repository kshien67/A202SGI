package recipe_saver.inti.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.VolleyError;
import org.json.JSONObject;

import recipe_saver.inti.myapplication.connector.LoginDAO;
import recipe_saver.inti.myapplication.connector.SupabaseConnector;

public class LoginActivity extends AppCompatActivity{
    private Button mLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mLoginButton = findViewById(R.id.login_button);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginDAO loginDAO = new LoginDAO(LoginActivity.this);
                loginDAO.login("kein.yie@gmail.com", "Pass123", new SupabaseConnector.VolleyCallback() {
                    @Override
                    public void onSuccess(JSONObject result) {
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onError(VolleyError error) {
                        Toast.makeText(LoginActivity.this, "Login failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });
    }
}
