package com.example.staffprofile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.example.staffprofile.Common.Common;
import com.example.staffprofile.Interface.ItemClickListener;
import com.example.staffprofile.Model.Banner;
import com.example.staffprofile.Model.Department;
import com.example.staffprofile.ViewHolder.DepartmentViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import java.util.HashMap;
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
    DatabaseReference databaseReference;

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
    // Creating RecyclerView.Adapter.
    RecyclerView.Adapter recyclerAdapter;

    FirebaseRecyclerAdapter<Department, DepartmentViewHolder> adapter;


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


        Toolbar toolbar = findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        getSupportActionBar().setTitle("Staff Profile");

        //Alert Progress
        builder = new AlertDialog.Builder(this);

        firebaseDatabase= FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("Department");

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
                            setProgressDialog();
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
            setProgressDialog();
            loadDepartment();
        }else {
            Intent intent = new Intent(getApplicationContext(), RetryActivity.class);
            startActivity(intent);
            finish();
            Toast.makeText(getApplicationContext(), "Please check your Internet Connection", Toast.LENGTH_SHORT).show();
        }


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
                dialog.dismiss();
                final Department clickCategoryItem = model;
                if (Common.isConnectedToInternet(getApplicationContext()))
                    holder.setItemClickListener(new ItemClickListener() {
                        @Override
                        public void onClick(View view, int position, boolean isLongClick) {
                            Intent itemList = new Intent(getApplicationContext(), StaffActivity.class);
                            //Because category Id is key, so we just get key of
                            editor.putString("DeptId",adapter.getRef(position).getKey());
                            editor.commit();
                            Log.e("ID",adapter.getRef(position).getKey());
                            startActivity(itemList);

                        }
                    });
                else
                    startActivity(new Intent(getApplicationContext(), RetryActivity.class));
            }

        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }

    private void setProgressDialog() {

        int llPadding = 30;
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll.setPadding(15, llPadding, 15, llPadding);
        ll.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams llParam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        ll.setLayoutParams(llParam);

        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setIndeterminate(true);
        progressBar.setPadding(0, 0, llPadding, 0);
        progressBar.setLayoutParams(llParam);

        llParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        TextView tvText = new TextView(this);
        tvText.setText("Please wait...");
        tvText.setTextColor(Color.parseColor("#000000"));
        tvText.setPadding(0,0,80,0);
        tvText.setTextSize(20);
        tvText.setLayoutParams(llParam);

        ll.addView(progressBar);
        ll.addView(tvText);

        //AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setView(ll);

        dialog = builder.create();
        dialog.show();
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(dialog.getWindow().getAttributes());
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setAttributes(layoutParams);
        }
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
            case R.id.nonteaching:
                startActivity(new Intent(getApplicationContext(),NonTeachingStaff.class));
            return(true);
            case R.id.exit:
                finish();
            return(true);
            case R.id.search:
                startActivity(new Intent(getApplicationContext(),SearchActivity.class));
                return (true);
    }
        return(super.onOptionsItemSelected(item));
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

}

