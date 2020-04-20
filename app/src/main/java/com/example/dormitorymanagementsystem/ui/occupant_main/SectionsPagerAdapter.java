package com.example.dormitorymanagementsystem.ui.occupant_main;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.dormitorymanagementsystem.R;
import com.example.dormitorymanagementsystem.classes.activity_classes.BillingFragment;
import com.example.dormitorymanagementsystem.classes.activity_classes.PersonalInfoFragment;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private BillingFragment thisBillingFragment = new BillingFragment();

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.occupant_tab_1, R.string.occupant_tab_2};
    private final Context mContext;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new PersonalInfoFragment();
            case 1:
                return thisBillingFragment;
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

    public BillingFragment getThisBillingFragment() {
        return thisBillingFragment;
    }
}