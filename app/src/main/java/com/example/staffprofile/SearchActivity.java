package com.example.staffprofile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.staffprofile.Interface.ItemClickListener;
import com.example.staffprofile.Model.NTStaff;
import com.example.staffprofile.Model.Staff;
import com.example.staffprofile.ViewHolder.NTStaffViewHolder;
import com.example.staffprofile.ViewHolder.StaffViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    //Search Box
    FirebaseRecyclerAdapter<Staff, StaffViewHolder> searchAdapter;
    FirebaseRecyclerAdapter<NTStaff, NTStaffViewHolder> ntsearchAdapter;

    List<String> suggestList = new ArrayList<>();
    MaterialSearchBar materialSearchBar;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference aidedList, unaidedList, ntaidedList, ntunaidedList;


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

        setContentView(R.layout.activity_search);

        database = FirebaseDatabase.getInstance();
        aidedList = database.getReference("Aided");
        unaidedList = database.getReference("UnAided");
        ntaidedList = database.getReference("NTAided");
        ntunaidedList = database.getReference("NTUnAided");

        recyclerView = findViewById(R.id.recycler_search);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);



        //Search Box
        materialSearchBar = findViewById(R.id.item_search);
        materialSearchBar.setHint("Search...");
        loadSuggest();
        materialSearchBar.setCardViewElevation(5);


        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //When the user type , we suggest search list
                List<String> suggest = new ArrayList<>();
                for(String search:suggestList)// loop in suggest list
                {
                    if (search.toLowerCase().contains(materialSearchBar.getText().toLowerCase()))
                        suggest.add(search);
                }
                materialSearchBar.setLastSuggestions(suggest);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                //when search bar is close
                //Restore original suggest adapter
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                //When Search Finish
                //Show result of search adapter
                startSearch(text);

            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });


    }

    private void loadSuggest() {
        aidedList.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot:dataSnapshot.getChildren()){

                    Staff items = postSnapshot.getValue(Staff.class);
                    suggestList.add(items.getName());// Add name of item to suggest list
                }
                materialSearchBar.setLastSuggestions(suggestList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        unaidedList.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot:dataSnapshot.getChildren()){

                    Staff items = postSnapshot.getValue(Staff.class);
                    suggestList.add(items.getName());// Add name of item to suggest list
                }
                materialSearchBar.setLastSuggestions(suggestList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        ntaidedList.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot:dataSnapshot.getChildren()){

                    Staff items = postSnapshot.getValue(Staff.class);
                    suggestList.add(items.getName());// Add name of item to suggest list
                }
                materialSearchBar.setLastSuggestions(suggestList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        ntunaidedList.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot:dataSnapshot.getChildren()){

                    Staff items = postSnapshot.getValue(Staff.class);
                    suggestList.add(items.getName());// Add name of item to suggest list
                }
                materialSearchBar.setLastSuggestions(suggestList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void startSearch(CharSequence text) {
        //compare name
        //AidedTeachingStaff
        Query Taided = aidedList.orderByChild("name").startAt(text+"").endAt(text + "\uf8ff");
        FirebaseRecyclerOptions<Staff> TOptions = new FirebaseRecyclerOptions.Builder<Staff>().setQuery(Taided, Staff.class).build();
        searchAdapter = new FirebaseRecyclerAdapter<Staff, StaffViewHolder>(TOptions){
            @Override
            protected void onBindViewHolder(@NonNull StaffViewHolder holder, int position, @NonNull Staff model) {
                holder.staffName.setText(model.getName());
                holder.staffPost.setText(model.getPost());

                Picasso.get().load(model.getImage()).into(holder.staffImage);

                final Staff clickCategoryItem = model;
                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //Because category Id is key, so we just get key of
                        Intent itemDetail = new Intent(getApplicationContext(), AidedStaffDetail.class);
                        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("MyPrefStaffId",0);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("StaffId", searchAdapter.getRef(position).getKey());
                        editor.commit();
                        startActivity(itemDetail);

                        //Toast.makeText(getApplicationContext(),""+clickCategoryItem.getName()+" Add to cart" ,Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override
            public StaffViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
                View view1 = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.staff_list, viewGroup, false);
                return  new StaffViewHolder(view1);
            }


        } ;

        //UnAidedTeachingStaff
        Query Tunaided = unaidedList.orderByChild("name").startAt(text+"").endAt(text + "\uf8ff");
        FirebaseRecyclerOptions<Staff> options1 = new FirebaseRecyclerOptions.Builder<Staff>().setQuery(Tunaided, Staff.class).build();
        searchAdapter = new FirebaseRecyclerAdapter<Staff, StaffViewHolder>(options1){
            @Override
            protected void onBindViewHolder(@NonNull StaffViewHolder holder, int position, @NonNull Staff model) {
                holder.staffName.setText(model.getName());
                holder.staffPost.setText(model.getPost());

                Picasso.get().load(model.getImage()).into(holder.staffImage);

                final Staff clickCategoryItem = model;
                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //Because category Id is key, so we just get key of
                        Intent itemDetail = new Intent(getApplicationContext(), UnAidedStaffDetail.class);
                        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("MyPrefUnStaffId",0);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("UnStaffId", searchAdapter.getRef(position).getKey());
                        editor.commit();
                        startActivity(itemDetail);

                        //Toast.makeText(getApplicationContext(),""+clickCategoryItem.getName()+" Add to cart" ,Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override
            public StaffViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
                View view1 = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.staff_list, viewGroup, false);
                return  new StaffViewHolder(view1);
            }


        } ;

        Query NTaided = aidedList.orderByChild("name").startAt(text+"").endAt(text + "\uf8ff");
        FirebaseRecyclerOptions<NTStaff> TUnOptions =
                new FirebaseRecyclerOptions.Builder<NTStaff>()
                        .setQuery(NTaided, NTStaff.class)
                        .build();

        ntsearchAdapter  = new FirebaseRecyclerAdapter<NTStaff, NTStaffViewHolder>(TUnOptions) {
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
                        if (getApplicationContext().checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
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

        //NT
        ntsearchAdapter.startListening();
        recyclerView.setAdapter(ntsearchAdapter);

        //T
        searchAdapter.startListening();
        recyclerView.setAdapter(searchAdapter);
    }


}
