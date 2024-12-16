package com.example.raiburst.ui.feed;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.raiburst.databinding.FragmentFeedBinding;
import com.example.raiburst.databinding.FragmentTestimBinding;
import com.example.raiburst.ui.camera.CameraActivity;
import com.example.raiburst.ui.camera.TestimViewModel;

public class FeedFragment extends Fragment{

    private FragmentFeedBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        FeedViewModel feedViewModel =
                new ViewModelProvider(this).get(FeedViewModel.class);

        binding = FragmentFeedBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        binding.addstatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Use requireContext() or getActivity() to get the Context
                Intent intent = new Intent(requireContext(), CameraActivity.class);
                startActivity(intent); // Start the CameraActivity
            }
        });

        return root;
    }
}