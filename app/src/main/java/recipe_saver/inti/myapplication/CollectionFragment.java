package recipe_saver.inti.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;

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
        LayoutInflater inflater = LayoutInflater.from(getContext());
        for (int i = 0; i < recipes.length(); i++) {
            try {
                View cardView = inflater.inflate(R.layout.card_recipe, mGridLayout, false);
                TextView title = cardView.findViewById(R.id.recipe_name);
                TextView likes = cardView.findViewById(R.id.recipe_likes);
                ImageView image = cardView.findViewById(R.id.recipe_image);

                title.setText(recipes.getJSONObject(i).getString("recipe_name"));
                likes.setText(recipes.getJSONObject(i).getString("like_count"));

                // Decode BYTEA string to byte array
                String byteaString = recipes.getJSONObject(i).getString("image");
                Log.d(TAG, "Image: " + byteaString);
                byte[] imageBytes = android.util.Base64.decode(byteaString, android.util.Base64.DEFAULT);

                // Convert byte array to Bitmap
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

                // Set Bitmap to ImageView
                image.setImageBitmap(bitmap);

                mGridLayout.addView(cardView);
            } catch (JSONException e) {
                Log.e(TAG, "JSON error: " + e.getMessage());
            }
        }

        if (recipes.length() % 2 != 0) {
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
