package com.aspegrenide.minide;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class FragmentAdapterIdea extends FragmentStateAdapter {
    public FragmentAdapterIdea(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1: return new FragmentImageIdea();
            case 2: return new FragmentDetailsIdea();
            //case 3: return new FragmentMyList();
        }
        return new FragmentDescriptionIdea();
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
