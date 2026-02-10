package com.beyondbinary.app.onboarding;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.beyondbinary.app.HomeFragment;
import com.beyondbinary.app.R;
import com.beyondbinary.app.data.database.AppDatabaseHelper;
import com.beyondbinary.app.data.models.User;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class OnboardingFragment extends Fragment {

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

        AppDatabaseHelper db = AppDatabaseHelper.getInstance(requireContext());
        db.insertUser(new User(1, bio));

        SharedPreferences prefs = requireContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(KEY_ONBOARDING_COMPLETED, true).apply();

        // Show bottom navigation now that onboarding is done
        View bottomNav = requireActivity().findViewById(R.id.bottom_navigation);
        if (bottomNav != null) {
            bottomNav.setVisibility(View.VISIBLE);
        }

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new HomeFragment())
                .commit();
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
