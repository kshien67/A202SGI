package recipe_saver.inti.myapplication;

import static java.sql.DriverManager.println;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.io.File;

import recipe_saver.inti.myapplication.connector.ProfileDAO;
import recipe_saver.inti.myapplication.connector.SupabaseConnector;


public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";
    private ImageButton mBackButton;
    private ImageView mProfileImage;

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

        Log.d(TAG, "Retrieving avatar");
        ProfileDAO profileDAO = new ProfileDAO(getContext());
        profileDAO.retrieveAvatar(new ProfileDAO.ImageCallback() {
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

        return v;
    }
}
