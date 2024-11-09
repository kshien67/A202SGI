package recipe_saver.inti.myapplication;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.VolleyError;
import org.json.JSONObject;

import recipe_saver.inti.myapplication.connector.LoginDAO;
import recipe_saver.inti.myapplication.connector.SupabaseConnector;

public class ForgetPasswordActivity  extends AppCompatActivity {
    private EditText mEmailEditText;
    private Button mResetPasswordButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        mEmailEditText = findViewById(R.id.email_edit_text);
        mResetPasswordButton = findViewById(R.id.reset_password_button);



        mResetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmailEditText.getText().toString().trim();

                if (email.isEmpty()) {
                    Toast.makeText(ForgetPasswordActivity.this, "Please enter your email address", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Use LoginDAO to request password reset with Supabase
                LoginDAO loginDAO = new LoginDAO(ForgetPasswordActivity.this);
                loginDAO.forgotPassword(email, new SupabaseConnector.VolleyCallback() {
                    @Override
                    public void onSuccess(JSONObject result) {
                        Toast.makeText(ForgetPasswordActivity.this, "Reset email sent! Check your inbox.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(VolleyError error) {
                        String errorMsg = "Password reset failed: ";
                        if (error.networkResponse != null) {
                            errorMsg += "Error code: " + error.networkResponse.statusCode;
                            if (error.networkResponse.data != null) {
                                errorMsg += ", Response: " + new String(error.networkResponse.data);
                            }
                        } else {
                            // Log details of the error for further debugging.
                            if (error.getCause() != null) {
                                errorMsg += error.getCause().getMessage();
                            } else {
                                errorMsg += "Unknown error.";
                            }
                            error.printStackTrace();  // This will log the entire error stack trace.
                        }
                        Toast.makeText(ForgetPasswordActivity.this, errorMsg, Toast.LENGTH_LONG).show();              }
                });
            }

        });
    }
}
