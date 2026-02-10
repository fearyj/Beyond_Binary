package com.beyondbinary.app.chatbot;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.beyondbinary.app.BuildConfig;
import com.beyondbinary.app.R;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.TextPart;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ChatbotFragment extends Fragment {

    private RecyclerView recyclerView;
    private ChatbotAdapter adapter;
    private List<Message> messageList;
    private EditText messageInput;
    private Button sendButton;
    private GenerativeModelFutures generativeModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chatbot, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize the Generative Model
        GenerativeModel gm = new GenerativeModel("gemini-1.5-flash", BuildConfig.GEMINI_API_KEY);
        generativeModel = GenerativeModelFutures.from(gm);

        recyclerView = view.findViewById(R.id.recyclerView_chatbot);
        messageInput = view.findViewById(R.id.message_input);
        sendButton = view.findViewById(R.id.send_button);

        messageList = new ArrayList<>();
        adapter = new ChatbotAdapter(messageList);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        sendButton.setOnClickListener(v -> {
            String messageText = messageInput.getText().toString().trim();
            if (!messageText.isEmpty()) {
                // Add user message
                addMessage(messageText, true);
                messageInput.setText("");

                // Get bot response
                getChatbotResponse(messageText);
            }
        });
    }

    private void getChatbotResponse(String userMessage) {
        List<TextPart> parts = new ArrayList<>();
        parts.add(new TextPart(userMessage));

        Content content = new Content(parts);

        Executor executor = Executors.newSingleThreadExecutor();

        Futures.addCallback(generativeModel.generateContent(content), new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String botResponse = result.getText();
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> addMessage(botResponse, false));
                }
            }

            @Override
            public void onFailure(@NonNull Throwable t) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> addMessage("Failed to get response. Error: " + t.getMessage(), false));
                }
            }
        }, executor);
    }

    private void addMessage(String text, boolean isSentByUser) {
        messageList.add(new Message(text, isSentByUser));
        adapter.notifyItemInserted(messageList.size() - 1);
        recyclerView.scrollToPosition(messageList.size() - 1);
    }
}
