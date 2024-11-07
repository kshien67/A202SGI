package recipe_saver.inti.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ImageButton;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.fragment.app.FragmentTransaction;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.android.volley.VolleyError;



import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import recipe_saver.inti.myapplication.connector.RecipeDAO;
import recipe_saver.inti.myapplication.connector.SupabaseConnector;
import recipe_saver.inti.myapplication.interfaces.Recipe;
import recipe_saver.inti.myapplication.interfaces.RecipeImpl;

public class AddRecipeFragment extends Fragment {
    private EditText mRecipeNameEditText, mDescriptionBox, mInstructionsBox;
    private Spinner mCuisineDropdown, mDifficultyDropdown;
    private final RecipeDAO mRecipeDAO = new RecipeDAO(SupabaseConnector.getInstance(getContext()));
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    private SeekBar mTimeNeededSlider;
    private TextView mTimeNeededValue;
    private SeekBar mServingsSlider;
    private TextView mServingsValue;
    private ImageButton mAddPhotoButton;
    private ImageView mSelectedImageView;
    private Bitmap mSelectedImageBitmap;
    private Button mSaveButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        try {
                            Bitmap bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(getActivity().getContentResolver(), imageUri));
                            mAddPhotoButton.setImageBitmap(bitmap);
                            mSelectedImageBitmap = bitmap;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_recipe, container, false);

        // Initialize widgets
        mRecipeNameEditText = view.findViewById(R.id.recipe_name_entry);
        mDescriptionBox = view.findViewById(R.id.description_box);
        mInstructionsBox = view.findViewById(R.id.instructions_box);
        mTimeNeededSlider = view.findViewById(R.id.time_needed_slider);
        mTimeNeededValue = view.findViewById(R.id.time_needed_value);
        mServingsSlider = view.findViewById(R.id.servings_slider);
        mServingsValue = view.findViewById(R.id.servings_value);
        mCuisineDropdown = view.findViewById(R.id.cuisine_dropdown);
        mDifficultyDropdown = view.findViewById(R.id.difficulty_dropdown);
        mAddPhotoButton = view.findViewById(R.id.add_photo_button);
        mSaveButton = view.findViewById(R.id.save_button);

        // Set an OnClickListener on the ImageButton to open the image chooser
        mAddPhotoButton.setOnClickListener(v -> openImageChooser());

        // Set initial values for TextViews based on SeekBar progress
        mTimeNeededValue.setText(getString(R.string.time_needed_placeholder, mTimeNeededSlider.getProgress()));
        mServingsValue.setText(getString(R.string.servings_placeholder, mServingsSlider.getProgress() + 1));

        // Update TextView when timeNeededSlider is adjusted
        mTimeNeededSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mTimeNeededValue.setText(getString(R.string.time_needed_placeholder, progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Update TextView when servingsSlider is adjusted
        mServingsSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mServingsValue.setText(getString(R.string.servings_placeholder, progress + 1));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Set the save button's onClickListener
        mSaveButton.setOnClickListener(v -> {
            // Collect data from UI elements
            String recipeName = mRecipeNameEditText.getText().toString().trim();
            String description = mDescriptionBox.getText().toString().trim();
            String cuisine = mCuisineDropdown.getSelectedItem().toString();
            int timeNeeded = mTimeNeededSlider.getProgress();
            int servings = mServingsSlider.getProgress() + 1;
            String difficulty = mDifficultyDropdown.getSelectedItem().toString().toLowerCase();
            String instructions = mInstructionsBox.getText().toString().trim();

            // Validation: Check if all fields are filled
            if (mSelectedImageBitmap == null || recipeName.isEmpty() || description.isEmpty() ||
                    cuisine.isEmpty() || difficulty.isEmpty() || instructions.isEmpty()) {
                Toast.makeText(getContext(), "Please fill up all the information", Toast.LENGTH_SHORT).show();
                return;
            }

            // Parse ingredients and quantities from instructions
            ArrayList<String> ingredientList = new ArrayList<>();
            ArrayList<String> quantityList = new ArrayList<>();
            parseIngredientsAndQuantities(instructions, ingredientList, quantityList);

            // Create a Recipe object
            Recipe recipe = new RecipeImpl(
                    mSelectedImageBitmap,
                    recipeName,
                    description,
                    timeNeeded,
                    servings,
                    cuisine,
                    difficulty,
                    instructions
            );

            // Use mRecipeDAO.addRecipe and pass the required parameters
            mRecipeDAO.addRecipe(recipe, ingredientList, quantityList, new RecipeDAO.FetchCallback() {
                @Override
                public void onSuccess() {
                    // Handle success
                    Toast.makeText(getContext(), "Recipe saved successfully", Toast.LENGTH_SHORT).show();

                    // Redirect or update UI after saving
                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, new DashboardFragment());
                    transaction.commit();
                }

                @Override
                public void onError(VolleyError error) {
                    // Handle error
                    Toast.makeText(getContext(), "Failed to save recipe: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("AddRecipeFragment", "Failed to save recipe", error);
                }
            });

        });

        return view;
    }

    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        imagePickerLauncher.launch(Intent.createChooser(intent, "Select Picture"));
    }

    // Parse ingredients and quantities from the instructions
    private void parseIngredientsAndQuantities(String instructions, ArrayList<String> ingredientList, ArrayList<String> quantityList) {
        Pattern pattern = Pattern.compile("\\[(\\d+\\s?(g|kg|mg|ton|tons|kilogram|milligram)?)\\[([a-zA-Z\\s]+)\\]]");
        Matcher matcher = pattern.matcher(instructions);

        while (matcher.find()) {
            String quantity = matcher.group(1).trim();
            String unit = matcher.group(2);
            String ingredient = matcher.group(3).trim();

            // Convert all units to grams
            double quantityInGrams = convertToGrams(quantity, unit);

            ingredientList.add(ingredient);
            quantityList.add(String.valueOf(quantityInGrams));
        }
    }

    private double convertToGrams(String quantity, String unit) {
        double value = Double.parseDouble(quantity.replaceAll("[^\\d.]", ""));
        switch (unit.toLowerCase()) {
            case "kg":
            case "kilogram":
                return value * 1000;
            case "mg":
            case "milligram":
                return value / 1000;
            case "ton":
            case "tons":
                return value * 1_000_000;
            default: // Assume grams if no conversion needed
                return value;
        }
    }
}
