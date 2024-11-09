package recipe_saver.inti.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.VolleyError;
import org.json.JSONArray;
import org.json.JSONObject;
import recipe_saver.inti.myapplication.connector.RankingDAO;
import recipe_saver.inti.myapplication.connector.VolleyCallback;
import recipe_saver.inti.myapplication.util.ImageLoader;  // A utility class for loading images (e.g., using Picasso or Glide)

public class RankingFragment extends Fragment {

    private RecyclerView rankingRecyclerView;
    private RankingDAO rankingDAO;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ranking, container, false);

        // Initialize components
        rankingRecyclerView = view.findViewById(R.id.recycler_view_ranking);
        rankingRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        rankingDAO = new RankingDAO(getContext());

        // Fetch top-ranked recipes
        fetchRankedRecipes();

        return view;
    }

    private void fetchRankedRecipes() {
        rankingDAO.getTopRankedRecipes(new VolleyCallback() {
            @Override
            public void onSuccess(JSONArray response) {
                // Bind recipes to RecyclerView
                RecipeAdapter adapter = new RecipeAdapter(response);
                rankingRecyclerView.setAdapter(adapter);
            }

            @Override
            public void onError(VolleyError error) {
                Toast.makeText(getContext(), "Failed to load ranking", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {
        private JSONArray recipes;

        public RecipeAdapter(JSONArray recipes) {
            this.recipes = recipes;
        }

        @Override
        public RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_ranking, parent, false);
            return new RecipeViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecipeViewHolder holder, int position) {
            try {
                JSONObject recipe = recipes.getJSONObject(position);
                holder.recipeName.setText(recipe.getString("name"));
                holder.recipeLikes.setText(String.valueOf(recipe.getInt("likes")));
                holder.recipeDescription.setText(recipe.getString("description"));

                // Load the image using a utility class (e.g., Picasso or Glide)
                String imageUrl = recipe.getString("image_url");
                ImageLoader.loadImage(holder.recipeImage, imageUrl);  // Use your image loading utility
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return recipes.length();
        }

        public class RecipeViewHolder extends RecyclerView.ViewHolder {
            ImageView recipeImage;
            TextView recipeName, recipeLikes, recipeDescription;

            public RecipeViewHolder(View itemView) {
                super(itemView);
                recipeImage = itemView.findViewById(R.id.recipe_image);
                recipeName = itemView.findViewById(R.id.recipe_name);
                recipeLikes = itemView.findViewById(R.id.recipe_likes);
                recipeDescription = itemView.findViewById(R.id.recipe_description);
            }
        }
    }
}
