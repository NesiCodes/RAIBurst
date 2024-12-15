package com.example.raiburst.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.appcompat.app.AppCompatActivity;

import com.example.raiburst.MainActivity;
import com.example.raiburst.databinding.FragmentProfileBinding;
import com.example.raiburst.ui.LogInActivity;
import com.example.raiburst.ui.profile.ProfileViewModel;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileFragment extends Fragment{

    private FirebaseAuth auth;

    private FragmentProfileBinding binding;
    private Button testButton;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ProfileViewModel profileViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);

        auth = FirebaseAuth.getInstance();

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                Intent intent = new Intent(requireActivity(), LogInActivity.class);
                startActivity(intent);
            }
        });

        return root;
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}