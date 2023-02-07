package com.cartrade.cameraexample;

import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.cartrade.cameraexample.fragments.CaseImageOfflineFragment;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;


public class ImagesActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;


    protected int getLayoutResourceId() {
        return R.layout.activity_internal_tabs;
    }

    public static int tabpos;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internal_tabs);
        CaseImageOfflineFragment.caseImageFragment = null;
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabTextColors(
                ContextCompat.getColor(ImagesActivity.this, R.color.txt_grey),
                ContextCompat.getColor(ImagesActivity.this, R.color.txt_blue)
        );
        LinearLayout linearLayout = (LinearLayout) tabLayout.getChildAt(0);
        linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(getResources().getColor(R.color.tabline));
        drawable.setSize(2, 1);
        linearLayout.setDividerPadding(LinearLayout.LayoutParams.WRAP_CONTENT);
        linearLayout.setDividerDrawable(drawable);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
                tabpos = i;

            }

            @Override
            public void onPageSelected(int i) {
                tabpos = i;

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        setupViewPager(viewPager);
        viewPager.setCurrentItem(0);

    }


    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
            adapter.addFragment(CaseImageOfflineFragment.getInstance(0), "Normal Camera");
            tabLayout.setVisibility(View.GONE);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(1);
        changeTabsFont();
    }

    private void changeTabsFont() {
        ViewGroup childTabLayout = (ViewGroup) tabLayout.getChildAt(0);
        for (int i = 0; i < childTabLayout.getChildCount(); i++) {
            ViewGroup viewTab = (ViewGroup) childTabLayout.getChildAt(i);
            for (int j = 0; j < viewTab.getChildCount(); j++) {
                View tabTextView = viewTab.getChildAt(j);
                if (tabTextView instanceof TextView) {
                    Typeface typeface = Typeface.createFromAsset(getAssets(), "roboto_medium.ttf");
                    ((TextView) tabTextView).setTypeface(typeface);
                }
            }
        }
    }


    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


    @Override
    public void onBackPressed() {

        finish();
    }
}