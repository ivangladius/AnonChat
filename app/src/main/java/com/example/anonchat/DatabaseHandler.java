package com.example.anonchat;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class DatabaseHandler {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private int numberOfMessages = 0;

    public CompletableFuture<String> findIdByGroupName(String groupName) {

        CompletableFuture<String> future = new CompletableFuture<>();

        db.collection("groups")
                .whereEqualTo("name", groupName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String documentId = document.getId();
                                future.complete(documentId);
                            }
                        } else {
                            Log.d("XLOG", "Error getting documents: " + task.getException());
                        }
                        future.complete(null);
                    }
                });
        return future;
    }

    public void sendMessage(String groupName, String nickname, String text) {

        findIdByGroupName(groupName)
                .thenAccept(documentId -> {
                    if (documentId != null) {

                        Message message = new Message(nickname, text);

                        if (documentId != null) {
                            db.collection("groups").document(documentId)
                                    .update("messages", FieldValue.arrayUnion(message))
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("XLOG", "Message added to 'messages' array");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e("XLOG", "Failed to add message to 'messages' array: " + e.getMessage());
                                        }
                                    });
                        }
                    }
                });
    }

//    public CompletableFuture<List<Message>> getAllMessages(String groupName) {
//        CompletableFuture<List<Message>> future = new CompletableFuture<>();
//
//        findIdByGroupName(groupName)
//                .thenAccept(documentId -> {
//                    if (documentId != null) {
//                        db.collection("groups").document(documentId).collection("messages")
//                                .get()
//                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                        if (task.isSuccessful()) {
//                                            List<Message> messages = new ArrayList<>();
//
//                                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                                // Retrieve the message data from the document
//                                                String sender = document.getString("sender");
//                                                String text = document.getString("text");
//
//                                                // Create a Message object
//                                                Message message = new Message(sender, text);
//
//                                                // Add the Message object to the list
//                                                messages.add(message);
//                                            }
//
//                                            // Complete the CompletableFuture with the list of messages
//                                            future.complete(messages);
//                                        } else {
//                                            Log.d("XLOG", "Error getting documents: " + task.getException());
//                                            // Complete the CompletableFuture with a failure
//                                            future.completeExceptionally(task.getException());
//                                        }
//                                    }
//                                });
//                    } else {
//                        // Complete the CompletableFuture with an empty list of messages
//                        future.complete(Collections.emptyList());
//                    }
//                });
//
//        return future;
//    }

//    public void getAllMessages(String groupName, LinearLayout linearLayout, Context context) {
//
//        Log.d("XLOG", "called getAllMessages()");
//
//        findIdByGroupName(groupName)
//                .thenAccept(documentId -> {
//                    if (documentId != null) {
//
//                        db.collection("groups").document(documentId).collection("messages")
//                                .get()
//                                .addOnCompleteListener(task -> {
//                                    if (task.isSuccessful()) {
//                                        for (QueryDocumentSnapshot document : task.getResult()) {
//                                            String sender = document.getString("nickname");
//                                            String text = document.getString("message");
//
//                                            // Create a Message object or process the message as needed
//                                            Message message = new Message(sender, text);
//
//                                            createChatText(message.getMessage(), linearLayout, context);
//                                            createChatText(" ", linearLayout, context);
//
//                                            Log.d("XLOG", "Message - Sender: " + message.getNickname() + ", Text: " + message.getMessage());
//                                        }
//                                    } else {
//                                        Log.d("XLOG", "Error getting documents: " + task.getException());
//                                    }
//                                });
//                    } else {
//                        Log.d("XLOG", "DOCUMENT ID IS NULL");
//                    }
//                });
//    }

    public void getAllMessages(String groupName, String currentUserNickname, ScrollView scrollView, LinearLayout linearLayout, Context context) {

        findIdByGroupName(groupName)
                .thenAccept(documentId -> {
                    if (documentId != null) {
                        db.collection("groups").document(documentId)
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        if (documentSnapshot.exists()) {
                                            List<HashMap<String, String>> messagesData = (List<HashMap<String, String>>) documentSnapshot.get("messages");

                                            List<Message> messages = new ArrayList<>();
                                            for (HashMap<String, String> messageData : messagesData) {
                                                String nickname = messageData.get("nickname");
                                                String messageText = messageData.get("message");
                                                Message message = new Message(nickname, messageText);
                                                messages.add(message);
                                            }

                                            linearLayout.removeAllViews();

                                            for (Message message : messages) {
                                                createChatText(message.getMessage(), currentUserNickname, message.nickname, linearLayout, context);
                                            }

                                            /* only scroll down if new messages arrived */
                                            int _numberOfMessages = messages.size();
                                            if (numberOfMessages != _numberOfMessages)
                                                scrollToBottom(scrollView);
                                            numberOfMessages = _numberOfMessages;


                                            // Do something with the "messages" list
                                        } else {
                                            Log.d("XLOG", "Document does not exist");
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("XLOG", "Error getting document: " + e);
                                    }
                                });
                    }
                });
    }

    public void createChatText(String text, String currentUserNickname, String nickname, LinearLayout linearLayout, Context context) {

        TextView nicknameText = new TextView(context);
        TextView emptyText = new TextView(context);
        TextView messageText = new TextView(context);

        // show users text on the left, and others text on the right side
        if (currentUserNickname.equals(nickname)) {
            nicknameText.setGravity(Gravity.RIGHT);
            messageText.setGravity(Gravity.RIGHT);
        } else {
            nicknameText.setGravity(Gravity.LEFT);
            messageText.setGravity(Gravity.LEFT);
        }

        nicknameText.setTextSize(25);
        nicknameText.setTypeface(null, Typeface.BOLD);
        nicknameText.setTextColor(Color.YELLOW);
        nicknameText.setText(nickname + " : ");
        linearLayout.addView(nicknameText);

        messageText.setTextSize(20);
        messageText.setTextColor(Color.WHITE);
        messageText.setTypeface(null, Typeface.ITALIC);
        messageText.setText(text);
        linearLayout.addView(messageText);

        emptyText.setTextSize(20);
        emptyText.setTypeface(null, Typeface.BOLD);
        emptyText.setText("");
        linearLayout.addView(emptyText);
    }

    private void scrollToBottom(ScrollView scrollView) {
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
    }
}
