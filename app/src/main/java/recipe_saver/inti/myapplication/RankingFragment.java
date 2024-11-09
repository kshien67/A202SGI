package recipe_saver.inti.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import recipe_saver.inti.myapplication.connector.SupabaseConnector;
import recipe_saver.inti.myapplication.connector.RecipeDAO;
import recipe_saver.inti.myapplication.interfaces.Recipe;

public class RankingFragment extends Fragment {

    private RecyclerView rankingRecyclerView;
    private RecipeAdapter recipeAdapter;
    private List<Recipe> recipeList = new ArrayList<>();
    private RecipeDAO recipeDAO;

    public RankingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recipeDAO = new RecipeDAO(SupabaseConnector.getInstance(getContext())); // Get instance of SupabaseConnector
        loadRankedRecipes();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ranking, container, false);
        rankingRecyclerView = view.findViewById(R.id.ranking_recycler_view);
        rankingRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recipeAdapter = new RecipeAdapter(recipeList);
        rankingRecyclerView.setAdapter(recipeAdapter);

        return view;
    }

    private void loadRankedRecipes() {
        String url = SupabaseConnector.SUPABASE_URL + "/rest/v1/recipe?select=recipe_name,description,image&order=likes_count.desc"; // Example URL to fetch ranked recipes

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                url,
                response -> {
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject recipeObject = response.getJSONObject(i);
                            String base64Image = recipeObject.getString("image"); // Get Base64 image string
                            byte[] imageBytes = Base64.decode(base64Image, Base64.DEFAULT);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

                            // Create Recipe object and set its values
                            Recipe recipe = new Recipe();
                            recipe.setImage(bitmap);
                            recipe.setRecipeName(recipeObject.getString("recipe_name"));
                            recipe.setDescription(recipeObject.getString("description"));

                            // Add the recipe to the list
                            recipeList.add(recipe);
                        }
                        recipeAdapter.notifyDataSetChanged(); // Update the adapter after data is added
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Error fetching recipes", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(getContext(), "Error loading recipes", Toast.LENGTH_SHORT).show();
                }
        );

        // Add the request to the request queue for Volley
        SupabaseConnector.getInstance(getContext()).getRequestQueue().add(jsonArrayRequest);
    }

    private static class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

        private final List<Recipe> recipes;

        public RecipeAdapter(List<Recipe> recipes) {
            this.recipes = recipes;
        }

        @Override
        public RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ranking_card, parent, false);
            return new RecipeViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecipeViewHolder holder, int position) {
            Recipe recipe = recipes.get(position);
            holder.recipeName.setText(recipe.getRecipeName());
            holder.recipeDescription.setText(recipe.getDescription());
            holder.recipeImage.setImageBitmap(recipe.getImage());
        }

        @Override
        public int getItemCount() {
            return recipes.size();
        }

        public static class RecipeViewHolder extends RecyclerView.ViewHolder {

            TextView recipeName;
            TextView recipeDescription;
            ImageView recipeImage;

            public RecipeViewHolder(View itemView) {
                super(itemView);
                recipeName = itemView.findViewById(R.id.recipe_name);
                recipeDescription = itemView.findViewById(R.id.recipe_description);
                recipeImage = itemView.findViewById(R.id.recipe_image);
            }
        }
    }
}
