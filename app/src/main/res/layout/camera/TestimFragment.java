package com.example.raiburst.ui.camera;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.raiburst.databinding.FragmentTestimBinding;


public class TestimFragment extends Fragment {
    private FragmentTestimBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        TestimViewModel testimViewModel =
                new ViewModelProvider(this).get(TestimViewModel.class);

        binding = FragmentTestimBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        binding.filters.setOnClickListener(new View.OnClickListener() {
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
