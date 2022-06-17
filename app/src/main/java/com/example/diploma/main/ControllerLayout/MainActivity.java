package com.example.diploma.main.ControllerLayout;

import com.example.diploma.R;
import com.example.diploma.databinding.ActivityMainBinding;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        NavController navController = ((NavHostFragment) (Objects.requireNonNull(getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment)))).getNavController();
        binding.bottomNav.setItemIconTintList(null);
        NavigationUI.setupWithNavController(binding.bottomNav, navController);
    }

}