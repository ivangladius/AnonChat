package com.example.anonchat;

import androidx.annotation.NonNull;

public class Message {
    public String nickname;
    public String message;

    public Message(String nickname, String message) {
        this.nickname = nickname;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String getNickname() {
        return nickname;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @NonNull
    @Override
    public String toString() {
        return nickname + " : " + message;
    }
}
