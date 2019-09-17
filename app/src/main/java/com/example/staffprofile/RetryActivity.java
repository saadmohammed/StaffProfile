package com.example.staffprofile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.staffprofile.Common.Common;

public class RetryActivity extends AppCompatActivity {
    private Button btnRetry;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retry);

        btnRetry = findViewById(R.id.buttonRetry);
        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Common.isConnectedToInternet(getBaseContext())){
                    startActivity(new Intent(getApplicationContext(),SplashActivity.class));
                    finish();
                }else {
                    Toast.makeText(getApplicationContext(),"Please check your Internet Connection", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }
}
