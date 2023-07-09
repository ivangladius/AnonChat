package com.example.anonchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupAddActivity extends AppCompatActivity {

    private EditText edtGroupAddName;
    private Button btnSubmit;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_add);

        Context context = getApplicationContext();

        edtGroupAddName = findViewById(R.id.edtGroupAddName);
        btnSubmit = findViewById(R.id.btnSubmitGroupName);


        btnSubmit.setOnClickListener(view -> {
            Map<String, Object> group = new HashMap<>();
            group.put("name", edtGroupAddName.getText().toString());
            group.put("members", new ArrayList<String>());
            group.put("messages", new ArrayList<Message>());

            db.collection("groups")
                    .add(group)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d("XLOG", "Snapshot added: " + documentReference.getId());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("XLOG", "Snapshot failed: " + e);
                        }
                    });

            db.collection("groups")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult())
                                    Log.d("XLOG", document.getId() + " => " + document.getString("name"));
                            } else {
                                Log.w("XLOG", "Error getting documents " + task.getException());
                            }
                        }
                    });

            Intent intent = new Intent(GroupAddActivity.this, GroupActivity.class);
            GroupAddActivity.this.startActivity(intent);
        });


    }
}