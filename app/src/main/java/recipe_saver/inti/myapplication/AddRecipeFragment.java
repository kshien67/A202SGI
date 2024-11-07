package recipe_saver.inti.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.io.OutputStreamWriter;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class AddRecipeFragment extends Fragment {
    private static final String SUPABASE_URL = "https://eectqypapojndpoosfza.supabase.co/rest/v1/recipes";
    private static final String SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImVlY3RxeXBhcG9qbmRwb29zZnphIiwicm9sZSI6ImFub24iLCJpYXQiOjE3Mjg2MzE0MzUsImV4cCI6MjA0NDIwNzQzNX0.6XY2mYEtVrmD9rKyoxjsijLHCNvyv4fQ2qEAAhhrdYg";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_recipe, container, false);

        // Initialize the SeekBars and TextViews
        SeekBar timeNeededSlider = view.findViewById(R.id.time_needed_slider);
        TextView timeNeededValue = view.findViewById(R.id.time_needed_value);
        SeekBar servingsSlider = view.findViewById(R.id.servings_slider);
        TextView servingsValue = view.findViewById(R.id.servings_value);

        // Set initial values for TextViews based on SeekBar progress
        timeNeededValue.setText(getString(R.string.time_needed_placeholder, timeNeededSlider.getProgress()));
        servingsValue.setText(getString(R.string.servings_placeholder, servingsSlider.getProgress() + 1)); // Starting at 1 serving

        // Update TextView when timeNeededSlider is adjusted
        timeNeededSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                timeNeededValue.setText(getString(R.string.time_needed_placeholder, progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Update TextView when servingsSlider is adjusted
        servingsSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                servingsValue.setText(getString(R.string.servings_placeholder, progress + 1)); // Add 1 to ensure at least 1 serving
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        return view;
    }
}
