package com.aspegrenide.minide;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class FragmentAdapterProblem extends FragmentStateAdapter {
    public FragmentAdapterProblem(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    private FragmentDescriptionProblem fd;

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1: return new FragmentImageProblem();
            case 2: return new FragmentDetailsProblem();
            case 3: return new FragmentConnections();
        }
        return new FragmentDescriptionProblem();
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
