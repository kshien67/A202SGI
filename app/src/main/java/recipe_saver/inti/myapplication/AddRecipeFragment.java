package recipe_saver.inti.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Bundle;
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
import org.json.JSONObject;
import com.android.volley.VolleyError;



import java.io.IOException;

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
            String difficulty = mDifficultyDropdown.getSelectedItem().toString();
            String instructions = mInstructionsBox.getText().toString().trim();

            // Validation: Check if all fields are filled
            if (mSelectedImageBitmap == null || recipeName.isEmpty() || description.isEmpty() ||
                    cuisine.isEmpty() || difficulty.isEmpty() || instructions.isEmpty()) {
                Toast.makeText(getContext(), "Please fill up all the information", Toast.LENGTH_SHORT).show();
                return;
            }

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
            mRecipeDAO.addRecipe(recipe, new SupabaseConnector.VolleyCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    // Handle the success case with the response object
                    Toast.makeText(getContext(), "Recipe saved successfully", Toast.LENGTH_SHORT).show();

                    // Redirect or update UI after saving
                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, new DashboardFragment()); // Replace with your dashboard fragment
                    transaction.commit();
                }

                @Override
                public void onError(VolleyError error) {
                    // Handle the error case using VolleyError
                    Toast.makeText(getContext(), "Failed to save recipe: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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
}
