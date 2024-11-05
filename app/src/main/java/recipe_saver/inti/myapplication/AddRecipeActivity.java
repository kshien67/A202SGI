package recipe_saver.inti.myapplication;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import recipe_saver.inti.myapplication.DatabaseHelper;

public class AddRecipeActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private EditText recipeName, description, instructions;
    private Spinner cuisineSpinner, difficultySpinner;
    private SeekBar timeSeekBar, servingsSeekBar, caloriesSeekBar;
    private TextView timeLabel, servingsLabel, caloriesLabel;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);

        dbHelper = new DatabaseHelper(this);

        // Initialize UI elements
        recipeName = findViewById(R.id.recipe_name_entry);
        description = findViewById(R.id.description_box);
        cuisineSpinner = findViewById(R.id.cuisine_dropdown);
        difficultySpinner = findViewById(R.id.difficulty_dropdown);
        timeSeekBar = findViewById(R.id.time_needed_slider);
        servingsSeekBar = findViewById(R.id.servings_slider);
        caloriesSeekBar = findViewById(R.id.calories_slider);
        /* #TODO: Dude where are these labels? Uncomment these lines when you add the ID
        timeLabel = findViewById(R.id.time_label);
        servingsLabel = findViewById(R.id.servings_label);
        caloriesLabel = findViewById(R.id.calories_label);
        */
        saveButton = findViewById(R.id.save_button);

        // Set SeekBar listeners to update the labels
        timeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // timeLabel.setText(progress + " min"); TODO: Uncomment this line when you add the ID
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        servingsSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // servingsLabel.setText(progress + " servings"); TODO: Uncomment this line when you add the ID
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        caloriesSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // caloriesLabel.setText(progress + " kcal"); #TODO: Uncomment this line when you add the ID
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Save button click listener
        saveButton.setOnClickListener(view -> saveRecipe());
    }

    private void saveRecipe() {
        String recipeNameText = recipeName.getText().toString();
        String descriptionText = description.getText().toString();
        String cuisine = cuisineSpinner.getSelectedItem().toString();
        String difficulty = difficultySpinner.getSelectedItem().toString();
        int time = timeSeekBar.getProgress();
        int servings = servingsSeekBar.getProgress();
        int calories = caloriesSeekBar.getProgress();
        byte[] image = null; // You can implement image selection functionality here if needed

        // Insert the recipe into the database
        /* #TODO: You are missing the user ID. Uncomment this when you add the ID
        long recipeId = dbHelper.insertRecipe(recipeNameText, descriptionText, cuisine, time, servings, calories, difficulty, image);
        if (recipeId > 0) {
            Toast.makeText(this, "Recipe saved successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to save recipe", Toast.LENGTH_SHORT).show();
        }
        */
    }
}
