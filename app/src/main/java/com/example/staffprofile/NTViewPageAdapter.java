package com.example.staffprofile;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class NTViewPageAdapter extends FragmentPagerAdapter {
    private final List<Fragment> unFragmentList = new ArrayList<>();
    private final List<String> unlistTitles = new ArrayList<>();

    public NTViewPageAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return unFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return unlistTitles.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return unlistTitles.get(position);
    }

    public void AddFragment (Fragment fragment, String title){
        unFragmentList.add(fragment);
        unlistTitles.add(title);
    }
}
