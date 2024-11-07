package recipe_saver.inti.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;

import recipe_saver.inti.myapplication.connector.ProfileDAO;
import recipe_saver.inti.myapplication.connector.SupabaseConnector;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;


public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";
    private final ProfileDAO profileDAO = new ProfileDAO(SupabaseConnector.getInstance(getContext()));
    private ImageButton mBackButton;
    private ImageView mProfileImage;
    private ImageButton mEditProfileImage;
    private TextView mUsername;
    private ImageButton mEditUsername;
    private TextView mBio;
    private ImageButton mEditBio;
    private LinearLayout mRecipeLayout;

    private ActivityResultLauncher<Intent> imagePickerLauncher;


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
                            profileDAO.upsertAvatar(bitmap, new SupabaseConnector.VolleyCallback() {
                                @Override
                                public void onSuccess(JSONObject result) {
                                    Log.d(TAG, "Avatar uploaded successfully");
                                    mProfileImage.setImageBitmap(bitmap);
                                }

                                @Override
                                public void onError(VolleyError error) {
                                    Log.e(TAG, "Error uploading avatar: " + error.getMessage());
                                }
                            });
                            mProfileImage.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        mBackButton = v.findViewById(R.id.back_button);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });

        mProfileImage = v.findViewById(R.id.profile_image);
        mEditProfileImage = v.findViewById(R.id.edit_profile_button);
        mUsername = v.findViewById(R.id.username);
        mEditUsername = v.findViewById(R.id.edit_username_button);
        mBio = v.findViewById(R.id.bio);
        mEditBio = v.findViewById(R.id.edit_bio_button);
        mRecipeLayout = v.findViewById(R.id.recipe_layout);

        mEditProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageChooser();
            }
        });

        mEditUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditDialog("Edit Username", mUsername);
            }
        });

        mEditBio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditDialog("Edit Bio", mBio);
            }
        });

        updateProfile();
        populateRecipeLayout();

        return v;
    }

    private void populateRecipeLayout() {
        Log.d(TAG, "Populating recipe layout");
        // Create a list of texts for the CardViews
        List<String[]> cardTexts = Arrays.asList(
                new String[]{"Recipes Created", "0"},
                new String[]{"Recipes Collected", "1"},
                new String[]{"Recipes Liked", "2"}
        );

        LayoutInflater inflater = LayoutInflater.from(getContext());

        for (int i = 0; i < cardTexts.size(); i++) {
            String[] texts = cardTexts.get(i);
            View cardView = inflater.inflate(R.layout.rounded_rectangle_text, mRecipeLayout, false);
            Log.d(TAG, "Adding card view: " + texts[0]);

            TextView titleTextView = cardView.findViewById(R.id.title_text);
            TextView subtitleTextView = cardView.findViewById(R.id.subtitle_text);

            titleTextView.setText(texts[0]);
            subtitleTextView.setText(texts[1]);

            // Set the card background color
            androidx.cardview.widget.CardView card = cardView.findViewById(R.id.card_view);
            if (i % 2 == 0) {
                card.setCardBackgroundColor(getResources().getColor(R.color.orange));
            } else {
                card.setCardBackgroundColor(getResources().getColor(R.color.green));
            }


            mRecipeLayout.addView(cardView);
        }
    }

    private void updateProfile() {
        Log.d(TAG, "Retrieving avatar");
        profileDAO.fetchAvatar(new ProfileDAO.ImageCallback() {
            @Override
            public void onSuccess(Bitmap result) {
                Log.d(TAG, "Avatar retrieved successfully");
                mProfileImage.setImageBitmap(result);
            }

            @Override
            public void onError(VolleyError error) {
                Log.e(TAG, "Error retrieving avatar: " + error.getMessage());
                mProfileImage.setImageResource(R.drawable.ic_temp_avatar);
            }
        });


        profileDAO.fetchUserDetails(new ProfileDAO.VolleyCallback() {
            @Override
            public void onSuccess(JSONArray result) {
                Log.d(TAG, "User details retrieved successfully");

                if (result.length() > 0) {
                    JSONObject userData = result.optJSONObject(0);
                    if (userData != null) {
                        mUsername.setText(userData.optString("username"));
                        String bio = userData.optString("bio");
                        if (bio.isEmpty()) {
                            bio = "The bio will be written here.";
                        }
                        mBio.setText(bio);
                    }
                }
            }

            @Override
            public void onError(VolleyError error) {
                Log.e(TAG, "Error retrieving user details: " + error.getMessage());
            }
        });
    }

    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        imagePickerLauncher.launch(Intent.createChooser(intent, "Select Picture"));
    }

    private void showEditDialog(String title, TextView textView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(title);

        View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edit_text, (ViewGroup) getView(), false);
        final EditText input = viewInflated.findViewById(R.id.edit_text);
        input.setText(textView.getText().toString());

        builder.setView(viewInflated);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                textView.setText(input.getText().toString());
                if (textView == mUsername) {
                    profileDAO.updateUsername(input.getText().toString(), new SupabaseConnector.VolleyCallback() {
                        @Override
                        public void onSuccess(JSONObject result) {
                            Log.d(TAG, "Username updated successfully");
                        }

                        @Override
                        public void onError(VolleyError error) {
                            Log.e(TAG, "Error updating username: " + error.getMessage());
                        }
                    });
                } else if (textView == mBio) {
                    profileDAO.updateBio(input.getText().toString(), new SupabaseConnector.VolleyCallback() {
                        @Override
                        public void onSuccess(JSONObject result) {
                            Log.d(TAG, "Bio updated successfully");
                        }

                        @Override
                        public void onError(VolleyError error) {
                            Log.e(TAG, "Error updating bio: " + error.getMessage());
                        }
                    });
                }
            }
        });

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
}
