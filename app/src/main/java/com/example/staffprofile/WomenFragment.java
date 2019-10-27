package com.example.staffprofile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import com.example.staffprofile.Common.Common;
import com.example.staffprofile.Interface.ItemClickListener;
import com.example.staffprofile.Model.Staff;
import com.example.staffprofile.ViewHolder.StaffViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class WomenFragment extends Fragment {
    View view;
    private RecyclerView womenTeachingRecyclerView;

    private DatabaseReference womenTeachingDatabaseReference;

    String DeptId = "";

    FirebaseRecyclerAdapter<Staff, StaffViewHolder> adapter;

    SharedPreferences sharedPreferences, staffSharedPreferences;
    SharedPreferences.Editor staffId;


    //Lock
    private ImageView lock;

    public WomenFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.women, container, false);

        //Lock
        lock = view.findViewById(R.id.womenLock);


        //Recycler View
        womenTeachingRecyclerView = view.findViewById(R.id.womenRecyclerView);
        womenTeachingRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //Firebase
        womenTeachingDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Women");

        //SharedPreferenceFromMain
        sharedPreferences =  getActivity().getSharedPreferences("MyPref", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();


        if (sharedPreferences.contains("DeptId")){
            DeptId = sharedPreferences.getString("DeptId","");
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        String principal = Common.CURRENT_USER.getPassword();
        String women = Common.CURRENT_USER.getPassword();
        if(!DeptId.isEmpty() && DeptId != null)
            if(Common.isConnectedToInternet(getActivity())) {
                if (women.equals("2222") || principal.equals("0000")){
                    womenTeachingStaff(DeptId);
                }
                else{
                    womenTeachingRecyclerView.setVisibility(View.GONE);
                    lock.setVisibility(View.VISIBLE);
                }
            }else
                Toast.makeText(getContext(),"Please Check Internet Connection", Toast.LENGTH_LONG).show();

    }

    private void womenTeachingStaff(String deptId) {
        Query query = womenTeachingDatabaseReference.orderByChild("deptId").equalTo(deptId);        FirebaseRecyclerOptions options =
                new FirebaseRecyclerOptions.Builder<Staff>()
                        .setQuery(query, Staff.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<Staff, StaffViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull StaffViewHolder holder, int i, @NonNull Staff staff) {
                holder.staffName.setText(staff.getName());
                holder.staffPost.setText(staff.getPost());
                Picasso.get().load(staff.getImage()).into(holder.staffImage);

                final Staff clickCategoryItem = staff;
                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        if (Common.isConnectedToInternet(getActivity())) {
                            staffId.putString("UnStaffId", adapter.getRef(position).getKey());
                            staffId.commit();
                            startActivity(new Intent(getContext(), UnAidedStaffDetail.class));
                        }
                    }
                });
            }

            @NonNull
            @Override
            public StaffViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View staffView = LayoutInflater.from(parent.getContext()).inflate(R.layout.staff_list, parent, false);
                StaffViewHolder staffViewHolder = new StaffViewHolder(staffView);
                return staffViewHolder;
            }
        };

        womenTeachingRecyclerView.setAdapter(adapter);
        adapter.startListening();
    }


}
