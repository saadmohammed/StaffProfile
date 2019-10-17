package com.example.staffprofile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.staffprofile.Common.Common;
import com.example.staffprofile.Model.Staff;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class AidedStaffDetail extends AppCompatActivity {

    SharedPreferences staffSharedPreferences;
    String StaffId = "";
    CollapsingToolbarLayout collapsingToolbarLayout;
    AppBarLayout appBarLayoutAided;

    private TextView txtName, txtDegree, txtPost,txtPhone, txtEmail, txtAddress;
    private ImageView imgStaff;
    private ImageButton btnCall,btnEmail;

    FirebaseDatabase database;
    DatabaseReference aided;

    String phoneNo = "", TO = "";

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

        setContentView(R.layout.activity_aided_staff_detail);
        //Appbar/Collapsing Layout
        collapsingToolbarLayout = findViewById(R.id.collapsingAided);
        appBarLayoutAided = findViewById(R.id.app_bar_layoutAided);
        appBarLayoutAided.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle("Staff Details");
                    isShow = true;
                } else if(isShow) {
                    collapsingToolbarLayout.setTitle(" ");//careful there should a space between double quote otherwise it wont work
                    isShow = false;
                }
            }
        });


        txtName = findViewById(R.id.detail_staffname);
        txtDegree = findViewById(R.id.detail_staffdegree);
        txtPost = findViewById(R.id.detail_staffpost);
        txtEmail = findViewById(R.id.detail_staffemail);
        txtPhone = findViewById(R.id.detail_staffphone);
        txtAddress = findViewById(R.id.detail_staffaddress);

        imgStaff = findViewById(R.id.detail_staffimage);

        btnCall = findViewById(R.id.img_aided_staff_call);
        btnEmail = findViewById(R.id.img_aided_staff_mail);

        //Firebase AIDED
        database = FirebaseDatabase.getInstance();
        aided = database.getReference().child("Aided");

        collapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);

        staffSharedPreferences = getApplicationContext().getSharedPreferences("MyPrefStaffId", 0);

        if (staffSharedPreferences.contains("StaffId")) {
            StaffId = staffSharedPreferences.getString("StaffId", "");
        } else {
            Toast.makeText(this, "Details Not Available", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, StaffActivity.class));
            finish();
        }

        if (!StaffId.isEmpty() && StaffId != null)
            if (Common.isConnectedToInternet(getApplicationContext()))
                aidedDetailStaff(StaffId);
            else
                Toast.makeText(getApplicationContext(), "Please Check Internet Connection", Toast.LENGTH_LONG).show();

        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + phoneNo));
                if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    Activity#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for Activity#requestPermissions for more details.
                    return;
                }
                startActivity(callIntent);
                }
            });

        btnEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{TO});
                emailIntent.setType("message/rfc822");
                startActivity(Intent.createChooser(emailIntent, "Select Email Sending App :"));

            }
        });

    }

    private void aidedDetailStaff(final String staffId) {
        aided.child(staffId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Staff staff = dataSnapshot.getValue(Staff.class);

                Picasso.get().load(staff.getImage()).into(imgStaff);
                txtName.setText(staff.getName());
                txtDegree.setText(staff.getDegree());
                txtPost.setText(staff.getPost());
                txtEmail.setText(staff.getEmail());
                txtAddress.setText(staff.getAddress());
                txtPhone.setText(staff.getPhone().toString());
                phoneNo = staff.getPhone().toString();
                TO = staff.getEmail();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }


}
