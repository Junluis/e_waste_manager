package com.capstone.e_waste_manager.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.adapter.FragmentViewHolder;

import com.capstone.e_waste_manager.Fragments.ProfileAboutFragment;
import com.capstone.e_waste_manager.Fragments.ProfileCommentFragment;
import com.capstone.e_waste_manager.Fragments.ProfilePostFragment;
import com.capstone.e_waste_manager.HomeModel;

import java.util.ArrayList;
import java.util.List;

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
