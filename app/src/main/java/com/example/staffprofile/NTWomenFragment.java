package com.example.staffprofile;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.staffprofile.Common.Common;
import com.example.staffprofile.Model.NTStaff;
import com.example.staffprofile.ViewHolder.NTStaffViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class NTWomenFragment extends Fragment {
    View view;
    private RecyclerView ntWomenRecyclerView;
    private LinearLayoutManager linearLayoutManager;

    private DatabaseReference ntWomenDatabaseReference;

    FirebaseRecyclerAdapter<NTStaff, NTStaffViewHolder> adapter;

    //Lock
    private ImageView lock;


    public NTWomenFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.ntwomen,container, false);

        ntWomenRecyclerView = view.findViewById(R.id.ntwomenRecyclerView);
        ntWomenRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //Lock
        lock = view.findViewById(R.id.ntWomenLock);

        //Firebase
        ntWomenDatabaseReference = FirebaseDatabase.getInstance().getReference("NTWomen");

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        String principal = Common.CURRENT_USER.getPassword();
        String women = Common.CURRENT_USER.getPassword();
            if(Common.isConnectedToInternet(getActivity())) {
                if (women.equals("2222") || principal.equals("0000")){
                    ntWomen();
                }
                else{
                    ntWomenRecyclerView.setVisibility(View.GONE);
                    lock.setVisibility(View.VISIBLE);
                }
            }else
                Toast.makeText(getContext(),"Please Check Internet Connection", Toast.LENGTH_LONG).show();



    }

    private void ntWomen() {
        Query query = ntWomenDatabaseReference;
        FirebaseRecyclerOptions<NTStaff> options =
                new FirebaseRecyclerOptions.Builder<NTStaff>()
                        .setQuery(query, NTStaff.class)
                        .build();

        adapter  = new FirebaseRecyclerAdapter<NTStaff, NTStaffViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull NTStaffViewHolder holder, int i, @NonNull final NTStaff ntStaff) {
                holder.ntStaffName.setText(ntStaff.getName());
                holder.ntStaffDesignation.setText(ntStaff.getDesignation());
                holder.ntStaffPhone.setText(ntStaff.getPhone().toString());
                holder.ntStaffCall.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:" + ntStaff.getPhone()));
                        if (getActivity().checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
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

            }

            @NonNull
            @Override
            public NTStaffViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View staffView = LayoutInflater.from(parent.getContext()).inflate(R.layout.nt_staff_list, parent, false);
                NTStaffViewHolder staffViewHolder = new NTStaffViewHolder(staffView);
                return staffViewHolder;
            }
        };

        ntWomenRecyclerView.setAdapter(adapter);
        adapter.startListening();

    }
}
