package com.example.staffprofile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.staffprofile.Model.Department;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class StaffActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPageAdapter adapter;

    FirebaseDatabase database;
    DatabaseReference reference;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    String DeptId = "";

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

        setContentView(R.layout.activity_staff);

        //Firebase
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Department");

        sharedPreferences = getApplicationContext().getSharedPreferences("MyPref",0);
        if (sharedPreferences.contains("DeptId")){
            DeptId = sharedPreferences.getString("DeptId","");
        }


        Toolbar toolbar =  findViewById(R.id.toolbarStaff);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        reference.child(DeptId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Department department = dataSnapshot.getValue(Department.class);
                getSupportActionBar().setTitle("Department of "+department.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        tabLayout = findViewById(R.id.tabLayoutId);
        viewPager = findViewById(R.id.viewPagerId);
        adapter = new ViewPageAdapter(getSupportFragmentManager());

        //Add Fragment
        adapter.AddFragment(new AidedFragment(), "Men - Aided");
        adapter.AddFragment(new UnAidedFragment(), "Men - SF");
        adapter.AddFragment(new WomenFragment(), "Women - SF");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
