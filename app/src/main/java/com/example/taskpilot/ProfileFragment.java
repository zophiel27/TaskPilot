package com.example.taskpilot;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    private ImageView ivProfilePic;
    private TextView tvProfileName, tvProfileEmail, tvProfileGender, tvProfileDOB;
    private Switch themeSwitch;
    private Button btnEditProfile;

    private SharedPreferences sharedPreferences;
    private String currentPhotoPath;
    private Uri profileImageUri;
    ActivityResultLauncher<Intent> getImageLauncher;

    private static final String PREFS_NAME = "user_prefs";
    private static final String KEY_NAME = "user_name";
    private static final String KEY_EMAIL = "user_email";
    private static final String KEY_GENDER = "user_gender";
    private static final String KEY_DOB = "user_dob";
    private static final String KEY_DARK_MODE = "dark_mode";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        init(view);
        loadUserData();
        setupThemeSwitch();

        ivProfilePic.setOnClickListener(v ->
                showImagePickerDialog()
        );
        loadProfilePicture();

        btnEditProfile.setOnClickListener(v ->
                showEditProfileDialog()
        );

        return view;
    }

    private void init(View view) {
        ivProfilePic = view.findViewById(R.id.ivProfilePic);
        tvProfileName = view.findViewById(R.id.tvProfileName);
        tvProfileEmail = view.findViewById(R.id.tvProfileEmail);
        tvProfileGender = view.findViewById(R.id.tvProfileGender);
        tvProfileDOB = view.findViewById(R.id.tvProfileDOB);
        themeSwitch = view.findViewById(R.id.themeSwitch);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);

        sharedPreferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        getImageLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                (result)->{
                    if(result.getResultCode() == RESULT_OK && result.getData()!=null)
                    {
                        profileImageUri = result.getData().getData();
                        if (profileImageUri != null) {
                            saveAndDisplayImage();
                        }
                    }
                });
    }

    private void loadUserData() {
        String name = sharedPreferences.getString(KEY_NAME, "Your Name");
        String email = sharedPreferences.getString(KEY_EMAIL, "example@email.com");
        String gender = sharedPreferences.getString(KEY_GENDER, "Not specified");
        String dob = sharedPreferences.getString(KEY_DOB, "DOB: Not set");

        tvProfileName.setText(name);
        tvProfileEmail.setText(email);
        tvProfileGender.setText(gender);
        tvProfileDOB.setText(dob);
    }

    private void setupThemeSwitch() {
        boolean isDarkMode = sharedPreferences.getBoolean(KEY_DARK_MODE, false);
        themeSwitch.setChecked(isDarkMode);

        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean(KEY_DARK_MODE, isChecked).apply();
            applyTheme(isChecked);
        });
    }

    private void applyTheme(boolean isDarkMode) {
        AppCompatDelegate.setDefaultNightMode(
                isDarkMode ? AppCompatDelegate.MODE_NIGHT_YES
                        : AppCompatDelegate.MODE_NIGHT_NO);

        requireActivity().recreate();
    }

    private void showEditProfileDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_edit_profile, null);
        builder.setView(dialogView);

        EditText etName = dialogView.findViewById(R.id.etName);
        EditText etEmail = dialogView.findViewById(R.id.etEmail);
        EditText etGender = dialogView.findViewById(R.id.etGender);
        EditText etDOB = dialogView.findViewById(R.id.etDOB);

        etName.setText(sharedPreferences.getString(KEY_NAME, ""));
        etEmail.setText(sharedPreferences.getString(KEY_EMAIL, ""));
        etGender.setText(sharedPreferences.getString(KEY_GENDER, ""));

        etDOB.setText(sharedPreferences.getString(KEY_DOB, "").replace("DOB: ", ""));
        etDOB.setOnClickListener(v -> showDatePickerDialog(etDOB));

        builder.setPositiveButton("Save", (dialog, which) -> {
            String name = etName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String gender = etGender.getText().toString().trim();
            String dob = etDOB.getText().toString().trim();

            if (name.isEmpty()) {
                Toast.makeText(requireContext(), "Name cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(requireContext(), "Enter a valid email", Toast.LENGTH_SHORT).show();
                return;
            }

            sharedPreferences.edit()
                    .putString(KEY_NAME, name)
                    .putString(KEY_EMAIL, email)
                    .putString(KEY_GENDER, gender)
                    .putString(KEY_DOB, dob.isEmpty() ? "DOB: Not set" : "DOB: " + dob)
                    .apply();

            // updating UI
            loadUserData();
            Toast.makeText(requireContext(), "Profile updated", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showDatePickerDialog(EditText etDOB) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePicker = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    String selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%d", month+1, dayOfMonth, year);
                    etDOB.setText(selectedDate);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePicker.show();
    }

    private void showImagePickerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setCustomTitle(LayoutInflater.from(getContext()).inflate(R.layout.dialog_profile_pic_title, null));

        builder.setItems(new CharSequence[]{"Choose from Gallery", "Remove Photo"},
                (dialog, which) -> {
                    switch (which) {
                        case 0:
                            openGallery();
                            break;
                        case 1:
                            removeProfilePicture();
                            break;
                    }
                });
        builder.show();
    }

    private void openGallery() {
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType("image/*");
        getImageLauncher.launch(i);
    }

    private void removeProfilePicture() {
        sharedPreferences.edit().remove("profile_image_uri").apply();
        ivProfilePic.setImageResource(R.drawable.icon_profile_tab);
        Toast.makeText(requireContext(), "Profile picture removed", Toast.LENGTH_SHORT).show();
    }

    private void loadProfilePicture() {
        String imageUriString = sharedPreferences.getString("profile_image_uri", null);
        if (imageUriString != null) {
            profileImageUri = Uri.parse(imageUriString);
            Glide.with(this)
                    .load(profileImageUri)
                    .circleCrop()
                    .into(ivProfilePic);
        }
    }

    private void saveAndDisplayImage() {
        sharedPreferences.edit()
                .putString("profile_image_uri", profileImageUri.toString())
                .apply();

        Glide.with(this)
                .load(profileImageUri)
                .circleCrop()
                .into(ivProfilePic);
    }

}