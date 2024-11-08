package recipe_saver.inti.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import recipe_saver.inti.myapplication.connector.RecipeDAO;
import recipe_saver.inti.myapplication.connector.SupabaseConnector;
import recipe_saver.inti.myapplication.interfaces.IngredientImpl;
import recipe_saver.inti.myapplication.interfaces.MeasurementUnitImpl;
import recipe_saver.inti.myapplication.interfaces.Recipe;
import recipe_saver.inti.myapplication.interfaces.RecipeImpl;
import recipe_saver.inti.myapplication.interfaces.EditTextCursorWatcher;
import recipe_saver.inti.myapplication.interfaces.MeasurementUnit;

public class AddRecipeFragment extends Fragment {
    private final static String TAG = "AddRecipeFragment";
    private List<IngredientImpl> mIngredients;

    private EditText mRecipeNameEditText, mDescriptionBox;
    private EditTextCursorWatcher mInstructionsBox;
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

    @SuppressLint("ClickableViewAccessibility")
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

        mInstructionsBox.setOnSelectionChangedListener((selStart, selEnd, firstPipe, secondPipe, end_index) -> {
            if (selEnd>=2 && selEnd <=secondPipe && selEnd > firstPipe) {
                showDropdown(mInstructionsBox, selEnd, "units");
            } else if (selEnd>secondPipe && selEnd <end_index) {
                Log.d(TAG, "Ingredient Popup: " + selEnd);
                showDropdown(mInstructionsBox, selEnd, "ingredients");
            }
        });

        mRecipeDAO.fetchAllIngredients(new RecipeDAO.IngredientCallback() {
            @Override
            public void onSuccess(List<IngredientImpl> ingredients) {
                Log.d(TAG, "Fetched ingredients successfully");
                Log.d(TAG, "Ingredients: " + ingredients);
                mIngredients = ingredients;
            }

            @Override
            public void onError(VolleyError error) {
                Log.e(TAG, "Failed to fetch ingredients", error);
            }
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
            List<List<Object>> recipeIngredients = parseIngredientsAndQuantities();
            Log.d(TAG, "Recipe ingredients: " + recipeIngredients);

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


            mRecipeDAO.addRecipe(recipe, recipeIngredients,new RecipeDAO.FetchCallback() {
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
    private List<List<Object>> parseIngredientsAndQuantities() {
        String instructions = mInstructionsBox.getText().toString();
        List<List<Object>> result = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\[\\[(\\d+(\\.\\d+)?)\\|(\\w+)\\|(.*?)\\]\\]");
        Matcher matcher = pattern.matcher(instructions);

        while (matcher.find()) {
            String quantityStr = matcher.group(1);
            String unit = matcher.group(3);
            String ingredientName = matcher.group(4);

            // Validate quantity
            double quantity;
            try {
                quantity = Double.parseDouble(quantityStr);
            } catch (NumberFormatException e) {
                continue; // Skip invalid quantity
            }

            // Validate unit
            if (!Arrays.asList(MeasurementUnit.getUnits()).contains(unit)) {
                continue; // Skip invalid unit
            }

            // Validate ingredient
            IngredientImpl ingredient = null;
            for (IngredientImpl ing : mIngredients) {
                if (ing.getIngredient_name().equalsIgnoreCase(ingredientName)) {
                    ingredient = ing;
                    break;
                }
            }
            if (ingredient == null) {
                continue; // Skip invalid ingredient
            }

            // Create MeasurementUnit
            MeasurementUnit measurementUnit = new MeasurementUnitImpl(quantity, unit);

            // Add to result list
            List<Object> entry = new ArrayList<>();
            entry.add(ingredient.getIngredient_id());
            entry.add(measurementUnit.toGrams());
            entry.add(measurementUnit.getDefaultUnit());
            result.add(entry);
        }
        return result;
    }

    private void showDropdown(View anchor, int position, String type) {
        List<String> options = new ArrayList<>();
        if (type.equals("units")) {
            Log.d(TAG, "showDropdown: units");
            Log.d(TAG, "showDropdown: " + MeasurementUnit.getUnits());
            Collections.addAll(options, MeasurementUnit.getUnits());
        } else if (type.equals("ingredients")) {
            for (IngredientImpl ingredient : mIngredients) {
                Log.d(TAG, "showDropdown: " + ingredient.getIngredient_name());
                options.add(ingredient.getIngredient_name());
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, options);
        ListView listView = new ListView(getContext());
        listView.setAdapter(adapter);

        PopupWindow popupWindow = new PopupWindow(listView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);

        listView.setOnItemClickListener((parent, view, position1, id) -> {
            String selectedItem = options.get(position1);
            Editable text = mInstructionsBox.getText();
            int start = Math.min(mInstructionsBox.getSelectionStart(), mInstructionsBox.getSelectionEnd());
            int end = Math.max(mInstructionsBox.getSelectionStart(), mInstructionsBox.getSelectionEnd());
            text.replace(start, end, selectedItem);
            popupWindow.dismiss();
        });

        popupWindow.showAsDropDown(anchor, 0, 0);
    }
}
