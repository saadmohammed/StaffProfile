package com.example.staffprofile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.bumptech.glide.request.RequestOptions;
import com.example.staffprofile.Common.Common;
import com.example.staffprofile.Interface.ItemClickListener;
import com.example.staffprofile.Model.Banner;
import com.example.staffprofile.Model.Department;
import com.example.staffprofile.ViewHolder.DepartmentViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.glide.slider.library.SliderLayout;
import com.glide.slider.library.animations.DescriptionAnimation;
import com.glide.slider.library.slidertypes.TextSliderView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    //Firebase
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference, bannerDatabaseReference;

    //Alert Progress
    AlertDialog.Builder builder;
    AlertDialog dialog;


    //Refresh
    SwipeRefreshLayout s;

    SharedPreferences sharedPreferences ;
    SharedPreferences.Editor editor;

    // Creating RecyclerView.
    RecyclerView recyclerView;
    GridLayoutManager mlm;
    FirebaseRecyclerAdapter<Department, DepartmentViewHolder> adapter;

    //Progress
    ProgressDialog pdialog;

    //Slider
    SliderLayout sliderLayout;
    ArrayList<String> image_list;
    ArrayList<String> name_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Font
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/small.otf")
                .setFontAttrId(R.attr.fontPath)
                .build());

        setContentView(R.layout.activity_main);


        //Permission
        String[] permissionString = new String[]{Manifest.permission.CALL_PHONE};
        ActivityCompat.requestPermissions(this, permissionString, 1);



        sharedPreferences = getApplicationContext().getSharedPreferences("MyPref", 0);
        editor = sharedPreferences.edit();

        //Progress
        pdialog = new ProgressDialog(MainActivity.this);
        pdialog.setMessage("Please wait...");
        pdialog.setCancelable(false);

        Toolbar toolbar = findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        getSupportActionBar().setTitle("Staff Profile");

        //Alert Progress
        builder = new AlertDialog.Builder(this);

        firebaseDatabase= FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("Department");
        bannerDatabaseReference = FirebaseDatabase.getInstance().getReference("Banner");

        // Setting RecyclerView size true.
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));



        //Refreshing
        s = findViewById(R.id.swipeRefreshMain);
        s.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                s.setRefreshing(true);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        s.setRefreshing(false);
                        if (Common.isConnectedToInternet(getApplicationContext())) {
                            pdialog.show();
                            slideSetup();
                            loadDepartment();

                        }else {
                            Toast.makeText(getApplicationContext(), "Please check your Internet Connection", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getApplicationContext(), RetryActivity.class);
                            startActivity(intent);
                            finish();

                        }

                    }
                }, 1000);
            }
        });
        if (Common.isConnectedToInternet(this)) {
            pdialog.show();
            slideSetup();
            loadDepartment();
        }else {
            Intent intent = new Intent(getApplicationContext(), RetryActivity.class);
            startActivity(intent);
            finish();
            Toast.makeText(getApplicationContext(), "Please check your Internet Connection", Toast.LENGTH_SHORT).show();
        }

    }

    //Slider
    private void slideSetup() {
        sliderLayout = findViewById(R.id.slider);
        image_list = new ArrayList<>();
        name_list = new ArrayList<>();

        final RequestOptions requestOptions = new RequestOptions();
        requestOptions.centerCrop();


        DatabaseReference banner = firebaseDatabase.getReference("Banner");
        banner.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Banner banners = snapshot.getValue(Banner.class);
                    image_list.add(banners.getImage());
                    name_list.add(banners.getName());
                }
                for(int i=0; i<image_list.size(); i++){


                    TextSliderView sliderView = new TextSliderView(getApplicationContext());

                    // initialize SliderLayout
                    sliderView
                            .image(image_list.get(i))
                            .description(name_list.get(i))
                            .setRequestOption(requestOptions)
                            .setProgressBarVisible(true);
                    sliderLayout.addSlider(sliderView);

                }

                sliderLayout.setPresetTransformer(SliderLayout.Transformer.Accordion);
                sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
                sliderLayout.setCustomAnimation(new DescriptionAnimation());
                sliderLayout.setDuration(6000);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    private void loadDepartment() {
        Query query = FirebaseDatabase.getInstance().getReference().child("Department");
        FirebaseRecyclerOptions<Department> options = new FirebaseRecyclerOptions.Builder<Department>()
                .setQuery(query, Department.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<Department, DepartmentViewHolder>(options) {
            @NonNull
            @Override
            public DepartmentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
                View view1 = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.department, viewGroup, false);
                return new DepartmentViewHolder(view1);
            }

            @Override
            protected void onBindViewHolder(@NonNull DepartmentViewHolder holder, int position, @NonNull Department model) {
                holder.departmentName.setText(model.getName());
                Picasso.get().load(model.getImage()).into(holder.departmentImage);
                pdialog.dismiss();
                final Department clickCategoryItem = model;
                if (Common.isConnectedToInternet(getApplicationContext()))
                    holder.setItemClickListener(new ItemClickListener() {
                        @Override
                        public void onClick(View view, int position, boolean isLongClick) {
                            Intent itemList = new Intent(getApplicationContext(), StaffActivity.class);
                            //Because category Id is key, so we just get key of
                           if(!adapter.getRef(position).getKey().equals("26")){
                                editor.putString("DeptId",adapter.getRef(position).getKey());
                                editor.commit();
                                Log.e("ID",adapter.getRef(position).getKey());
                                startActivity(itemList);
                            }
                           else {
                               startActivity(new Intent(getApplicationContext(), NonTeachingStaff.class));
                           }
                        }
                    });
                else
                    Toast.makeText(getApplicationContext(), "Please check your Internet Connection", Toast.LENGTH_LONG).show();

            }

        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.logout:
                Intent logout = new Intent(MainActivity.this, LoginActivity.class);
                logout.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(logout);
            return(true);
            case R.id.web:
                Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.jmc.edu"));
                startActivity(browser);
            return(true);
            case R.id.about:
                about();
            return (true);
        }
        return(super.onOptionsItemSelected(item));
    }

    private void about() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);


        LayoutInflater inflater = getLayoutInflater();
        View about = inflater.inflate(R.layout.aboutus,null);
        alertDialog.setView(about);
        alertDialog.show();

    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Log.d("PhoneCall", "Permission Granted");

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}

