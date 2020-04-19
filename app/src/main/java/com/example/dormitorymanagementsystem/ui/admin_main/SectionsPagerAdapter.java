package com.example.dormitorymanagementsystem.ui.admin_main;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.dormitorymanagementsystem.R;
import com.example.dormitorymanagementsystem.classes.activity_classes.ClientFragment;
import com.example.dormitorymanagementsystem.classes.activity_classes.RoomFragment;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private ClientFragment thisClientFragment = new ClientFragment();

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.admin_tab_1, R.string.admin_tab_2};
    private final Context mContext;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        switch (position) {
            case 0:
                return new RoomFragment();
            case 1:
                //
                //return new ClientFragment();
                return thisClientFragment;
            default:
                return null;
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        return TAB_TITLES.length;
    }

    public ClientFragment getThisClientFragment() {
        return thisClientFragment;
    }
}