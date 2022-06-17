package com.example.diploma.main.ControllerLayout;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.diploma.R;
import com.example.diploma.databinding.StartAppBinding;

public class StartAppFragment extends Fragment {

    private StartAppBinding binding;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return StartAppBinding.inflate(inflater, container, false).getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        int SPLASH_DISPLAY_LENGTH = 2000;
        new Handler().postDelayed(() ->
                NavHostFragment.findNavController(StartAppFragment.this)
                        .navigate(R.id.action_startFragment_to_fragment_main), SPLASH_DISPLAY_LENGTH);

    }


}