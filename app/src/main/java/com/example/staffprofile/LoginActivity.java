package com.example.staffprofile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.staffprofile.Common.Common;
import com.example.staffprofile.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private EditText edtUsername, edtPassword;
    private Button btnLogin;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

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

        setContentView(R.layout.activity_login);

        //SharedPreferences
        final SharedPreferences username = getApplicationContext().getSharedPreferences("Username",0);
        final SharedPreferences.Editor editor = username.edit();


        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("User");

        edtUsername = findViewById(R.id.username);
        edtPassword = findViewById(R.id.password);
        btnLogin = findViewById(R.id.login);

        /*if (username.contains("Username")){
            String exists =  username.getString("Username","");
            if (!exists.isEmpty() && exists!= null){
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        }*/

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog dialog = new ProgressDialog(LoginActivity.this);
                dialog.setMessage("Please wait...");
                dialog.show();


                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            //Check If User Exist or Not
                            if (Common.isConnectedToInternet(LoginActivity.this)){
                                if (!edtPassword.getText().toString().isEmpty() || !edtUsername.getText().toString().isEmpty()){

                                    if (dataSnapshot.child(edtUsername.getText().toString()).exists()){
                                        dialog.dismiss();
                                        User user = dataSnapshot.child(edtUsername.getText().toString()).getValue(User.class);
                                        if (user.getPassword().equals(edtPassword.getText().toString())){
                                            Intent i = new Intent(LoginActivity.this,MainActivity.class);
                                            Common.CURRENT_USER = user;
                                            startActivity(i);
                                            /*editor.putString("Username",edtUsername.getText().toString());
                                            editor.commit();
                                            */finish();
                                        }
                                        else {
                                            Toast.makeText(getApplicationContext(),"Wrong Password !!!",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    else{
                                        dialog.dismiss();
                                        Toast.makeText(getApplicationContext(),"User Not Exists !!!",Toast.LENGTH_SHORT).show();
                                    }
                                }
                                else {
                                    dialog.dismiss();
                                    Toast.makeText(getApplicationContext(),"Fields should not empty !!!",Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            dialog.dismiss();
                        }
                    });
                }

        });


    }
}
