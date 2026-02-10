package com.beyondbinary.app.onboarding;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.beyondbinary.app.HomeFragment;
import com.beyondbinary.app.R;
import com.beyondbinary.app.api.ApiService;
import com.beyondbinary.app.api.RetrofitClient;
import com.beyondbinary.app.api.UserResponse;
import com.beyondbinary.app.data.database.AppDatabaseHelper;
import com.beyondbinary.app.data.models.User;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OnboardingFragment extends Fragment {

    private static final String TAG = "OnboardingFragment";
    private static final String PREF_NAME = "beyondbinary_prefs";
    public static final String KEY_ONBOARDING_COMPLETED = "onboarding_completed";

    private static final Question[] QUESTIONS = {
            new Question(
                    "Let's get to know you",
                    "Are your opinions usually guided by:",
                    "Logic & Analysis",
                    "Feelings & Values"
            ),
            new Question(
                    "Your social style",
                    "On weekends, do you prefer:",
                    "Quiet time alone",
                    "Being with others"
            ),
            new Question(
                    "Your activity preference",
                    "Which sounds more appealing:",
                    "An energetic workout",
                    "A peaceful walk"
            ),
            new Question(
                    "Your mindset",
                    "When facing stress, you tend to:",
                    "Take action",
                    "Reflect and breathe"
            ),
            new Question(
                    "Your ideal wellness",
                    "Your ideal wellness activity:",
                    "Group fitness class",
                    "Solo nature experience"
            )
    };

    private int currentStep = 0;
    private final List<String> userChoices = new ArrayList<>();

    private TextView titleText;
    private TextView questionText;
    private MaterialButton btnOptionA;
    private MaterialButton btnOptionB;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_onboarding, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        titleText = view.findViewById(R.id.onboarding_title);
        questionText = view.findViewById(R.id.onboarding_question);
        btnOptionA = view.findViewById(R.id.btn_option_a);
        btnOptionB = view.findViewById(R.id.btn_option_b);

        displayQuestion();

        btnOptionA.setOnClickListener(v -> onOptionSelected(btnOptionA.getText().toString()));
        btnOptionB.setOnClickListener(v -> onOptionSelected(btnOptionB.getText().toString()));
    }

    private void displayQuestion() {
        Question q = QUESTIONS[currentStep];
        titleText.setText(q.screenTitle);
        questionText.setText(q.questionText);
        btnOptionA.setText(q.optionA);
        btnOptionB.setText(q.optionB);
    }

    private void onOptionSelected(String choiceText) {
        userChoices.add(choiceText);

        currentStep++;

        if (currentStep < QUESTIONS.length) {
            displayQuestion();
        } else {
            completeOnboarding();
        }
    }

    private void completeOnboarding() {
        String bio = String.join(", ", userChoices);
        String interestTags = deriveInterestTags(userChoices);

        SharedPreferences prefs = requireContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);

        // Save locally — read existing user first to preserve profile picture and other fields
        AppDatabaseHelper db = AppDatabaseHelper.getInstance(requireContext());
        User localUser = db.getUserById(userId);
        if (localUser == null) {
            localUser = new User();
            localUser.setId(userId);
        }
        localUser.setBio(bio);
        localUser.setInterestTags(interestTags);
        db.insertUser(localUser);

        // Save to backend
        if (userId != -1) {
            Map<String, String> body = new HashMap<>();
            body.put("bio", bio);
            body.put("interest_tags", interestTags);

            ApiService apiService = RetrofitClient.getApiService();
            apiService.updateUser(userId, body).enqueue(new Callback<UserResponse>() {
                @Override
                public void onResponse(@NonNull Call<UserResponse> call,
                                       @NonNull Response<UserResponse> response) {
                    Log.d(TAG, "User profile updated on backend: " + response.isSuccessful());
                }

                @Override
                public void onFailure(@NonNull Call<UserResponse> call, @NonNull Throwable t) {
                    Log.e(TAG, "Failed to update user on backend", t);
                }
            });
        }

        prefs.edit().putBoolean(KEY_ONBOARDING_COMPLETED, true).apply();

        // Show bottom navigation
        View bottomNav = requireActivity().findViewById(R.id.bottom_navigation);
        if (bottomNav != null) {
            bottomNav.setVisibility(View.VISIBLE);
        }

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new HomeFragment())
                .commit();
    }

    private String deriveInterestTags(List<String> choices) {
        Set<String> tags = new HashSet<>();

        // Map choices to interest tags
        for (String choice : choices) {
            switch (choice) {
                case "Logic & Analysis":
                    tags.add("coding");
                    tags.add("board games");
                    break;
                case "Feelings & Values":
                    tags.add("arts");
                    tags.add("community");
                    break;
                case "Quiet time alone":
                    tags.add("reading");
                    tags.add("photography");
                    break;
                case "Being with others":
                    tags.add("social");
                    tags.add("party");
                    break;
                case "An energetic workout":
                    tags.add("sports");
                    tags.add("gym");
                    break;
                case "A peaceful walk":
                    tags.add("hiking");
                    tags.add("yoga");
                    break;
                case "Take action":
                    tags.add("running");
                    tags.add("cycling");
                    break;
                case "Reflect and breathe":
                    tags.add("yoga");
                    tags.add("meditation");
                    break;
                case "Group fitness class":
                    tags.add("sports");
                    tags.add("social");
                    break;
                case "Solo nature experience":
                    tags.add("hiking");
                    tags.add("outdoor");
                    break;
            }
        }

        return String.join(",", tags);
    }

    private static class Question {
        final String screenTitle;
        final String questionText;
        final String optionA;
        final String optionB;

        Question(String screenTitle, String questionText, String optionA, String optionB) {
            this.screenTitle = screenTitle;
            this.questionText = questionText;
            this.optionA = optionA;
            this.optionB = optionB;
        }
    }
}
