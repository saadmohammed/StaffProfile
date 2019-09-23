package com.example.staffprofile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NonTeachingStaff extends AppCompatActivity {

    private TabLayout nttabLayout;
    private ViewPager ntviewPager;
    private ViewPageAdapter ntadapter;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Font
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/small.otf")
                .setFontAttrId(R.attr.fontPath)
                .build());

        setContentView(R.layout.activity_non_teaching_staff);

        Toolbar toolbar =  findViewById(R.id.toolbarNTStaff);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Non - Teaching Staff");
        nttabLayout = findViewById(R.id.tabLayoutIdNT);
        ntviewPager = findViewById(R.id.viewPagerIdNT);
        ntadapter = new ViewPageAdapter(getSupportFragmentManager());

        //Add Fragment
        ntadapter.AddFragment(new NTAidedFragemt(), "Aided");
        ntadapter.AddFragment(new NTUnAidedFragment(), "Self - Finance");

        ntviewPager.setAdapter(ntadapter);
        nttabLayout.setupWithViewPager(ntviewPager);

    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
