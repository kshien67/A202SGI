package recipe_saver.inti.myapplication;

import android.content.Intent;
import android.util.Log;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.VolleyError;
import com.google.android.material.imageview.ShapeableImageView;

import recipe_saver.inti.myapplication.connector.DashboardDAO;
import recipe_saver.inti.myapplication.connector.SupabaseConnector;


public class DashboardFragment extends Fragment {

    private static final String TAG = "DashboardFragment";
    private final DashboardDAO mDashboardDAO = new DashboardDAO(SupabaseConnector.getInstance(getContext()));
    private ShapeableImageView mProfileButton;
    private TextView mUsername;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dashboard, container, false);

        mProfileButton = v.findViewById(R.id.dashboard_pfp);
        mUsername = v.findViewById(R.id.dashboard_username);

        mProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new ProfileFragment());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        updateDashboard();
        return v;
    }

    private void updateDashboard() {
        mDashboardDAO.fetchAvatar(new DashboardDAO.ImageCallback() {
            @Override
            public void onSuccess(Bitmap result) {
                mProfileButton.setImageBitmap(result);
            }

            @Override
            public void onError(VolleyError error) {
                Log.e(TAG, "Error fetching avatar: " + error.getMessage());
            }
        });

        mDashboardDAO.fetchUsername(new DashboardDAO.StringCallback() {
            @Override
            public void onSuccess(String result) {
                mUsername.setText(result);
            }

            @Override
            public void onError(VolleyError error) {
                Log.e(TAG, "Error fetching username: " + error.getMessage());
            }
        });
    }
}
