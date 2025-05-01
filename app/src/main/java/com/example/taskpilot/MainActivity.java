package com.example.taskpilot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager2 viewPager2;
    ViewPagerAdapter adapter;
    private static final String PREFS_NAME = "user_prefs";
    private static final String KEY_DARK_MODE = "dark_mode";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // applying light or dark theme based on preference
        SharedPreferences sharedPreferences = this.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean isDarkMode = sharedPreferences.getBoolean(KEY_DARK_MODE, false);
        // making default status bar transparent
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.primaryColor));
        }


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        new TabLayoutMediator(
                tabLayout,
                viewPager2,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        switch (position)
                        {
                            case 0:
//                                tab.setText("Schedule");
//                                BadgeDrawable badge = tab.getOrCreateBadge();
//                                badge.setNumber(10);
//                                badge.setMaxCharacterCount(2);
                                tab.setIcon(R.drawable.icon_schedule_tab);
                                break;
                            case 1:
//                                tab.setText("History");
//                                BadgeDrawable badge1 = tab.getOrCreateBadge();
//                                badge1.setNumber(100);
//                                badge1.setMaxCharacterCount(3);
                                tab.setIcon(R.drawable.icon_past_tab);
                                break;
                            case 2:
//                                tab.setText("Notifications");
//                                BadgeDrawable badge2 = tab.getOrCreateBadge();
//                                badge2.setNumber(55);
                                tab.setIcon(R.drawable.icon_notif_tab);
                                break;
                            case 3:
//                                tab.setText("Profile");
//                                BadgeDrawable badge3 = tab.getOrCreateBadge();
//                                badge3.setNumber(55);
                                tab.setIcon(R.drawable.icon_profile_tab);
                                break;
                        }
                    }
                }
        ).attach();

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                BadgeDrawable badge = tabLayout.getTabAt(position).getOrCreateBadge();
                badge.setVisible(false);
                badge.setNumber(0);
            }
        });
    }

    public void init()
    {
        tabLayout = findViewById(R.id.tabLayout);
        viewPager2 = findViewById(R.id.viewpager2);
        adapter = new ViewPagerAdapter(this);
        viewPager2.setAdapter(adapter);
    }

}