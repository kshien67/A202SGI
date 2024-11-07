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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.IOException;

import recipe_saver.inti.myapplication.connector.RecipeDAO;
import recipe_saver.inti.myapplication.connector.SupabaseConnector;

public class AddRecipeFragment extends Fragment {

    private final RecipeDAO mRecipeDAO = new RecipeDAO(SupabaseConnector.getInstance(getContext()));
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    private SeekBar mTimeNeededSlider;
    private TextView mTimeNeededValue;
    private SeekBar mServingsSlider;
    private TextView mServingsValue;

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
                            /*
                            // Database code to save the image
                            // Update the image in the UI
                            */
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

        // Initialize the SeekBars and TextViews
        mTimeNeededSlider = view.findViewById(R.id.time_needed_slider);
        mTimeNeededValue = view.findViewById(R.id.time_needed_value);
        mServingsSlider = view.findViewById(R.id.servings_slider);
        mServingsValue = view.findViewById(R.id.servings_value);

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

        return view;
    }

    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        imagePickerLauncher.launch(Intent.createChooser(intent, "Select Picture"));
    }
}
