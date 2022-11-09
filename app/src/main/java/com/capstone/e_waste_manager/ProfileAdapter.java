package com.capstone.e_waste_manager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ProfileAdapter extends FragmentStateAdapter {
    public ProfileAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position){
            case 1:
                return new ProfileCommentFragment();
            case 2:
                return new ProfileAboutFragment();
        }
        return new ProfilePostFragment();
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
