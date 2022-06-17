package com.example.diploma.main.ControllerLayout.Clothes;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ClothesFragmentStateAdapter extends FragmentStateAdapter {

    private static final int FRAGMENT_COUNT = 5;

    public ClothesFragmentStateAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 1:
                return new TopFragment();
            case 2:
                return new BottomFragment();
            case 3:
                return new OuterwearFragment();
            case 4:
                return new ShoesFragment();
            default:
                return new AllFragment();
        }
    }

    @Override
    public int getItemCount() {
        return FRAGMENT_COUNT;
    }
}
