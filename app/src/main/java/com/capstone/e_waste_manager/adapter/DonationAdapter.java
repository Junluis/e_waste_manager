package com.capstone.e_waste_manager.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.capstone.e_waste_manager.Fragments.AllDonationFragment;
import com.capstone.e_waste_manager.Fragments.FinishedDonationFragment;
import com.capstone.e_waste_manager.Fragments.OngoingDonationFragment;

public class DonationAdapter extends FragmentStateAdapter {



    public DonationAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position){
            case 1:
                return new OngoingDonationFragment();
            case 2:
                return new FinishedDonationFragment();
        }
        return new AllDonationFragment();



    }

    @Override
    public int getItemCount() {
        return 3;
    }




}
