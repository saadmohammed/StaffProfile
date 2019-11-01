package com.example.staffprofile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
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

public class UnAidedStaffDetail extends AppCompatActivity {

    SharedPreferences staffSharedPreferences;
    String UnStaffId = "";
    private ImageButton btnCall, btnEmail;
    private TextView txtName, txtDegree, txtPost, txtPhone, txtEmail, txtAddress;
    private ImageView imgStaff;
    private CardView unaidedDetailCard;
    CollapsingToolbarLayout collapsingToolbarLayoutUnaided;
    AppBarLayout appBarLayoutUnAided;
    FirebaseDatabase database;
    DatabaseReference unAided;
    String phoneNo = "", TO = "", Details;

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

        setContentView(R.layout.activity_un_aided_staff_detail);

        collapsingToolbarLayoutUnaided = findViewById(R.id.collapsingUnAided);
        appBarLayoutUnAided = findViewById(R.id.app_bar_layoutUnAided);
        appBarLayoutUnAided.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayoutUnaided.setTitle("Staff Details");
                    isShow = true;
                } else if(isShow) {
                    collapsingToolbarLayoutUnaided.setTitle(" ");//careful there should a space between double quote otherwise it wont work
                    isShow = false;
                }
            }
        });

        txtName = findViewById(R.id.detail_unstaffname);
        txtDegree = findViewById(R.id.detail_unstaffdegree);
        txtPost = findViewById(R.id.detail_unstaffpost);
        txtEmail = findViewById(R.id.detail_unstaffemail);
        txtPhone = findViewById(R.id.detail_unstaffphone);
        txtAddress = findViewById(R.id.detail_unstaffaddress);
        imgStaff = findViewById(R.id.detail_unstaffimage);
        btnCall = findViewById(R.id.img_unaided_staff_call);
        btnEmail = findViewById(R.id.img_unaided_staff_mail);
        unaidedDetailCard = findViewById(R.id.unaidedCard);

        //Firebase UNAIDED
        database = FirebaseDatabase.getInstance();
        unAided = database.getReference().child("UnAided");


        staffSharedPreferences = getApplicationContext().getSharedPreferences("MyPrefUnStaffId", 0);

        if (staffSharedPreferences.contains("UnStaffId")) {
            UnStaffId = staffSharedPreferences.getString("UnStaffId", "");
        } else {
            Toast.makeText(this, "Details Not Available", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, StaffActivity.class));
            finish();
        }

        if (!UnStaffId.isEmpty() && UnStaffId != null)
            if (Common.isConnectedToInternet(getApplicationContext()))
                unAidedDetailStaff(UnStaffId);
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

        unaidedDetailCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Details.isEmpty()){
                    Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(Details));
                    startActivity(intent);
                }
                else {
                    Toast.makeText(getApplicationContext()," Details not available", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void unAidedDetailStaff(String unStaffId) {
        unAided.child(unStaffId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                Staff staff = dataSnapshot.getValue(Staff.class);
                Picasso.get().load(staff.getImage()).into(imgStaff);
                txtName.setText(staff.getName());
                txtDegree.setText(staff.getDegree());
                txtPost.setText(staff.getPost());
                txtPhone.setText(staff.getPhone().toString());
                txtEmail.setText(staff.getEmail());
                txtAddress.setText(staff.getAddress());
                phoneNo = staff.getPhone().toString();
                TO = staff.getEmail();
                Details = staff.getDetails();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }
}
