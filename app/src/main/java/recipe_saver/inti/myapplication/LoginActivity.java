package recipe_saver.inti.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.VolleyError;
import org.json.JSONObject;

import recipe_saver.inti.myapplication.connector.ForgetPasswordActivity;
import recipe_saver.inti.myapplication.connector.LoginDAO;
import recipe_saver.inti.myapplication.connector.SignUpActivity;
import recipe_saver.inti.myapplication.connector.SupabaseConnector;

public class LoginActivity extends AppCompatActivity {
    private Button mLoginButton;
    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private TextView mRegisterLink;
    private TextView mForgotPasswordLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize UI components
        mEmailEditText = findViewById(R.id.email_edit_text);
        mPasswordEditText = findViewById(R.id.password_edit_text);
        mLoginButton = findViewById(R.id.login_button);
        mRegisterLink = findViewById(R.id.register_link);
        mForgotPasswordLink = findViewById(R.id.forgot_password_link);

        // Set click listener for login button
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmailEditText.getText().toString().trim();
                String password = mPasswordEditText.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please enter both email and password", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Use LoginDAO to login with Supabase
                LoginDAO loginDAO = new LoginDAO(LoginActivity.this);
                loginDAO.login(email, password, new SupabaseConnector.VolleyCallback() {
                    @Override
                    public void onSuccess(JSONObject result) {
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onError(VolleyError error) {
                        Toast.makeText(LoginActivity.this, "Wrong Email or Password. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // Set click listener for register link
        mRegisterLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to SignUpActivity when register link is clicked
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        // Set click listener for forgot password link
        mForgotPasswordLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
                startActivity(intent);
            }
        });
    }
}
