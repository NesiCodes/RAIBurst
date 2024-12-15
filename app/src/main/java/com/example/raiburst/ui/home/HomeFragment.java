package com.example.raiburst.ui.home;

import static androidx.fragment.app.FragmentManager.TAG;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.raiburst.databinding.FragmentHomeBinding;

import org.checkerframework.checker.units.qual.C;

import java.util.logging.Logger;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    private ChatGPTService chatGPTService;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        chatGPTService = new ChatGPTService();

        binding.sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        // Access the button using binding
//        binding.button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Example: Show a Toast message
//                Toast.makeText(getContext(), "Test", Toast.LENGTH_SHORT).show();
//            }
//        });


        //esht ven text direkt nga layout (file .xml)
//        final TextView textView = binding.textHome;
//        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void sendMessage() {
        String message = binding.userInput.getText().toString();
//        Log.i(TAG, message);

        if (message.isEmpty()) return;

        // Display user message
        addMessageToChat("You: " + message, true);

        // Clear input field
        binding.userInput.setText("");


        // Send the message to the ChatGPT API
        chatGPTService.getChatGPTResponseAsync(message, new ChatGPTCallback() {

            @Override
            public void onResponse(String response) {

                getActivity().runOnUiThread(() -> addMessageToChat("RaiAssistant: " + response, false));
            }

            @Override
            public void onError(String error) {

                getActivity().runOnUiThread(() -> addMessageToChat("Error: " + error, false));
            }
        });
    }

    private void addMessageToChat(String message, boolean isUser) {
        // Create a TextView for the message
        TextView textView = new TextView(getContext());
        textView.setText(message);
        textView.setPadding(20, 15, 20, 15);
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(16);

        // Customize alignment and background based on message type
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        // Set alignment and background
        if (isUser) {
            layoutParams.gravity = Gravity.END; // Right-aligned
            textView.setBackground(createRoundedBackground(Color.parseColor("#0078D7"))); // Blue background
        } else {
            layoutParams.gravity = Gravity.START; // Left-aligned
            textView.setBackground(createRoundedBackground(Color.parseColor("#606060"))); // Gray background
        }

        layoutParams.setMargins(10, 10, 10, 10);
        textView.setLayoutParams(layoutParams);

        // Add the TextView to the chat container
        binding.chatContainer.addView(textView);
    }

    private GradientDrawable createRoundedBackground(int color) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(30); // Border radius
        return drawable;
    }
}