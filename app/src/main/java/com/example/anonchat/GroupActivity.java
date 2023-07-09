package com.example.anonchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GroupActivity extends AppCompatActivity {

    private Button btnSettings;
    private Button btnGroupAdd;

    RecyclerView recyclerView;

    ArrayList<GroupModel> groupModels = new ArrayList<>();

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        Context context = getApplicationContext();

        if (FileUtility.isNicknameEmpty()) {
            Intent intent = new Intent(GroupActivity.this, MainActivity.class);
            GroupActivity.this.startActivity(intent);
        }

        recyclerView = findViewById(R.id.mGroupView);

        btnSettings = findViewById(R.id.btnSettings);
        btnGroupAdd = findViewById(R.id.btnGroupAdd);


        gatherGroupNames();

        btnGroupAdd.setOnClickListener(view -> {
            Intent intent = new Intent(GroupActivity.this, GroupAddActivity.class);
            GroupActivity.this.startActivity(intent);
        });

        btnSettings.setOnClickListener(view -> {
            Intent intent = new Intent(GroupActivity.this, Settings.class);
            GroupActivity.this.startActivity(intent);
        });
    }

    public void displayModels(RecyclerView recyclerView) {
        GroupViewAdapter groupViewAdapter
                = new GroupViewAdapter(this, groupModels);

        recyclerView.setAdapter(groupViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void gatherGroupNames() {
        db.collection("groups")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String groupName = document.getString("name");
                                groupModels.add(new GroupModel(groupName));
                                displayModels(recyclerView); // display all Groups when data successfully arrived
                            }
                        }
                    }
                });
    }
}