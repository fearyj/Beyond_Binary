package com.beyondbinary.app.registration;

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

import com.beyondbinary.app.HomeFragment;
import com.beyondbinary.app.R;
import com.beyondbinary.app.api.ApiService;
import com.beyondbinary.app.api.CreateUserResponse;
import com.beyondbinary.app.api.RetrofitClient;
import com.beyondbinary.app.onboarding.OnboardingFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrationFragment extends Fragment {

    private static final String PREF_NAME = "beyondbinary_prefs";

    private TextInputEditText inputEmail;
    private MaterialButton btnContinue;
    private View btnGoogle;
    private View btnApple;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_registration, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        inputEmail = view.findViewById(R.id.input_email);
        btnContinue = view.findViewById(R.id.btn_continue);
        btnGoogle = view.findViewById(R.id.btn_google);
        btnApple = view.findViewById(R.id.btn_apple);

        btnContinue.setOnClickListener(v -> onContinueClicked());
        btnGoogle.setOnClickListener(v ->
                Toast.makeText(getContext(), "Coming soon!", Toast.LENGTH_SHORT).show());
        btnApple.setOnClickListener(v ->
                Toast.makeText(getContext(), "Coming soon!", Toast.LENGTH_SHORT).show());
    }

    private void onContinueClicked() {
        String email = inputEmail.getText() != null ? inputEmail.getText().toString().trim() : "";

        if (email.isEmpty() || !email.contains("@")) {
            inputEmail.setError("Please enter a valid email");
            inputEmail.requestFocus();
            return;
        }

        btnContinue.setEnabled(false);
        btnContinue.setText("Loading...");

        Map<String, String> body = new HashMap<>();
        body.put("email", email);

        ApiService apiService = RetrofitClient.getApiService();
        apiService.createUser(body).enqueue(new Callback<CreateUserResponse>() {
            @Override
            public void onResponse(@NonNull Call<CreateUserResponse> call,
                                   @NonNull Response<CreateUserResponse> response) {
                if (!isAdded()) return;

                if (response.isSuccessful() && response.body() != null) {
                    CreateUserResponse result = response.body();
                    int userId = result.getUserId();

                    SharedPreferences prefs = requireContext()
                            .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
                    prefs.edit().putInt("user_id", userId).apply();

                    // Check if existing user with bio (already onboarded)
                    CreateUserResponse.UserData user = result.getUser();
                    if (user != null && user.getBio() != null && !user.getBio().isEmpty()) {
                        prefs.edit().putBoolean(OnboardingFragment.KEY_ONBOARDING_COMPLETED, true).apply();
                        navigateToHome();
                    } else {
                        navigateToOnboarding();
                    }
                } else {
                    Toast.makeText(getContext(), "Registration failed. Please try again.",
                            Toast.LENGTH_SHORT).show();
                    resetButton();
                }
            }

            @Override
            public void onFailure(@NonNull Call<CreateUserResponse> call, @NonNull Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(getContext(), "Network error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
                resetButton();
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

    private void navigateToHome() {
        View bottomNav = requireActivity().findViewById(R.id.bottom_navigation);
        if (bottomNav != null) {
            bottomNav.setVisibility(View.VISIBLE);
        }

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new HomeFragment())
                .commit();
    }

    private void resetButton() {
        btnContinue.setEnabled(true);
        btnContinue.setText("Continue");
    }
}
