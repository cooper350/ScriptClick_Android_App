package com.example.mainscreen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class troupe_settings extends AppCompatActivity {

    private TroupeAdapter mAdapter;
    private ListView mMemberList;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_troupe_settings);

        ImageView backArrow = findViewById(R.id.arrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mMemberList = findViewById(R.id.memberList);

        // Retrieve the troupe ID from the intent
        int troupeId = getIntent().getIntExtra("troupeId", 0);

        // Initialize the Firebase database reference
        mDatabase = FirebaseDatabase.getInstance("https://scriptclickblue-8e8be-default-rtdb.firebaseio.com/").getReference();

        // Fetch troupe members based on the troupe ID
        fetchTroupeMembers(troupeId);
    }

    private void fetchTroupeMembers(int troupeId) {

        // Fetch the director's name
        mDatabase.child("troupes").child("troupe" + troupeId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String director = dataSnapshot.child("director").getValue(String.class);
                String tname = dataSnapshot.child("name").getValue(String.class);

                // Set the director's name to the box4 TextView
                TextView box3 = findViewById(R.id.box3);
                TextView box2 = findViewById(R.id.box2);
                TextView box1 = findViewById(R.id.box1);
                box3.setText("Director: " + director);
                box2.setText("Troupe ID: " + troupeId);
                box1.setText("Troupe Name: " + tname);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });

        mDatabase.child("troupes").child("troupe" + troupeId).child("members").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                LoggedInUser loggedInUser = LoginRepository.getInstance(new LoginDataSource()).getLoggedInUser();
                ArrayList<String> members = new ArrayList<>();

                for (DataSnapshot memberSnapshot : dataSnapshot.getChildren()) {
                    String name = memberSnapshot.child("name").getValue(String.class);
                    String role = memberSnapshot.child("role").getValue(String.class);
                    members.add(name + ":" + role);
                }

                mAdapter = new TroupeAdapter(troupe_settings.this, members);
                mMemberList.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
