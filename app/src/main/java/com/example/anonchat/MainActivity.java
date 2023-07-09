package com.example.anonchat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private EditText edtNickname;
    private Button btnSubmit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Context context = getApplicationContext();

        edtNickname = findViewById(R.id.edtNickname);
        btnSubmit = findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(view -> {
            try {
                FileUtility.writeToFile(FileUtility.nicknameFile, edtNickname.getText().toString(), context);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            Intent intent = new Intent(MainActivity.this, GroupActivity.class);
            MainActivity.this.startActivity(intent);

        });
    }
}