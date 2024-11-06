package recipe_saver.inti.myapplication;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;

import recipe_saver.inti.myapplication.connector.ProfileDAO;
import recipe_saver.inti.myapplication.connector.SupabaseConnector;


public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";
    private final ProfileDAO profileDAO = new ProfileDAO(SupabaseConnector.getInstance(getContext()));
    private ImageButton mBackButton;
    private ImageView mProfileImage;
    private TextView mUsername;
    private TextView mBio;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        mBackButton = v.findViewById(R.id.back_button);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });

        mProfileImage = v.findViewById(R.id.profile_image);
        mUsername = v.findViewById(R.id.username);
        mBio = v.findViewById(R.id.bio);

        updateProfile();

        return v;
    }

    private void updateProfile() {
        Log.d(TAG, "Retrieving avatar");
        profileDAO.fetchAvatar(new ProfileDAO.ImageCallback() {
            @Override
            public void onSuccess(Bitmap result) {
                Log.d(TAG, "Avatar retrieved successfully");
                mProfileImage.setImageBitmap(result);
            }

            @Override
            public void onError(VolleyError error) {
                Log.e(TAG, "Error retrieving avatar: " + error.getMessage());
                mProfileImage.setImageResource(R.drawable.ic_temp_avatar);
            }
        });


        profileDAO.fetchUserDetails(new ProfileDAO.VolleyCallback() {
            @Override
            public void onSuccess(JSONArray result) {
                Log.d(TAG, "User details retrieved successfully");

                if (result.length() > 0) {
                    JSONObject userData = result.optJSONObject(0);
                    if (userData != null) {
                        mUsername.setText(userData.optString("username"));
                        String bio = userData.optString("bio");
                        if (bio.isEmpty()) {
                            bio = "The bio will be written here.";
                        }
                        mBio.setText(bio);
                    }
                }
            }

            @Override
            public void onError(VolleyError error) {
                Log.e(TAG, "Error retrieving user details: " + error.getMessage());
            }
        });
    }
}
