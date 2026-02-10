package com.beyondbinary.app.registration;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.beyondbinary.app.R;
import com.beyondbinary.app.api.ApiService;
import com.beyondbinary.app.api.RetrofitClient;
import com.beyondbinary.app.api.UserResponse;
import com.beyondbinary.app.data.database.AppDatabaseHelper;
import com.beyondbinary.app.data.models.User;
import com.beyondbinary.app.onboarding.OnboardingFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileSetupFragment extends Fragment {

    private static final String PREF_NAME = "beyondbinary_prefs";
    public static final String KEY_PROFILE_SETUP_COMPLETED = "profile_setup_completed";

    private TextInputEditText inputUsername;
    private TextInputEditText inputDob;
    private TextInputEditText inputAddress;
    private TextInputEditText inputCaption;
    private MaterialButton btnNext;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_setup, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        inputUsername = view.findViewById(R.id.input_username);
        inputDob = view.findViewById(R.id.input_dob);
        inputAddress = view.findViewById(R.id.input_address);
        inputCaption = view.findViewById(R.id.input_caption);
        btnNext = view.findViewById(R.id.btn_next);

        inputDob.setOnClickListener(v -> showDatePicker());
        btnNext.setOnClickListener(v -> onNextClicked());
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR) - 18;
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    Calendar selected = Calendar.getInstance();
                    selected.set(selectedYear, selectedMonth, selectedDay);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                    inputDob.setText(dateFormat.format(selected.getTime()));
                },
                year, month, day
        );
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void onNextClicked() {
        String username = inputUsername.getText() != null ? inputUsername.getText().toString().trim() : "";
        String dob = inputDob.getText() != null ? inputDob.getText().toString().trim() : "";
        String address = inputAddress.getText() != null ? inputAddress.getText().toString().trim() : "";
        String caption = inputCaption.getText() != null ? inputCaption.getText().toString().trim() : "";

        if (username.isEmpty()) {
            inputUsername.setError("Username is required");
            inputUsername.requestFocus();
            return;
        }

        btnNext.setEnabled(false);
        btnNext.setText("Saving...");

        SharedPreferences prefs = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);

        // Save to local DB
        AppDatabaseHelper dbHelper = AppDatabaseHelper.getInstance(requireContext());
        User user = dbHelper.getUserById(userId);
        if (user == null) {
            user = new User();
            user.setId(userId);
        }
        user.setUsername(username);
        user.setDob(dob);
        user.setAddress(address);
        user.setCaption(caption);
        dbHelper.insertUser(user);

        // Save to backend
        Map<String, String> body = new HashMap<>();
        body.put("username", username);
        body.put("dob", dob);
        body.put("address", address);
        body.put("caption", caption);

        ApiService apiService = RetrofitClient.getApiService();
        apiService.updateUser(userId, body).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(@NonNull Call<UserResponse> call,
                                   @NonNull Response<UserResponse> response) {
                if (!isAdded()) return;

                prefs.edit().putBoolean(KEY_PROFILE_SETUP_COMPLETED, true).apply();
                navigateToOnboarding();
            }

            @Override
            public void onFailure(@NonNull Call<UserResponse> call, @NonNull Throwable t) {
                if (!isAdded()) return;
                // Still proceed even if network fails — data is saved locally
                prefs.edit().putBoolean(KEY_PROFILE_SETUP_COMPLETED, true).apply();
                navigateToOnboarding();
            }
        });
    }

    private void navigateToOnboarding() {
        View bottomNav = requireActivity().findViewById(R.id.bottom_navigation);
        if (bottomNav != null) {
            bottomNav.setVisibility(View.GONE);
        }

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new OnboardingFragment())
                .commit();
    }
}
