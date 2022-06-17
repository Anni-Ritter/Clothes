package com.example.diploma.main.ControllerLayout.Clothes;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.diploma.R;
import com.example.diploma.databinding.FragmentClothesBinding;
import com.google.android.material.tabs.TabLayout;

import java.io.File;


public class ClothesFragment extends Fragment {
    private FragmentClothesBinding binding;
    private ViewPager2.OnPageChangeCallback changeCallback;
    private boolean isFABOpen;
    static final int GALLERY_REQUEST = 1;
    private static final String[] CAMERA_PERMISSION = new String[]{Manifest.permission.CAMERA};
    private static final int CAMERA_REQUEST_CODE = 10;
    private ActivityResultLauncher<Intent> photoPickerResultLauncher;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentClothesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initTabLayout();
        File file = new File(requireActivity().getFilesDir(), ".jpg");
        registerActivityResult();
        if (file.canRead()) {
            Glide.with(requireContext())
                    .load(file)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(binding.imageView4);
        }
        binding.addButton.setOnClickListener(v -> {
            if (!isFABOpen) {
                showFABMenu();
            } else {
                closeFABMenu();
            }
        });

        binding.galleryButton.setOnClickListener(v -> {
            String[] mimeTypes = {"image/jpeg", "image/png"};
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                photoPickerIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
                photoPickerIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                photoPickerIntent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                photoPickerIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                photoPickerIntent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
                photoPickerResultLauncher.launch(photoPickerIntent);

            } else
                ActivityCompat.requestPermissions(
                        requireActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        102);
        });

        binding.cameraButton.setOnClickListener(v -> {
            if (hasCameraPermission()) {
                Navigation.findNavController(v).navigate(R.id.action_fragment_clothes_to_cameraFragment);
            } else {
                requestPermission();
            }
        });
    }

    private void registerActivityResult() {
        photoPickerResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                if (result.getData() != null) {
                    Uri selectedImage = result.getData().getData();
                    binding.imageView4.setImageURI(selectedImage);
                }
            }
        });

    }

    private void initTabLayout() {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        ClothesFragmentStateAdapter adapter = new ClothesFragmentStateAdapter(fragmentManager, getLifecycle());
        binding.viewPager.setAdapter(adapter);
        binding.tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(@NonNull TabLayout.Tab tab) {
                binding.viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(@NonNull TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(@NonNull TabLayout.Tab tab) {

            }
        });
        initCallback();
        binding.viewPager.registerOnPageChangeCallback(changeCallback);
    }

    private void initCallback(){
        changeCallback = new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                binding.tabs.selectTab(binding.tabs.getTabAt(position));
            }
        };
    }



    private boolean hasCameraPermission() {
        return ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(
                requireActivity(),
                CAMERA_PERMISSION,
                CAMERA_REQUEST_CODE
        );
    }

    private void showFABMenu() {
        isFABOpen = true;
        binding.cameraButton.animate().translationY(-getResources().getDimension(R.dimen.standard_55));
        binding.galleryButton.animate().translationY(-getResources().getDimension(R.dimen.standard_105));
    }

    private void closeFABMenu() {
        isFABOpen = false;
        binding.cameraButton.animate().translationY(0);
        binding.galleryButton.animate().translationY(0);
    }

    @Override
    public void onDestroyView() {
        binding.viewPager.unregisterOnPageChangeCallback(changeCallback);
        binding = null;
        super.onDestroyView();
    }
}