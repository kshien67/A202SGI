package recipe_saver.inti.myapplication.connector;
import android.content.Intent;
import android.net.Uri;
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
import recipe_saver.inti.myapplication.connector.SupabaseConnector;
public class ResetPasswordActivity extends AppCompatActivity {

    private EditText mNewPasswordEditText;
    private EditText mConfirmPasswordEditText;
    private Button mResetPasswordButton;
    private String accessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        // Get the access token from the deep link
        Uri data = getIntent().getData();
        if (data != null) {
            accessToken = data.getQueryParameter("access_token");
        }


        mNewPasswordEditText = findViewById(R.id.new_password_edit_text);
        mConfirmPasswordEditText = findViewById(R.id.confirm_password_edit_text);
        mResetPasswordButton = findViewById(R.id.reset_password_button);

        mResetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPassword = mNewPasswordEditText.getText().toString().trim();
                String confirmPassword = mConfirmPasswordEditText.getText().toString().trim();

                if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(ResetPasswordActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!newPassword.equals(confirmPassword)) {
                    Toast.makeText(ResetPasswordActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Reset the password using the access token
                resetPassword(newPassword);
            }
        });
    }

    private void resetPassword(String newPassword) {
        LoginDAO loginDAO = new LoginDAO(ResetPasswordActivity.this);
        loginDAO.resetPassword(accessToken, newPassword, new SupabaseConnector.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                Toast.makeText(ResetPasswordActivity.this, "Password reset successful! You can now log in.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(VolleyError error) {
                Toast.makeText(ResetPasswordActivity.this, "Failed to reset password: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
