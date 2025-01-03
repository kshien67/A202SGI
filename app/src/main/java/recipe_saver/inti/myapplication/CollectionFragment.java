package recipe_saver.inti.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Collections;

import recipe_saver.inti.myapplication.connector.CollectionDAO;
import recipe_saver.inti.myapplication.connector.SupabaseConnector;

public class CollectionFragment extends Fragment {
    private static final String TAG = "CollectionFragment";
    private CollectionDAO mCollectionDAO;
    private GridLayout mGridLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCollectionDAO = new CollectionDAO((SupabaseConnector.getInstance(getContext())));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_collection, container, false);

        mGridLayout = v.findViewById(R.id.recipe_grid);


        mCollectionDAO.fetchCollectedRecipes(new CollectionDAO.ArrayCallback() {
            @Override
            public void onSuccess(JSONArray response) {
                Log.d(TAG, "Collected recipes: " + response.toString());
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


        return v;
    }

    private void populateCards(JSONArray recipes) {
        LayoutInflater inflater = LayoutInflater.from(requireActivity());
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
                mCollectionDAO.fetchRecipeImage(url, new CollectionDAO.ImageCallback() {
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
