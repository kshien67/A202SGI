package recipe_saver.inti.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.VolleyError;
import org.json.JSONArray;
import org.json.JSONObject;
import recipe_saver.inti.myapplication.connector.RankingDAO;

import java.util.ArrayList;
import com.squareup.picasso.Picasso;


public class RankingFragment extends Fragment {

    private static final String TAG = "RankingFragment";
    private RecyclerView mRecyclerView;
    private RankingAdapter mRankingAdapter;
    private ArrayList<Recipe> mRankingList = new ArrayList<>();
    private RankingDAO mRankingDAO;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRankingDAO = new RankingDAO(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout
        View rootView = inflater.inflate(R.layout.fragment_ranking, container, false);

        // Set up RecyclerView
        mRecyclerView = rootView.findViewById(R.id.recycler_view_ranking);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRankingAdapter = new RankingAdapter(mRankingList);
        mRecyclerView.setAdapter(mRankingAdapter);

        // Fetch recipes data
        fetchRankingData();

        return rootView;
    }

    private void fetchRankingData() {
        mRankingDAO.getTopRankedRecipes(new RankingDAO.ArrayCallback() {
            @Override
            public void onSuccess(JSONArray response) {
                mRankingList.clear();
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject recipeData = response.getJSONObject(i);
                        String recipeId = recipeData.optString("recipe_id");
                        String recipeName = recipeData.optString("recipe_name");
                        String description = recipeData.optString("description");
                        int likeCount = recipeData.optInt("like_count");
                        String imageUrl = recipeData.optString("image");

                        Recipe recipe = new Recipe(recipeId, recipeName, description, likeCount, imageUrl);
                        Log.d(TAG, "Recipe: " + recipe.getRecipeId());
                        mRankingList.add(recipe);
                    }
                    mRankingAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing ranking data: " + e.getMessage());
                }
            }

            @Override
            public void onError(VolleyError error) {
                Log.e(TAG, "Error fetching ranking data: " + error.getMessage());
            }
        });
    }

    private static class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.RankingViewHolder> {

        private final ArrayList<Recipe> mRecipeList;

        public RankingAdapter(ArrayList<Recipe> recipeList) {
            this.mRecipeList = recipeList;
        }

        @Override
        public RankingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ranking_card, parent, false);
            return new RankingViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RankingViewHolder holder, int position) {
            Recipe recipe = mRecipeList.get(position);
            holder.rank.setText(String.valueOf(position + 1));
            holder.recipeName.setText(recipe.getRecipeName());
            holder.recipeDescription.setText(recipe.getDescription());
            holder.likes.setText(String.valueOf(recipe.getLikeCount()));

            holder.itemView.setOnClickListener(v -> {
                Bundle bundle = new Bundle();
                bundle.putString("recipe_id", recipe.getRecipeId());

                RecipeFragment recipeFragment = new RecipeFragment();
                recipeFragment.setArguments(bundle);

                // Replace the current fragment with RecipeFragment and add to back stack
                if (v.getContext() instanceof AppCompatActivity) {
                    AppCompatActivity activity = (AppCompatActivity) v.getContext();
                    activity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, recipeFragment)
                            .addToBackStack(null)
                            .commit();
                }

            });

            // Optionally load image (use Picasso or Glide)
            if (!recipe.getImageUrl().isEmpty()) {
                Picasso.get().load(recipe.getImageUrl()).into(holder.recipeImage);
            }
        }

        @Override
        public int getItemCount() {
            return mRecipeList.size();
        }

        public static class RankingViewHolder extends RecyclerView.ViewHolder {
            TextView rank, recipeName, recipeDescription, likes;
            ImageView recipeImage;

            public RankingViewHolder(View itemView) {
                super(itemView);
                rank = itemView.findViewById(R.id.rank);
                recipeName = itemView.findViewById(R.id.recipe_name);
                recipeDescription = itemView.findViewById(R.id.recipe_description);
                likes = itemView.findViewById(R.id.recipe_likes);
                recipeImage = itemView.findViewById(R.id.recipe_image);
            }
        }
    }

    // Recipe class to store recipe details
    private static class Recipe {
        private String recipeId;
        private String recipeName;
        private String description;
        private int likeCount;
        private String imageUrl;

        public Recipe(String recipeId, String recipeName, String description, int likeCount, String imageUrl) {
            this.recipeId = recipeId;
            this.recipeName = recipeName;
            this.description = description;
            this.likeCount = likeCount;
            this.imageUrl = imageUrl;
        }

        public String getRecipeId() {
            return recipeId;
        }

        public String getRecipeName() {
            return recipeName;
        }

        public String getDescription() {
            return description;
        }

        public int getLikeCount() {
            return likeCount;
        }

        public String getImageUrl() {
            return imageUrl;
        }
    }
}
