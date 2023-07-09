package com.example.anonchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private TextView tvChatNickname;
    private TextView tvGroupName;

    private LinearLayout linearLayout;
    private ScrollView scrollView;

    private Button btnSend;
    private EditText edtMessage;

    String groupName;
    String nickname;

    static Handler handler = null;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DatabaseHandler databaseHandler = new DatabaseHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Context context = getApplicationContext();

        btnSend = findViewById(R.id.btnSend);
        edtMessage = findViewById(R.id.edtMessage);

        scrollView = findViewById(R.id.scrollView);
        linearLayout = findViewById(R.id.chatLayout);

        tvGroupName = findViewById(R.id.tvGroupName);

        nickname = FileUtility.getNickname(context);
        groupName = getIntent().getStringExtra("groupName");

        displayGroupName();
        setupButtonSend();
        databaseHandler.getAllMessages(groupName, nickname, scrollView, linearLayout, context);
//        displayMessages(groupName);
        scrollToBottom();

        reload();
    }


    private void setupButtonSend() {

        btnSend.setOnClickListener(view -> {

            String message = edtMessage.getText().toString();
            databaseHandler.sendMessage(groupName, nickname, message);

            edtMessage.setText("");
            /* hide keyboard after button is pressed */
            View v = this.getCurrentFocus();
            if (v != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
            /* after message is send scroll to bottom */
        });
    }

    public void reload() {
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                databaseHandler.getAllMessages(groupName, nickname, scrollView, linearLayout, getApplicationContext());
                reload();
            }
        }, 500); // run reload every half a second (getMessages)
    }

    private void displayGroupName() {
        if (groupName != null) {
            tvGroupName.setText(groupName);
        } else {
            tvGroupName.setText(R.string.unknown_group);
        }
    }

    private void scrollToBottom() {
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
    }



}