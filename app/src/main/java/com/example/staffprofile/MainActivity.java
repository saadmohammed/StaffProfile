package com.example.staffprofile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.staffprofile.Common.Common;
import com.example.staffprofile.Interface.ItemClickListener;
import com.example.staffprofile.Model.Department;
import com.example.staffprofile.ViewHolder.DepartmentViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    FirebaseDatabase firebaseDatabaseCategory;
    DatabaseReference databaseReferenceCategory;

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



        firebaseDatabaseCategory = FirebaseDatabase.getInstance();
        databaseReferenceCategory = firebaseDatabaseCategory.getReference().child("Department");

        recyclerView = findViewById(R.id.recyclerView);

        // Setting RecyclerView size true.
        recyclerView.setHasFixedSize(true);

        // Setting RecyclerView layout as LinearLayout.
        //recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Small Basket");

        if (Common.isConnectedToInternet(this)) {
            //setProgressDialog();
            loadDepartment();
        }else {
         /*   Intent intent = new Intent(getApplicationContext(), Retry.class);
            startActivity(intent);
            finish();*/
            Toast.makeText(getApplicationContext(), "Please check your Internet Connection", Toast.LENGTH_SHORT).show();
        }

    }

    private void loadDepartment() {
        Query query = FirebaseDatabase.getInstance().getReference().child("Department");
        FirebaseRecyclerOptions<Department> options = new FirebaseRecyclerOptions.Builder<Department>().setQuery(query, Department.class).build();
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
                Picasso.with(getBaseContext()).load(model.getImage()).into(holder.departmentImage);
                //dialog.dismiss();
                final Department clickCategoryItem = model;
                if (Common.isConnectedToInternet(getApplicationContext()))
                    holder.setItemClickListener(new ItemClickListener() {
                        @Override
                        public void onClick(View view, int position, boolean isLongClick) {
                            //Intent itemList = new Intent(getApplicationContext(), ItemsList.class);
                            //Because category Id is key, so we just get key of
                            //itemList.putExtra("CategoryId", adapter.getRef(position).getKey());
                            //startActivity(itemList);
                              Toast.makeText(getApplicationContext(),"Starting",Toast.LENGTH_SHORT).show();
                        }
                    });
                else
                    startActivity(new Intent(getApplicationContext(), RetryActivity.class));
            }

        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }
}
