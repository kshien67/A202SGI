package recipe_saver.inti.myapplication.connector;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.VolleyError;
import org.json.JSONObject;

import recipe_saver.inti.myapplication.LoginActivity;
import recipe_saver.inti.myapplication.R;
import recipe_saver.inti.myapplication.connector.LoginDAO;
import recipe_saver.inti.myapplication.connector.SupabaseConnector;

public class SignUpActivity extends AppCompatActivity {

    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private EditText mConfirmPasswordEditText;
    private Button mSignUpButton;
    private Button mLoginLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize UI components
        mEmailEditText = findViewById(R.id.email_edit_text);
        mPasswordEditText = findViewById(R.id.password_edit_text);
        mConfirmPasswordEditText = findViewById(R.id.confirm_password_edit_text);
        mSignUpButton = findViewById(R.id.signup_button);
        mLoginLink = findViewById(R.id.login_link);

        // Set click listener for sign-up button
        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmailEditText.getText().toString().trim();
                String password = mPasswordEditText.getText().toString().trim();
                String confirmPassword = mConfirmPasswordEditText.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(SignUpActivity.this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(SignUpActivity.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    Toast.makeText(SignUpActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Use LoginDAO to sign up with Supabase
                LoginDAO loginDAO = new LoginDAO(SignUpActivity.this);
                loginDAO.signUp(email, password, new SupabaseConnector.VolleyCallback() {
                    @Override
                    public void onSuccess(JSONObject result) {
                        Toast.makeText(SignUpActivity.this, "Sign-up successful! Please log in.", Toast.LENGTH_SHORT).show();
                        // Directly redirect to the login page or optionally login the user automatically
                        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onError(VolleyError error) {
                        if (error.networkResponse != null && error.networkResponse.statusCode == 422) {
                            Toast.makeText(SignUpActivity.this, "Account with this email already exists. Please use a different email.", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(SignUpActivity.this, "Sign-up failed: " + error.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        // Set click listener for login link
        mLoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to LoginActivity when login link is clicked
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}
