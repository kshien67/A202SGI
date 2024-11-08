package recipe_saver.inti.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.android.volley.VolleyError;

import org.json.JSONObject;

import recipe_saver.inti.myapplication.connector.LoginDAO;
import recipe_saver.inti.myapplication.connector.SupabaseConnector;

public class SettingsFragment extends Fragment {
    private LoginDAO mLoginDAO;
    private Button mLogoutButton;
    private Button mResetPasswordButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, container, false);

        mLoginDAO = new LoginDAO(getContext());

        mLogoutButton = v.findViewById(R.id.logout);
        mResetPasswordButton = v.findViewById(R.id.reset_password);

        mLogoutButton.setOnClickListener(v1 -> mLoginDAO.logout(new LoginDAO.NoResponseCallback() {
            @Override
            public void onSuccess() {
                Log.d("SettingsFragment", "Logged out successfully");
                // Pop all fragments from the back stack
                getParentFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                // Redirect to login activity
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                getActivity().finish();
            }

            @Override
            public void onError(VolleyError error) {
                Log.e("SettingsFragment", "Error logging out: " + error.getMessage());
            }
        }));

        mResetPasswordButton.setOnClickListener(v1 -> {
            showResetPasswordDialog();
        });

        return v;
    }

    private void showResetPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Reset Password");

        // Set up the input
        final EditText input = new EditText(getActivity());
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", (dialog, which) -> {
            String newPassword = input.getText().toString();
            mLoginDAO.resetPassword(newPassword, new SupabaseConnector.VolleyCallback() {
                @Override
                public void onSuccess(JSONObject result) {
                    Toast.makeText(getActivity(), "Password reset successfully", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(VolleyError error) {
                    Log.e("SettingsFragment", "Error resetting password: " + error.getMessage());
                    Toast.makeText(getActivity(), "Error resetting password", Toast.LENGTH_SHORT).show();
                }
            });
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
}
