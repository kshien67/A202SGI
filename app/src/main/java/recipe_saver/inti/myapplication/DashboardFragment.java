package recipe_saver.inti.myapplication;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.VolleyError;
import com.google.android.material.imageview.ShapeableImageView;

import org.json.JSONArray;

import recipe_saver.inti.myapplication.connector.CollectionDAO;
import recipe_saver.inti.myapplication.connector.DashboardDAO;
import recipe_saver.inti.myapplication.connector.SupabaseConnector;


public class DashboardFragment extends Fragment {

    private static final String TAG = "DashboardFragment";
    private final DashboardDAO mDashboardDAO = new DashboardDAO(SupabaseConnector.getInstance(getContext()));

    private ShapeableImageView mProfileButton;
    private TextView mUsername;
    private GridLayout mGridLayout;
    private ImageButton mChineseButton;
    private ImageButton mWesternButton;
    private ImageButton mJapaneseButton;
    private ImageButton mIndianButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dashboard, container, false);

        mProfileButton = v.findViewById(R.id.dashboard_pfp);
        mUsername = v.findViewById(R.id.dashboard_username);
        mGridLayout = v.findViewById(R.id.recipe_grid);
        mChineseButton = v.findViewById(R.id.button_chinese_cuisine);
        mWesternButton = v.findViewById(R.id.button_western_cuisine);
        mJapaneseButton = v.findViewById(R.id.button_japanese_cuisine);
        mIndianButton = v.findViewById(R.id.button_indian_cuisine);

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
        mChineseButton.setOnClickListener(v1 -> updateRecipes("Chinese"));
        mWesternButton.setOnClickListener(v1 -> updateRecipes("Western"));
        mJapaneseButton.setOnClickListener(v1 -> updateRecipes("Japanese"));
        mIndianButton.setOnClickListener(v1 -> updateRecipes("Indian"));
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

        updateRecipes(null);
    }

    private void updateRecipes(@Nullable String cuisine) {
        mGridLayout.removeAllViews();

        mDashboardDAO.fetchDashboardRecipes(cuisine, new DashboardDAO.ArrayCallback() {
            @Override
            public void onSuccess(JSONArray response) {
                Log.d(TAG, "Recipes: " + response.toString());
                populateCards(response);
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
    }

    private void populateCards(JSONArray recipes) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        for (int i = 0; i < recipes.length(); i++) {
            try {
                View cardView = inflater.inflate(R.layout.card_recipe, mGridLayout, false);
                TextView title = cardView.findViewById(R.id.recipe_name);
                TextView likes = cardView.findViewById(R.id.recipe_likes);
                ImageButton image = cardView.findViewById(R.id.recipe_image);


                title.setText(recipes.getJSONObject(i).getString("recipe_name"));
                likes.setText(recipes.getJSONObject(i).getString("like_count"));

                String recipeId = recipes.getJSONObject(i).getString("recipe_id");
                String url = recipes.getJSONObject(i).getString("image");
                mDashboardDAO.fetchRecipeImage(url, new DashboardDAO.ImageCallback() {
                    @Override
                    public void onSuccess(Bitmap bitmap) {image.setImageBitmap(bitmap);}

                    @Override
                    public void onError(VolleyError error) {
                        Log.e(TAG, "Error: " + error.toString());
                        if (error.networkResponse != null) {
                            Log.e(TAG, "Status Code: " + error.networkResponse.statusCode);
                            Log.e(TAG, "Response Data: " + new String(error.networkResponse.data));
                        }
                    }
                });

                image.setOnClickListener(v -> {
                    Bundle bundle = new Bundle();
                    bundle.putString("recipe_id", recipeId);

                    RecipeFragment recipeFragment = new RecipeFragment();
                    recipeFragment.setArguments(bundle);

                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, recipeFragment)
                            .addToBackStack(null)
                            .commit();
                });

                //Log.d(TAG, "Added card: " + cardView.toString());
                //Log.d(TAG, "Likes: " + likes.getText());
                //Log.d(TAG, "Image: " + url);
                mGridLayout.addView(cardView);
            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
            }
        }

        if (recipes.length() % 2 != 0) {
            Log.d(TAG, "Adding empty view");
            View emptyView = new View(getContext());
            GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
            layoutParams.width = 0;
            layoutParams.height = 0;
            layoutParams.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1, 1f);
            emptyView.setLayoutParams(layoutParams);
            mGridLayout.addView(emptyView);
        }
    }
}
