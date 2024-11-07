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
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
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
            public void onStopTrackingTouch(SeekBar seekBar) {
                submitRecipeData(timeNeededSlider.getProgress(), servingsSlider.getProgress() + 1);
            }
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
            public void onStopTrackingTouch(SeekBar seekBar) {
                submitRecipeData(timeNeededSlider.getProgress(), servingsSlider.getProgress() + 1);
            }
        });

        return view;
    }

    private void submitRecipeData(int timeNeeded, int servings) {
        new Thread(() -> {
            try {
                URL url = new URL(SUPABASE_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Authorization", "Bearer " + SUPABASE_ANON_KEY);
                conn.setDoOutput(true);

                // Create JSON data
                String jsonData = String.format("{\"time_needed\": %d, \"servings\": %d}", timeNeeded, servings);

                // Write JSON data to the request
                OutputStream os = conn.getOutputStream();
                OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
                osw.write(jsonData);
                osw.flush();
                osw.close();
                os.close();

                int responseCode = conn.getResponseCode();
                getActivity().runOnUiThread(() -> {
                    if (responseCode == HttpURLConnection.HTTP_CREATED) {
                        Toast.makeText(getContext(), "Recipe submitted successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Failed to submit recipe", Toast.LENGTH_SHORT).show();
                    }
                });

                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
                getActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "Error submitting recipe", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}
