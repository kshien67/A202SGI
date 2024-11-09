package recipe_saver.inti.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.VolleyError;
import com.google.android.material.imageview.ShapeableImageView;

import recipe_saver.inti.myapplication.connector.RecipeDAO;
import recipe_saver.inti.myapplication.connector.SupabaseConnector;

public class RecipeFragment extends Fragment {
    private static final String TAG = "RecipeFragment";
    private RecipeDAO mRecipeDAO;
    private String recipeID;

    private ImageButton mBackButton;
    private ShapeableImageView mShareButton;
    private ShapeableImageView mCollectButton;
    private ShapeableImageView mLikeButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_recipe, container, false);

        Bundle bundle = getArguments();
        if (bundle != null) {
            recipeID = bundle.getString("recipe_id");
        }
        mRecipeDAO = new RecipeDAO((SupabaseConnector.getInstance(getContext())));

        mBackButton = v.findViewById(R.id.back_button);
        mShareButton = v.findViewById(R.id.share_button);
        mCollectButton = v.findViewById(R.id.collect_button);
        mLikeButton = v.findViewById(R.id.like_button);

        mBackButton.setOnClickListener(view -> {
            getParentFragmentManager().popBackStack();
        });

        mLikeButton.setOnClickListener(view -> {
            mRecipeDAO.toggleLikeRecipe(recipeID, new RecipeDAO.BooleanCallback() {
                @Override
                public void onSuccess(Boolean result) {
                    if (result) {
                        Toast.makeText(getContext(), "Recipe Liked!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Recipe Unliked!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(VolleyError error) {
                    Log.e(TAG, "Error: " + error.toString());
                    if (error.networkResponse != null) {
                        Log.e(TAG, "Status Code: " + error.networkResponse.statusCode);
                        Log.e(TAG, "Response Data: " + new String(error.networkResponse.data));
                    }
                }
            });
        });

        mCollectButton.setOnClickListener(view -> {
            mRecipeDAO.toggleCollectRecipe(recipeID, new RecipeDAO.BooleanCallback() {
                @Override
                public void onSuccess(Boolean result) {
                    if (result) {
                        Toast.makeText(getContext(), "Recipe Collected!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Recipe Uncollected!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(VolleyError error) {
                    Log.e(TAG, "Error: " + error.toString());
                    if (error.networkResponse != null) {
                        Log.e(TAG, "Status Code: " + error.networkResponse.statusCode);
                        Log.e(TAG, "Response Data: " + new String(error.networkResponse.data));
                    }
                }
            });
        });

        return v;
    }
}
