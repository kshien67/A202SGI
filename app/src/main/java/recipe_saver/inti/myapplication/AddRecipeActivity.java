package recipe_saver.inti.myapplication;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SeekBar;
import android.widget.TextView;
import android.view.View;

public class AddRecipeActivity extends AppCompatActivity {

    private Button addPhotoButton, saveButton;
    private EditText recipeName, description, instructions;
    private Spinner cuisineSpinner, difficultySpinner;
    private SeekBar timeSeekBar, servingsSeekBar, caloriesSeekBar;
    private TextView timeLabel, servingsLabel, caloriesLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);

//        // Initialize UI elements
//        addPhotoButton = findViewById(R.id.add_photo_button);
//        recipeName = findViewById(R.id.recipe_name);
//        description = findViewById(R.id.description);
//        instructions = findViewById(R.id.instructions);
//        cuisineSpinner = findViewById(R.id.cuisine_spinner);
//        difficultySpinner = findViewById(R.id.difficulty_spinner);
//        timeSeekBar = findViewById(R.id.time_seekbar);
//        servingsSeekBar = findViewById(R.id.servings_seekbar);
//        caloriesSeekBar = findViewById(R.id.calories_seekbar);
//        timeLabel = findViewById(R.id.time_label);
//        servingsLabel = findViewById(R.id.servings_label);
//        caloriesLabel = findViewById(R.id.calories_label);
//        saveButton = findViewById(R.id.save_button);

        // Add photo button click listener
        addPhotoButton.setOnClickListener(view -> {
            // Logic to add photo (e.g., open camera or gallery)
        });

        // Set SeekBar listeners to update the labels
        timeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                timeLabel.setText(progress + " min");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        servingsSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                servingsLabel.setText(progress + " servings");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        caloriesSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                caloriesLabel.setText(progress + " kcal");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Save button click listener
        saveButton.setOnClickListener(view -> {
            // Logic to save the recipe
            String recipe = recipeName.getText().toString();
            String cuisine = cuisineSpinner.getSelectedItem().toString();
            String difficulty = difficultySpinner.getSelectedItem().toString();
            int time = timeSeekBar.getProgress();
            int servings = servingsSeekBar.getProgress();
            int calories = caloriesSeekBar.getProgress();
            String desc = description.getText().toString();
            String instr = instructions.getText().toString();

            // Save logic (e.g., store in database, show a confirmation message)
        });
    }
}
